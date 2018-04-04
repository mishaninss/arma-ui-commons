/*
 * Copyright 2018 Sergey Mishanin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mishaninss.html.containers;

import com.github.mishaninss.data.CsvDataExtractor;
import com.github.mishaninss.data.UiCommonsProperties;
import com.github.mishaninss.exceptions.ContainerInitException;
import com.github.mishaninss.html.elements.ArmaElement;
import com.github.mishaninss.html.composites.IndexedElement;
import com.github.mishaninss.html.composites.TemplatedElement;
import com.github.mishaninss.html.containers.annotations.*;
import com.github.mishaninss.html.containers.annotations.Optional;
import com.github.mishaninss.html.containers.interfaces.IBatchElementsContainer;
import com.github.mishaninss.html.containers.interfaces.IHaveUrl;
import com.github.mishaninss.html.containers.table.Column;
import com.github.mishaninss.html.containers.table.Table;
import com.github.mishaninss.html.containers.table.annotations.IColumn;
import com.github.mishaninss.html.containers.table.annotations.IContextualColumn;
import com.github.mishaninss.html.containers.table.annotations.IContextualTable;
import com.github.mishaninss.html.containers.table.annotations.ITable;
import com.github.mishaninss.html.interfaces.*;
import com.github.mishaninss.html.listeners.*;
import com.github.mishaninss.html.readers.AbstractReader;
import com.github.mishaninss.html.readers.NoopReader;
import com.github.mishaninss.uidriver.interfaces.IFrame;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.utils.ReflectionUtils;
import com.github.mishaninss.utils.UrlUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

/**
 * Provides mechanism for creating and initializing of container instances
 */
@SuppressWarnings("unused")
@Component
public class ContainersFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainersFactory.class);
    private static final String EXCEPTION_EMPTY_ELEMENT_ID = "element ID cannot be null or blank string";
    private static final String EXCEPTION_NO_LOCATORS = "Cannot initialize controller for element [%s > %s]. At least one locator should be provided.";
    private static final String EXCEPTION_LOCATORS_FILE_NOT_FOUND = "Cannot initialize container [%s]. Locators file [%s] was not found.";
    private static final String EXCEPTION_INIT_FAILURE = "Cannot initialize container [%s]";
    private static final String EXCEPTION_RESET_FAILURE = "Cannot reset container [%s]";
    private final List<IElementEventHandler> defaultListeners = new ArrayList<>();
    private static final ThreadLocal<Map<Class<?>, Object>> CONTAINERS = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<ContainersFactory> INSTANCES = new ThreadLocal<>();

    @Autowired
    private UiCommonsProperties uiCommonsProperties;
    @Autowired
    private Environment env;
    @Autowired
    private UrlUtils urlUtils;
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    private void init(){
        defaultListeners.add(applicationContext.getBean(WaitingEventHandler.class));
        defaultListeners.add(applicationContext.getBean(LoggingEventHandler.class));
        defaultListeners.add(applicationContext.getBean(ScrollingEventHandler.class));
        if (uiCommonsProperties.framework().debugMode) {
            defaultListeners.add(applicationContext.getBean(HighlightEventHandler.class));
        }
        INSTANCES.set(this);
    }

    @PreDestroy
    private void destroy(){
        INSTANCES.remove();
    }

    private ContainersFactory(){}

    public static ContainersFactory get(){
        return INSTANCES.get();
    }

    public void resetAll(){
        CONTAINERS.get().keySet().forEach(clazz -> {
            try{
                resetContainer(clazz);
            } catch(Exception ex){
                LOGGER.error(ex.getMessage());
                String message = String.format("Cannot reset container [%s]", clazz.getName());
                throw new ContainerInitException(message, ex);
            }
        });
    }

    private void resetContainer(Class<?> clazz) {
        Object container = CONTAINERS.get().get(clazz);
        if (container == null){
            initContainer(clazz);
        } else {
            if (Table.class.isAssignableFrom(clazz)) {
                resetTable((Table) container);
            } else {
                resetContainer((IElementsContainer) container);
            }
        }
    }

    private void resetTable(Table currentInstance){
        Class<? extends Table> clazz = currentInstance.getClass();
        try {
            Table cleanInstance = clazz.getConstructor().newInstance();
            List<Field> columnFields = FieldUtils.getFieldsListWithAnnotation(clazz, IColumn.class);
            for (Field columnField : columnFields) {
                if (Modifier.isStatic(columnField.getModifiers())) {
                    Column<? extends IInteractiveElement> cleanElement = (Column<? extends IInteractiveElement>) FieldUtils.readStaticField(columnField, true);
                    FieldUtils.writeStaticField(columnField, cleanElement);
                } else {
                    Column<? extends IInteractiveElement> cleanElement = (Column<? extends IInteractiveElement>) FieldUtils.readField(columnField, cleanInstance, true);
                    FieldUtils.writeField(columnField, currentInstance, cleanElement);
                }
            }
            initTable(currentInstance);
        } catch (Exception ex){
            throw getException(ex, EXCEPTION_RESET_FAILURE, clazz.getName());
        }
    }

    private void resetContainer(IElementsContainer currentInstance){
        Class<? extends IElementsContainer> clazz = currentInstance.getClass();
        try {
            IElementsContainer cleanInstance = clazz.getConstructor().newInstance();
            List<Field> controllerFields = new ArrayList<>(Arrays.asList(FieldUtils.getFieldsWithAnnotation(clazz, Element.class)));
            controllerFields.addAll(Arrays.asList(FieldUtils.getFieldsWithAnnotation(clazz, ContextualElement.class)));
            for (Field controllerField : controllerFields) {
                if (Modifier.isStatic(controllerField.getModifiers())) {
                    IInteractiveElement cleanElement = (IInteractiveElement) FieldUtils.readStaticField(controllerField, true);
                    FieldUtils.writeStaticField(controllerField, cleanElement);
                } else {
                    IInteractiveElement cleanElement = (IInteractiveElement) FieldUtils.readField(controllerField, cleanInstance, true);
                    FieldUtils.writeField(controllerField, currentInstance, cleanElement);
                }
            }
            initContainer(currentInstance);
        } catch (Exception ex){
            throw getException(ex, EXCEPTION_RESET_FAILURE, clazz.getName());
        }
    }

    /**
     * Provides an instance of container of a given class.
     * If an instance of a given class has been already created, method will return it.
     * Otherwise a new instance will be created and initialized.
     * @param clazz a Page Object class
     * @return an instance of Page Object class
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> T initContainer(Class clazz){
        Preconditions.checkNotNull(clazz, "clazz value cannot be null");
        try {
            T instance = (T) CONTAINERS.get().get(clazz);
            if (instance == null){
                instance = (T) clazz.getConstructor().newInstance();
                CONTAINERS.get().put(clazz, instance);
                if (Table.class.isAssignableFrom(clazz)) {
                    initTable((Table) instance);
                } else {
                    initContainer((IElementsContainer) instance);
                }
                if (instance instanceof IndexedContainer){
                    ((IndexedContainer)instance).wrap((IBatchElementsContainer) instance);
                }
            }
            return instance;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw getException(e, EXCEPTION_INIT_FAILURE, clazz.getName());
        }
    }

    /**
     * Provides an instance of container by a given class name.
     * If an instance of a given class has been already created, method will return it.
     * Otherwise a new instance will be created and initialized.
     * @param className the canonical name of a Page Object class
     * @return an instance of Page Object class
     */
    public synchronized <T> T initContainer(String className){
        Class clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw getException(e, "Unknown container name [%s]", className);
        }
        return initContainer(clazz);
    }

    /**
     * Performs initializing of a given container instance.
     * Initializing includes mapping of element controllers and element instances,
     * resolving element parameters, specified by annotations.
     * @param instance - an instance of Page Object class
     */
    public void initContainer(IElementsContainer instance) {
        try {
            setContainerName(instance);
            setContainerLocator(instance);
            setContainerUrl(instance);

            Map<String, List<String>> loadedLocators = loadLocators(instance);
            resolveElementsMapping(instance, loadedLocators);

            checkIfNested(instance);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw getException(ex, EXCEPTION_INIT_FAILURE, instance.getClass().getName());
        }
    }

    private Container getContainerProps(IElementsContainer instance){
        Class<? extends IElementsContainer> clazz = instance.getClass();
        if (clazz.isAnnotationPresent(ContextualContainer.class)){
            ContextualContainer contextualContainer = clazz.getAnnotation(ContextualContainer.class);
            Container[] containers = contextualContainer.value();
            Container defaultParameters = null;
            for (Container container: containers){
                String property = container.prop();
                String expectedValue = container.val();
                String actualValue = env.getProperty(property, "");
                if (StringUtils.isNoneBlank(actualValue) && actualValue.equals(expectedValue)){
                    return container;
                } else if (defaultParameters == null && StringUtils.isBlank(expectedValue)){
                    defaultParameters = container;
                }
            }
            return defaultParameters;
        } else if (clazz.isAnnotationPresent(Container.class)){
            return clazz.getAnnotation(Container.class);
        }
        return null;
    }

    private Url getUrlProps(IElementsContainer instance){
        Class<? extends IElementsContainer> clazz = instance.getClass();
        if (clazz.isAnnotationPresent(ContextualUrl.class)){
            ContextualUrl contextualUrl = clazz.getAnnotation(ContextualUrl.class);
            Url[] urls = contextualUrl.value();
            Url defaultParameters = null;
            for (Url url: urls){
                String property = url.prop();
                String expectedValue = url.val();
                String actualValue = env.getProperty(property, "");
                if (StringUtils.isNoneBlank(actualValue) && actualValue.equals(expectedValue)){
                    return url;
                } else if (defaultParameters == null && StringUtils.isBlank(expectedValue)){
                    defaultParameters = url;
                }
            }
            return defaultParameters;
        } else if (clazz.isAnnotationPresent(Url.class)){
            return clazz.getAnnotation(Url.class);
        }
        return null;
    }

    private Element getElementProps(Field elementField){
        if (elementField.isAnnotationPresent(ContextualElement.class)){
            ContextualElement contextualElement = elementField.getAnnotation(ContextualElement.class);
            Element[] elements = contextualElement.value();
            Element defaultParameters = null;
            for (Element element: elements){
                String property = element.prop();
                String expectedValue = element.val();
                if (Element.PROFILE_PROPERTY.equals(property)
                        && ArrayUtils.contains(env.getActiveProfiles(), expectedValue)) {
                    return element;
                } else if (StringUtils.equals(env.getProperty(property), expectedValue)) {
                    return element;
                } else if (defaultParameters == null && StringUtils.isBlank(expectedValue)){
                    defaultParameters = element;
                }
            }
            return defaultParameters;
        } else if (elementField.isAnnotationPresent(Element.class)){
            return elementField.getAnnotation(Element.class);
        }
        return null;
    }

    private void setContainerName(IElementsContainer instance){
        Class<? extends IElementsContainer> clazz = instance.getClass();
        if (instance instanceof INamed) {
            String name = null;
            Container containerProps = getContainerProps(instance);
            if (containerProps != null) {
                name = containerProps.name().trim();
            }
            if (StringUtils.isBlank(name)) {
                name = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(clazz.getSimpleName()), " ");
            }
            ((INamed)instance).setName(name);
        }
    }

    private void setContainerLocator(IElementsContainer instance){
        Container containerProps = getContainerProps(instance);
        if (containerProps != null){
            String locator = containerProps.value();
            if (StringUtils.isBlank(locator)) {
                locator = containerProps.locator();
            }
            if (StringUtils.isNoneBlank(locator)) {
                instance.setLocator(locator);
            }
        }
    }

    private void setContainerUrl(IElementsContainer instance) {
        if (instance instanceof IHaveUrl) {
            String url = null;
            Url urlProps = getUrlProps(instance);
            if (urlProps != null){
                url = urlProps.value().trim();
            }
            if (!StringUtils.isBlank(url)){
                url = urlUtils.resolveUrl(url);
                ((IHaveUrl)instance).setUrl(url);
            }
        }
    }

    private void checkIfNested(IElementsContainer instance) {
        Class<?> clazz = instance.getClass();
        if (clazz.isAnnotationPresent(Nested.class)) {
            Class<?> declaringClass = clazz.getDeclaringClass();
            if (declaringClass != null && ArmaContainer.class.isAssignableFrom(declaringClass)){
                ILocatable context = initContainer(declaringClass);
                instance.setContext(context);
                if (context instanceof IFrame){
                    instance.setContextLookup(false);
                }
            }
        }
    }

    private Map<String, List<String>> loadLocators(IElementsContainer instance) throws IOException {
        Class<? extends IElementsContainer> clazz = instance.getClass();
        Map<String, List<String>> loadedLocators = new LinkedHashMap<>();
        Container containerProps = getContainerProps(instance);
        if (containerProps != null) {
            String locatorsFilePath = containerProps.locators();
            if (StringUtils.isNoneBlank(locatorsFilePath)){
                try {
                    loadedLocators = CsvDataExtractor.extractData(locatorsFilePath);
                } catch (IOException ex){
                    String message = String.format(EXCEPTION_LOCATORS_FILE_NOT_FOUND, ((INamed) instance).getName(), locatorsFilePath);
                    throw new ContainerInitException(message, ex);
                }
            } else {
                locatorsFilePath = clazz.getName().replace(".", "/") + ".csv";
                URL locatorsUrl = clazz.getClassLoader().getResource(locatorsFilePath);
                if (locatorsUrl != null){
                    loadedLocators = CsvDataExtractor.extractData(locatorsUrl);
                }
            }
        }
        return loadedLocators;
    }
    
    private void resolveElementsMapping(IElementsContainer container, Map<String, List<String>> loadedLocators) throws IllegalAccessException {
        Class<?> clazz = container.getClass();
        
        Map<String, IInteractiveElement> elements = new LinkedHashMap<>();
        
        List<Class> elementIdsClasses = ReflectionUtils.getInnerClassesWithAnnotation(clazz, ElementIds.class);
        List<Field> controllerFields = FieldUtils.getFieldsListWithAnnotation(clazz, Element.class);
        controllerFields.addAll(FieldUtils.getFieldsListWithAnnotation(clazz, ContextualElement.class));

        //Map element controllers with explicitly provided IDs
        for(Class<?> elementIdsClass: elementIdsClasses){
            elements.putAll(resolveElementIdsClassMapping(elementIdsClass, controllerFields, container, loadedLocators));
        }

        //Map element controllers without explicitly provided IDs
        elements.putAll(mapElementsWithoutExplicitId(controllerFields, container, loadedLocators));
        
        if (MapUtils.isNotEmpty(elements)) {
            container.addElements(elements);
        }
    }
    
    private Map<String, IInteractiveElement> resolveElementIdsClassMapping(Class<?> elementIdsClass, List<Field> controllerFields, IElementsContainer container, Map<String, List<String>> loadedLocators) throws IllegalAccessException {
        Map<String, IInteractiveElement> elements = new LinkedHashMap<>();

        Field[] idFields = elementIdsClass.getFields();

        for (Field idField : idFields) {
            String idFieldName = idField.getName();

            Object elementIdObj = FieldUtils.readStaticField(idField, true);
            String elementId;
            if (elementIdObj != null) {
                elementId = elementIdObj.toString();
            } else {
                elementId = idFieldName;
                FieldUtils.writeStaticField(idField, elementId);
            }

            IInteractiveElement element = getMappedElement(idFieldName, elementId, controllerFields, container, loadedLocators.get(elementId));

            if (element != null) {
                elements.put(sanitizeElementId(elementId), element);
            }
        }
        return elements;
    }

    private IInteractiveElement getMappedElement(String idFieldName, String elementId, List<Field> controllerFields, IElementsContainer container, List<String> loadedLocators) {
        for (Field controllerField : controllerFields) {
            if (controllerField.isAnnotationPresent(Element.class) &&
                    controllerField.getAnnotation(Element.class).id().equals(elementId) ||
                    controllerField.getName().equalsIgnoreCase(idFieldName)) {

                IInteractiveElement element = initElement(elementId, controllerField, container, loadedLocators);

                controllerFields.remove(controllerField);
                return element;
            }
        }
        return null;
    }

    private Map<String, IInteractiveElement> mapElementsWithoutExplicitId(List<Field> controllerFields, IElementsContainer container, Map<String, List<String>> loadedLocators) {
        Map<String, IInteractiveElement> elements = new LinkedHashMap<>();

        for (Field controllerField : controllerFields) {
            String elementId = null;
            Element elementProps = getElementProps(controllerField);
            if (elementProps != null) {
                elementId = elementProps.id();
            }
            if (StringUtils.isBlank(elementId)){
                elementId = controllerField.getName();
            }

            IInteractiveElement element = initElement(elementId, controllerField, container, loadedLocators.get(elementId));
            elements.put(sanitizeElementId(elementId), element);
        }

        return elements;
    }

    /**
     * Performs initializing of an element controller:
     * - creates a controller instance if it was not explicitly created;
     * - assigns locator if it was not specified explicitly;
     * - sets name of element;
     * - sets dynamic mark;
     * - sets context lookup mark.
     * @param elementId - internal ID of an element
     * @param controllerField - field of an element controller
     * @param container - instance of an element's container
     * @param locators - locators, loaded from an external source;
     * @return and instance of Interactive Element
     */
    private IInteractiveElement initElement(String elementId, Field controllerField, IElementsContainer container, List<String> locators) {
        //Get element controller
        IInteractiveElement element = readElementInstance(controllerField, container);

        //If element controller was not explicitly instantiated try to create an instance
        if (element == null){
            List<String> elementLocators = locators;
            if (CollectionUtils.isEmpty(elementLocators)){
                elementLocators = getElementLocators(controllerField);
                if (CollectionUtils.isEmpty(elementLocators)){
                    throw getException(EXCEPTION_NO_LOCATORS, ((INamed)container).getName(), elementId);
                }
            }

            Class<? extends IInteractiveElement> controllerClass = getControllerClass(controllerField);
            element = createElementController(controllerClass, elementLocators);
        } else if (StringUtils.isBlank(element.getLocator())){
            //If controller was instantiated, but has no assigned locator (e.g. explicitly created without provided locator)
            List<String> elementLocators = locators;
            if (CollectionUtils.isEmpty(elementLocators)){
                elementLocators = getElementLocators(controllerField);
            }
            if (CollectionUtils.isNotEmpty(elementLocators)) {
                element.setLocator(elementLocators.get(0));
            } else {
                throw getException(EXCEPTION_NO_LOCATORS, ((INamed)container).getName(), elementId);
            }
        }

        element.setContext(container);
        addDefaultListeners(element);
        setElementName(element, controllerField);
        setElementDynamicMark(element, controllerField);
        if (container instanceof IFrame) {
            element.setContextLookup(false);
        } else {
            setElementContextLookup(element, controllerField);
        }
        setNextPage(element, controllerField, container);
        setReader(element, controllerField);

        if (controllerField.getType().equals(IndexedElement.class) && !(element instanceof IndexedElement)){
            element = applicationContext.getBean(IndexedElement.class, element);
        } else if (controllerField.getType().equals(TemplatedElement.class) && !(element instanceof TemplatedElement)){
            element = applicationContext.getBean(TemplatedElement.class, element);
        }

        writeElementInstance(controllerField, container, element);

        return element;
    }

    private static IInteractiveElement readElementInstance(Field controllerField, IElementsContainer container){
        Preconditions.checkNotNull(controllerField, "controllerField value cannot be null");
        Preconditions.checkNotNull(container, "container value cannot be null");
        try {
            return Modifier.isStatic(controllerField.getModifiers()) ?
                    (IInteractiveElement) FieldUtils.readStaticField(controllerField, true) :
                    (IInteractiveElement) FieldUtils.readField(controllerField, container, true);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private static void writeElementInstance(Field controllerField, IElementsContainer container, IInteractiveElement element){
        Preconditions.checkNotNull(controllerField, "controllerField value cannot be null");
        Preconditions.checkNotNull(container, "container value cannot be null");
        Preconditions.checkNotNull(element, "element value cannot be null");
        try {
            if (Modifier.isStatic(controllerField.getModifiers())) {
                FieldUtils.writeStaticField(controllerField, element, true);
            }
            else {
                FieldUtils.writeField(controllerField, container, element, true);
            }
        } catch (IllegalAccessException e) {
            throw getException(e, "Couldn't write %s controller field of the %s container", controllerField.getName(), ((INamed)container).getName());
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends IInteractiveElement> getControllerClass(Field controllerField){
        Class<?> fieldType = controllerField.getType();
        Element elementProps = getElementProps(controllerField);
        Class<?> elementType = elementProps != null ? elementProps.type() : ArmaElement.class;
        if (fieldType.equals(IndexedElement.class)
                || fieldType.equals(TemplatedElement.class)
                || fieldType.equals(Column.class)){
            Class<?> genericType = ReflectionUtils.getGenericClass(controllerField.getGenericType(), 0);
            if (genericType != null){
                elementType = genericType;
            }
        } else if (!fieldType.isInterface()){
            elementType = fieldType;
        }

        if (!fieldType.isAssignableFrom(elementType)){
            throw getException("%s field type %s is not compatible with % element type", controllerField.getName(), fieldType.getName(), elementType.getName());
        }
        if (!IInteractiveElement.class.isAssignableFrom(elementType)){
            throw getException("%s field type %s is not compatible with IInteractiveElement type", controllerField.getName(), elementType.getName());
        }
        return (Class<? extends IInteractiveElement>)elementType;
    }

    private IInteractiveElement createElementController(Class<? extends IInteractiveElement> clazz, List<String> locators) {
        return createElementController(clazz, locators.toArray(new String[0]));
    }

    private IInteractiveElement createElementController(Class<? extends IInteractiveElement> clazz, String... locators) {
        return applicationContext.getBean(clazz, locators);
    }

    private void addDefaultListeners(IInteractiveElement element){
        if (element instanceof IListenableElement) {
            IListenableElement listenableElement = ((IListenableElement)element);
            ILocatable context = element.getContext();
            if (context != null && context instanceof IFrame){
                IFrameEventHandler iFrameEventHandler = applicationContext.getBean(IFrameEventHandler.class);
                listenableElement
                        .addEventListener(ElementEvent.CHANGE_VALUE, iFrameEventHandler)
                        .addEventListener(ElementEvent.READ_VALUE, iFrameEventHandler)
                        .addEventListener(ElementEvent.ACTION, iFrameEventHandler)
                        .addEventListener(ElementEvent.IS_DISPLAYED, iFrameEventHandler);
            }
            listenableElement
                    .addEventListeners(ElementEvent.CHANGE_VALUE, defaultListeners)
                    .addEventListeners(ElementEvent.READ_VALUE, defaultListeners)
                    .addEventListeners(ElementEvent.ACTION, defaultListeners)
                    .addEventListeners(ElementEvent.IS_DISPLAYED, defaultListeners);
        }
    }

    private void setElementName(IInteractiveElement element, Field controllerField){
        if (element instanceof INamed) {
            String name = null;
            Element elementProps = getElementProps(controllerField);
            if (elementProps != null) {
                name = elementProps.name().trim();
            }
            if (StringUtils.isBlank(name)) {
                name = StringUtils.capitalize(
                        StringUtils.join(
                                StringUtils.splitByCharacterTypeCamelCase(controllerField.getName()),
                                " ")
                );
            }
            ((INamed) element).setName(name);
        }
    }

    private void setReader(IInteractiveElement element, Field controllerField){
        if (element instanceof ArmaElement) {
            Element elementProps = getElementProps(controllerField);
            if (elementProps != null) {
                Reader readerProps = elementProps.reader();
                Class<? extends AbstractReader> type = readerProps.value();
                if (!Objects.equals(type, NoopReader.class)) {
                    String[] args = readerProps.args();
                    AbstractReader reader = applicationContext.getBean(type, args);
                    ((ArmaElement) element).setReader(reader);
                }
            }
        }
    }

    private List<String> getElementLocators(Field controllerField){
        Element elementProps = getElementProps(controllerField);
        if (elementProps != null) {
            List<String> locators = Arrays.asList(elementProps.locators());
            if (CollectionUtils.isNotEmpty(locators)) {
                return locators;
            }
            locators = new ArrayList<>();
            String locator = elementProps.value();
            if (StringUtils.isBlank(locator)) {
                locator = elementProps.locator();
            }
            if (StringUtils.isNoneBlank(locator)){
                locators.add(locator);
            }
            return locators;
        }
        return new ArrayList<>();
    }

    private void setElementDynamicMark(IInteractiveElement element, Field controllerField){
        if (controllerField.isAnnotationPresent(Optional.class)){
            element.setOptional(true);
        } else {
            Element elementProps = getElementProps(controllerField);
            if (elementProps != null) {
                element.setOptional(elementProps.dynamic());
            }
        }
    }

    private void setElementContextLookup(IInteractiveElement element, Field controllerField){
        boolean useContextLookup = true;
        Element elementProps = getElementProps(controllerField);
        if (elementProps != null){
            useContextLookup = elementProps.contextLookup();
        }
        element.setContextLookup(useContextLookup);
    }

    private void setNextPage(IInteractiveElement element, Field controllerField, IElementsContainer container){
        Element elementProps = getElementProps(controllerField);
        if (elementProps != null) {
            Class<? extends IInteractiveContainer> nextPageClass = elementProps.nextPage();
            if (!nextPageClass.equals(ArmaContainer.class) && IInteractiveContainer.class.isAssignableFrom(nextPageClass)) {
                element.setNextPage(nextPageClass);
                return;
            }
        }
        element.setNextPage((IInteractiveContainer) container);
    }

    static String sanitizeElementId(String elementId){
        Preconditions.checkArgument(StringUtils.isNoneBlank(elementId), EXCEPTION_EMPTY_ELEMENT_ID);
        return elementId.toLowerCase().replace(" ", "");
    }

    private void initTable(Table instance) throws IllegalAccessException {
        Map<String, Column<? extends IInteractiveElement>> namedColumns = new LinkedHashMap<>();
        Map<Integer, Column<? extends IInteractiveElement>> indexedColumns = new LinkedHashMap<>();

        Class<? extends Table> clazz = instance.getClass();
        List<Field> columnFields = FieldUtils.getFieldsListWithAnnotation(clazz, IColumn.class);
        columnFields.addAll(FieldUtils.getFieldsListWithAnnotation(clazz, IContextualColumn.class));

        setTableLocator(instance);

        //Set container's name for logging
        setTableName(instance);

        for (int i=0; i<columnFields.size(); i++){
            Field columnField = columnFields.get(i);
            Column<? extends IInteractiveElement> column =
            Modifier.isStatic(columnField.getModifiers()) ?
                    (Column<? extends IInteractiveElement>) FieldUtils.readStaticField(columnField, true) :
                    (Column<? extends IInteractiveElement>) FieldUtils.readField(columnField, instance, true);
            if (column == null){
                IInteractiveElement cell = createCellInstance(columnField);
                cell.setContext(instance);
                column = applicationContext.getBean(Column.class, cell);
                FieldUtils.writeField(columnField, instance, column);
            }

            String colName = getColumnName(columnField);
            column.setName(colName);
            namedColumns.put(column.getName(), column);

            int colIndex = getColumnIndex(columnField);
            if (colIndex <= 0) {
                colIndex = i + 1;
            }
            column.setColIndex(colIndex);
            indexedColumns.put(column.getColIndex(), column);

            int startRowIndex = getStartRowIndex(columnField);
            if (startRowIndex > 0){
                column.setStartRowIndex(startRowIndex);
            }

            column.setTable(instance);
        }

        instance.setNamedColumns(namedColumns);
        instance.setIndexedColumns(indexedColumns);
    }

    private void setTableLocator(Table instance){
        ITable tableProps = getTableProps(instance.getClass());
        if (tableProps != null){
            String locator = tableProps.locator();
            if (StringUtils.isNoneBlank(locator)){
                instance.setLocator(locator);
            }
        }
    }

    private void setTableName(Table instance){
        String name = null;
        ITable tableProps = getTableProps(instance.getClass());
        if (tableProps != null){
            name = tableProps.name();
        }
        if (StringUtils.isBlank(name)) {
            name = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(instance.getClass().getSimpleName()), " ");
        }
        instance.setName(name);
    }

    private ITable getTableProps(Class<? extends Table> clazz){
        if (clazz.isAnnotationPresent(IContextualTable.class)){
            IContextualTable contextualTable = clazz.getAnnotation(IContextualTable.class);
            ITable[] tablesProps = contextualTable.value();
            ITable defaultParameters = null;
            for (ITable tableProps: tablesProps){
                String property = tableProps.prop();
                String expectedValue = tableProps.val();
                String actualValue = env.getProperty(property, "");
                if (StringUtils.isNoneBlank(actualValue) && actualValue.equals(expectedValue)){
                    return tableProps;
                } else if (defaultParameters == null && StringUtils.isBlank(expectedValue)){
                    defaultParameters = tableProps;
                }
            }
            return defaultParameters;
        } else if (clazz.isAnnotationPresent(ITable.class)){
            return clazz.getAnnotation(ITable.class);
        }
        return null;
    }

    private IColumn getColumnProps(Field columnField){
        if (columnField.isAnnotationPresent(IContextualColumn.class)){
            IContextualColumn contextualColumn = columnField.getAnnotation(IContextualColumn.class);
            IColumn[] columns = contextualColumn.value();
            IColumn defaultParameters = null;
            for (IColumn column: columns){
                String property = column.prop();
                String expectedValue = column.val();
                String actualValue = env.getProperty(property, "");
                if (StringUtils.isNoneBlank(actualValue) && actualValue.equals(expectedValue)){
                    return column;
                } else if (defaultParameters == null && StringUtils.isBlank(expectedValue)){
                    defaultParameters = column;
                }
            }
            return defaultParameters;
        } else if (columnField.isAnnotationPresent(IColumn.class)){
            return columnField.getAnnotation(IColumn.class);
        }
        return null;
    }

    private IInteractiveElement createCellInstance(Field field){
        Class<? extends IInteractiveElement> cellClass = getControllerClass(field);
        List<String> locators = getColumnLocators(field);
        return createElementController(cellClass, locators);
    }

    private String getColumnName(Field columnField){
        String name = null;
        IColumn columnProps = getColumnProps(columnField);
        if (columnProps != null){
            name = columnProps.name();
        }
        if (StringUtils.isBlank(name)) {
            name = StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(columnField.getName()), " "));
        }
        return name;
    }

    private int getColumnIndex(Field columnField){
        int index = 0;
        IColumn columnProps = getColumnProps(columnField);
        if (columnProps != null){
            index = columnProps.index();
        }
        return index;
    }

    private int getStartRowIndex(Field columnField){
        int startRowIndex = 0;
        IColumn columnProps = getColumnProps(columnField);
        if (columnProps != null){
            startRowIndex = columnProps.startIndex();
        }
        return startRowIndex;
    }

    private List<String> getColumnLocators(Field columnField){
        IColumn columnProps = getColumnProps(columnField);
        if (columnProps != null) {
            List<String> locators = Arrays.asList(columnProps.locators());
            if (CollectionUtils.isNotEmpty(locators)) {
                return locators;
            }
            locators = new ArrayList<>();
            String locator = columnProps.locator();
            if (StringUtils.isNoneBlank(locator)){
                locators.add(locator);
            }
            return locators;
        }
        return new ArrayList<>();
    }

    private static ContainerInitException getException(String message, Object... args){
        return new ContainerInitException(String.format(message, args));
    }

    private static ContainerInitException getException(Throwable cause, String message, Object... args){
        return new ContainerInitException(String.format(message, args), cause);
    }
}