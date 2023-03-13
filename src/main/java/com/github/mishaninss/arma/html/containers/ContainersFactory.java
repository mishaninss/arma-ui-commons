package com.github.mishaninss.arma.html.containers;

import com.github.mishaninss.arma.data.CsvDataExtractor;
import com.github.mishaninss.arma.data.DataObject;
import com.github.mishaninss.arma.data.UiCommonsProperties;
import com.github.mishaninss.arma.exceptions.ContainerInitException;
import com.github.mishaninss.arma.html.composites.IndexedElement;
import com.github.mishaninss.arma.html.composites.TemplatedElement;
import com.github.mishaninss.arma.html.containers.annotations.Container;
import com.github.mishaninss.arma.html.containers.annotations.ContextualContainer;
import com.github.mishaninss.arma.html.containers.annotations.ContextualElement;
import com.github.mishaninss.arma.html.containers.annotations.ContextualUrl;
import com.github.mishaninss.arma.html.containers.annotations.Element;
import com.github.mishaninss.arma.html.containers.annotations.ElementIds;
import com.github.mishaninss.arma.html.containers.annotations.Nested;
import com.github.mishaninss.arma.html.containers.annotations.Optional;
import com.github.mishaninss.arma.html.containers.annotations.Url;
import com.github.mishaninss.arma.html.containers.interfaces.IBatchElementsContainer;
import com.github.mishaninss.arma.html.containers.interfaces.IDefaultEventHandlersProvider;
import com.github.mishaninss.arma.html.containers.interfaces.IHaveUrl;
import com.github.mishaninss.arma.html.containers.table.Column;
import com.github.mishaninss.arma.html.containers.table.Table;
import com.github.mishaninss.arma.html.containers.table.annotations.IColumn;
import com.github.mishaninss.arma.html.containers.table.annotations.IContextualColumn;
import com.github.mishaninss.arma.html.containers.table.annotations.IContextualTable;
import com.github.mishaninss.arma.html.containers.table.annotations.ITable;
import com.github.mishaninss.arma.html.elements.ArmaElement;
import com.github.mishaninss.arma.html.elements.NoopElement;
import com.github.mishaninss.arma.html.interfaces.IElementsContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.interfaces.IListenableElement;
import com.github.mishaninss.arma.html.interfaces.INamed;
import com.github.mishaninss.arma.html.readers.NoopReader;
import com.github.mishaninss.arma.uidriver.LocatorType;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import com.github.mishaninss.arma.utils.ReflectionUtils;
import com.github.mishaninss.arma.utils.UrlUtils;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Provides mechanism for creating and initializing of container instances
 */
@SuppressWarnings("unused")
@Component
public class ContainersFactory implements InitializingBean, DisposableBean {

  private static final String EXCEPTION_NO_LOCATORS = "Невозможно создать контроллер для элемента [%s > %s]. Должен быть указан как минимум один локатор";
  private static final String EXCEPTION_INIT_FAILURE = "Невозможно инициализировать контейнер [%s]";
  private static final String EXCEPTION_LOCATORS_FILE_NOT_FOUND =
      EXCEPTION_INIT_FAILURE + ". Файл с локаторами [%s] не найден";
  private static final ThreadLocal<ContainersFactory> INSTANCES = new ThreadLocal<>();

  @Autowired
  private UiCommonsProperties uiCommonsProperties;
  @Autowired
  private Environment env;
  @Autowired
  private UrlUtils urlUtils;
  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private IDefaultEventHandlersProvider defaultEventHandlersProvider;

  @Override
  public void afterPropertiesSet() {

    INSTANCES.set(this);
  }

  @Override
  public void destroy() {
    INSTANCES.remove();
  }

  private ContainersFactory() {
  }

  public static ContainersFactory get() {
    return INSTANCES.get();
  }

  /**
   * Provides an instance of container of a given class. If an instance of a given class has been
   * already created, method will return it. Otherwise a new instance will be created and
   * initialized.
   *
   * @param clazz a Page Object class
   * @return an instance of Page Object class
   */
  public synchronized <T> T initContainer(Class<T> clazz) {
    Preconditions.checkNotNull(clazz, "clazz value cannot be null");
    try {
      return applicationContext.getBean(clazz);
    } catch (Exception e) {
      throw getException(e, EXCEPTION_INIT_FAILURE, clazz.getName());
    }
  }

  /**
   * Performs initializing of a given container instance. Initializing includes mapping of element
   * controllers and element instances, resolving element parameters, specified by annotations.
   *
   * @param instance - an instance of Page Object class
   */
  public void initContainer(IElementsContainer instance) {
    try {
      setContainerName(instance);
      setContainerLocator(instance);
      setContainerUrl(instance);
      setContainerContext(instance);

      Map<String, List<String>> loadedLocators = loadLocators(instance);
      resolveElementsMapping(instance, loadedLocators);

      checkIfNested(instance);
    } catch (Exception ex) {
      throw getException(ex, EXCEPTION_INIT_FAILURE, instance.getClass().getName());
    }
  }

  private @Nullable
  Container getContainerProps(@NonNull IElementsContainer instance) {
    return getContainerProps(instance.getClass());
  }

  private @Nullable
  Container getContainerProps(@NonNull Class<?> clazz) {
    if (clazz.isAnnotationPresent(ContextualContainer.class)) {
      var contextualContainer = clazz.getAnnotation(ContextualContainer.class);
      Container[] containers = contextualContainer.value();
      Container defaultParameters = null;
      for (Container container : containers) {
        String property = container.prop();
        String expectedValue = container.val();
        if (Element.PROFILE_PROPERTY.equals(property)
            && ArrayUtils.contains(env.getActiveProfiles(), expectedValue)) {
          return container;
        } else if (StringUtils.equals(env.getProperty(property), expectedValue)) {
          return container;
        } else if (defaultParameters == null && StringUtils.isBlank(expectedValue)) {
          defaultParameters = container;
        }
      }
      return defaultParameters;
    } else if (clazz.isAnnotationPresent(Container.class)) {
      return clazz.getAnnotation(Container.class);
    }
    return null;
  }

  private @Nullable
  Url getUrlProps(@NonNull IElementsContainer instance) {
    Class<? extends IElementsContainer> clazz = instance.getClass();
    if (clazz.isAnnotationPresent(ContextualUrl.class)) {
      var contextualUrl = clazz.getAnnotation(ContextualUrl.class);
      Url[] urls = contextualUrl.value();
      Url defaultParameters = null;
      for (Url url : urls) {
        String property = url.prop();
        String expectedValue = url.val();
        String actualValue = env.getProperty(property, "");
        if (StringUtils.isNoneBlank(actualValue) && actualValue.equals(expectedValue)) {
          return url;
        } else if (defaultParameters == null && StringUtils.isBlank(expectedValue)) {
          defaultParameters = url;
        }
      }
      return defaultParameters;
    } else if (clazz.isAnnotationPresent(Url.class)) {
      return clazz.getAnnotation(Url.class);
    }
    return null;
  }

  private @Nullable
  Element getElementProps(@NonNull Field elementField) {
    if (elementField.isAnnotationPresent(ContextualElement.class)) {
      var contextualElement = elementField.getAnnotation(ContextualElement.class);
      Element[] elements = contextualElement.value();
      Element defaultParameters = null;
      for (Element element : elements) {
        String property = element.prop();
        String expectedValue = element.val();
        if (Element.PROFILE_PROPERTY.equals(property)
            && ArrayUtils.contains(env.getActiveProfiles(), expectedValue)) {
          return element;
        } else if (StringUtils.equals(env.getProperty(property), expectedValue)) {
          return element;
        } else if (defaultParameters == null && StringUtils.isBlank(expectedValue)) {
          defaultParameters = element;
        }
      }
      return defaultParameters;
    } else if (elementField.isAnnotationPresent(Element.class)) {
      return elementField.getAnnotation(Element.class);
    }
    return null;
  }

  public String getContainerName(Class<?> clazz) {
    var containerProps = getContainerProps(clazz);
    if (containerProps != null) {
      String name = containerProps.value();
      if (StringUtils.isBlank(name)) {
        name = containerProps.name();
      }

      if (StringUtils.isBlank(name)) {
        name = StringUtils
            .join(StringUtils.splitByCharacterTypeCamelCase(clazz.getSimpleName()), " ");
      }
      return StringUtils.normalizeSpace(name);
    }
    return null;
  }

  public static String getContainerBeanId(@NonNull Class<?> clazz) {
    Container containerProps = null;
    if (clazz.isAnnotationPresent(ContextualContainer.class)) {
      Container[] containerPropsArr = clazz.getAnnotation(ContextualContainer.class).value();
      if (ArrayUtils.isNotEmpty(containerPropsArr)) {
        containerProps = containerPropsArr[0];
      }
    } else if (clazz.isAnnotationPresent(Container.class)) {
      containerProps = clazz.getAnnotation(Container.class);
    }
    String name = null;
    if (containerProps != null) {
      name = containerProps.value();
    }

    if (StringUtils.isBlank(name)) {
      name = StringUtils
          .join(StringUtils.splitByCharacterTypeCamelCase(clazz.getSimpleName()), " ");
    }
    return DataObject.sanitizeElementId(name);
  }

  private void setContainerName(@NonNull IElementsContainer instance) {
    Class<? extends IElementsContainer> clazz = instance.getClass();
    if (instance instanceof INamed && ((INamed) instance).getName() == null) {
      String name = getContainerName(clazz);
      ((INamed) instance).setName(StringUtils.normalizeSpace(name));
    }
  }

  private void setContainerLocator(@NonNull IElementsContainer instance) {
    var containerProps = getContainerProps(instance);
    if (containerProps != null) {
      String locator = getContainerLocator(containerProps);
      if (StringUtils.isNotBlank(locator)) {
        instance.setLocator(locator);
      }
    }
  }

  public static String getContainerLocator(Container containerProps) {
    String locator = getTypedContainerLocator(containerProps);
    if (StringUtils.isNotBlank(locator)) {
      return locator;
    }
    locator = containerProps.locator();
    if (StringUtils.isNotBlank(locator)) {
      return locator;
    }
    return null;
  }

  private void setContainerUrl(@Nullable IElementsContainer instance) {
    if (instance instanceof IHaveUrl) {
      String url = null;
      var urlProps = getUrlProps(instance);
      if (urlProps != null) {
        url = urlProps.value().trim();
      }
      if (!StringUtils.isBlank(url)) {
        url = urlUtils.resolveUrl(url);
        ((IHaveUrl) instance).setUrl(url);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void checkIfNested(@NonNull ILocatable instance) {
    Class<? extends ILocatable> clazz = instance.getClass();
    if (clazz.isAnnotationPresent(Nested.class)) {
      Class<?> declaringClass = clazz.getDeclaringClass();
      if (declaringClass != null && ArmaContainer.class.isAssignableFrom(declaringClass)) {
        try {
          ILocatable context = initContainer((Class<ArmaContainer>) declaringClass);
          instance.setContext(context);
        } catch (Exception ex) {
          ((ArmaContainer) instance).setContextClass((Class<ILocatable>) declaringClass);
        }
      }
    }
  }

  private @NonNull
  Map<String, List<String>> loadLocators(@NonNull IElementsContainer instance) throws IOException {
    Class<? extends IElementsContainer> clazz = instance.getClass();
    Map<String, List<String>> loadedLocators = new LinkedHashMap<>();
    var containerProps = getContainerProps(instance);
    if (containerProps != null) {
      String locatorsFilePath = containerProps.locators();
      if (StringUtils.isNoneBlank(locatorsFilePath)) {
        try {
          loadedLocators = CsvDataExtractor.extractData(locatorsFilePath);
        } catch (IOException ex) {
          var message = String
              .format(EXCEPTION_LOCATORS_FILE_NOT_FOUND, ((INamed) instance).getName(),
                  locatorsFilePath);
          throw new ContainerInitException(message, ex);
        }
      } else {
        locatorsFilePath = clazz.getName().replace(".", "/") + ".csv";
        var locatorsUrl = clazz.getClassLoader().getResource(locatorsFilePath);
        if (locatorsUrl != null) {
          loadedLocators = CsvDataExtractor.extractData(locatorsUrl);
        }
      }
    }
    return loadedLocators;
  }

  private void resolveElementsMapping(@NonNull IElementsContainer container,
      Map<String, List<String>> loadedLocators) throws IllegalAccessException {
    Class<?> clazz = container.getClass();

    Map<String, IInteractiveElement> elements = new LinkedHashMap<>();

    List<Class> elementIdsClasses = ReflectionUtils
        .getInnerClassesWithAnnotation(clazz, ElementIds.class);
    List<Field> controllerFields = FieldUtils.getFieldsListWithAnnotation(clazz, Element.class);
    controllerFields.addAll(FieldUtils.getFieldsListWithAnnotation(clazz, ContextualElement.class));

    //Map element controllers with explicitly provided IDs
    for (Class<?> elementIdsClass : elementIdsClasses) {
      elements.putAll(resolveElementIdsClassMapping(elementIdsClass, controllerFields, container,
          loadedLocators));
    }

    //Map element controllers without explicitly provided IDs
    elements.putAll(mapElementsWithoutExplicitId(controllerFields, container, loadedLocators));

    if (MapUtils.isNotEmpty(elements)) {
      container.setElements(elements);
    }
  }

  private Map<String, IInteractiveElement> resolveElementIdsClassMapping(Class<?> elementIdsClass,
      List<Field> controllerFields, IElementsContainer container,
      Map<String, List<String>> loadedLocators) throws IllegalAccessException {
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

      IInteractiveElement element = getMappedElement(idFieldName, elementId, controllerFields,
          container, loadedLocators.get(elementId));

      if (element != null) {
        elements.put(DataObject.sanitizeElementId(elementId), element);
      }
    }
    return elements;
  }

  private IInteractiveElement getMappedElement(String idFieldName, String elementId,
      @NonNull List<Field> controllerFields, IElementsContainer container,
      List<String> loadedLocators) {
    for (Field controllerField : controllerFields) {
      if (controllerField.isAnnotationPresent(Element.class) &&
          controllerField.getAnnotation(Element.class).id().equals(elementId) ||
          controllerField.getName().equalsIgnoreCase(idFieldName)) {

        IInteractiveElement element = initElement(elementId, controllerField, container,
            loadedLocators);

        controllerFields.remove(controllerField);
        return element;
      }
    }
    return null;
  }

  private @NonNull
  Map<String, IInteractiveElement> mapElementsWithoutExplicitId(List<Field> controllerFields,
      IElementsContainer container, Map<String, List<String>> loadedLocators) {
    Map<String, IInteractiveElement> elements = new LinkedHashMap<>();

    for (Field controllerField : controllerFields) {
      String elementId = null;
      var elementProps = getElementProps(controllerField);
      if (elementProps != null) {
        elementId = elementProps.id();
      }
      if (StringUtils.isBlank(elementId)) {
        elementId = StringUtils
            .join(StringUtils.splitByCharacterTypeCamelCase(controllerField.getName()), "_")
            .toLowerCase();
      }

      IInteractiveElement element = initElement(elementId, controllerField, container,
          loadedLocators.get(elementId));
      elements.put(DataObject.sanitizeElementId(elementId), element);
    }

    return elements;
  }

  /**
   * Performs initializing of an element controller: - creates a controller instance if it was not
   * explicitly created; - assigns locator if it was not specified explicitly; - sets name of
   * element; - sets dynamic mark; - sets context lookup mark.
   *
   * @param elementId       - internal ID of an element
   * @param controllerField - field of an element controller
   * @param container       - instance of an element's container
   * @param locators        - locators, loaded from an external source;
   * @return and instance of Interactive Element
   */
  private IInteractiveElement initElement(String elementId, Field controllerField,
      IElementsContainer container, List<String> locators) {
    //Get element controller
    IInteractiveElement element = readElementInstance(controllerField, container);

    //If element controller was not explicitly instantiated try to create an instance
    if (element == null) {
      List<String> elementLocators = locators;
      if (CollectionUtils.isEmpty(elementLocators)) {
        elementLocators = getElementLocators(controllerField);
        if (CollectionUtils.isEmpty(elementLocators)) {
          throw getException(EXCEPTION_NO_LOCATORS, ((INamed) container).getName(), elementId);
        }
      }

      Class<? extends IInteractiveElement> controllerClass = getControllerClass(controllerField);
      element = createElementController(controllerClass, elementLocators);
    } else if (StringUtils.isBlank(element.getLocator())) {
      //If controller was instantiated, but has no assigned locator (e.g. explicitly created without provided locator)
      List<String> elementLocators = locators;
      if (CollectionUtils.isEmpty(elementLocators)) {
        elementLocators = getElementLocators(controllerField);
      }
      if (CollectionUtils.isNotEmpty(elementLocators)) {
        element.setLocator(elementLocators.get(0));
      } else {
        throw getException(EXCEPTION_NO_LOCATORS, ((INamed) container).getName(), elementId);
      }
    }

    setElementContext(element, container, controllerField);
    addDefaultListeners(element);
    setElementName(element, controllerField, elementId);
    setElementOptionalMark(element, controllerField);
    setElementContextLookup(element, controllerField);
    setNextPage(element, controllerField, container);
    setReader(element, controllerField);

    if (controllerField.getType().equals(IndexedElement.class)
        && !(element instanceof IndexedElement)) {
      element = applicationContext.getBean(IndexedElement.class, element);
    } else if (controllerField.getType().equals(TemplatedElement.class)
        && !(element instanceof TemplatedElement)) {
      element = applicationContext.getBean(TemplatedElement.class, element);
    }

    writeElementInstance(controllerField, container, element);

    return element;
  }

  private void setElementContext(IInteractiveElement element, IElementsContainer container,
      Field controllerField) {
    var elementProps = getElementProps(controllerField);
    if (elementProps != null) {
      Class<? extends ILocatable> contextClass = elementProps.context();
      if (!contextClass.equals(NoopElement.class)) {
        ILocatable context = applicationContext.getBean(contextClass);
        context.getRealLocatableObjectDeque().pop().setContext(container);
        element.setContext(context);
        return;
      }
    }
    element.setContext(container);
  }

  private void setContainerContext(IElementsContainer container) {
    var containerProps = getContainerProps(container);
    if (containerProps != null) {
      Class<? extends ILocatable> contextClass = containerProps.context();
      if (!contextClass.equals(NoopElement.class)) {
        ILocatable context = applicationContext.getBean(contextClass);
        container.setContext(context);
      }
    }
  }

  private static IInteractiveElement readElementInstance(@NonNull Field controllerField,
      @NonNull IElementsContainer container) {
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

  private static void writeElementInstance(@NonNull Field controllerField,
      @NonNull IElementsContainer container, @NonNull IInteractiveElement element) {
    Preconditions.checkNotNull(controllerField, "controllerField value cannot be null");
    Preconditions.checkNotNull(container, "container value cannot be null");
    Preconditions.checkNotNull(element, "element value cannot be null");
    try {
      if (Modifier.isStatic(controllerField.getModifiers())) {
        FieldUtils.writeStaticField(controllerField, element, true);
      } else {
        FieldUtils.writeField(controllerField, container, element, true);
      }
    } catch (IllegalAccessException e) {
      throw getException(e, "Couldn't write %s controller field of the %s container",
          controllerField.getName(), ((INamed) container).getName());
    }
  }

  @SuppressWarnings("unchecked")
  private @NonNull
  Class<? extends IInteractiveElement> getControllerClass(@NonNull Field controllerField) {
    Class<?> fieldType = controllerField.getType();
    var elementProps = getElementProps(controllerField);
    Class<?> elementType = elementProps != null ? elementProps.type() : NoopElement.class;
    if (ReflectionUtils.isGenericType(fieldType)) {
      Class<?> genericType = ReflectionUtils.getGenericClass(controllerField.getGenericType(), 0);
      if (genericType != null) {
        elementType = genericType;
      } else if (elementType.equals(NoopElement.class)) {
        throw getException(
            "Type of an element field [%s] is a generic type [%s], but type parameter was not provided. Add generic parameter of a field type or use @Element(type = <element type>)",
            controllerField.getName(), fieldType.getName());
      }
    } else if (!elementType.equals(NoopElement.class)) {
      if (!fieldType.isAssignableFrom(elementType)) {
        throw getException(
            "Element type, specified for field [%s], is [%s], and it's not compatible with field type [%s]",
            controllerField.getName(), elementType.getName(), fieldType.getName());
      }
    } else if (!fieldType.isInterface()) {
      elementType = fieldType;
    } else {
      throw getException(
          "Type of an element field [%s] is an interface [%s], but implementation type was not provided. Provide concrete element type using @Element(type = <element type>)",
          controllerField.getName(), fieldType.getName());
    }

    if (!IInteractiveElement.class.isAssignableFrom(elementType)) {
      throw getException(
          "Element type, specified for field [%s], is [%s], and it's not compatible with IInteractiveElement type",
          controllerField.getName(), elementType.getName());
    }
    return (Class<? extends IInteractiveElement>) elementType;
  }

  private IInteractiveElement createElementController(Class<? extends IInteractiveElement> clazz,
      List<String> locatorsList) {
    return createElementController(clazz, locatorsList.toArray(new String[0]));
  }

  private IInteractiveElement createElementController(Class<? extends IInteractiveElement> clazz,
      String... locators) {
    return applicationContext.getBean(clazz, (Object[]) locators);
  }

  public void addDefaultListeners(IInteractiveElement element) {
    if (uiCommonsProperties.framework().areDefaultListenersEnabled
        && element instanceof IListenableElement) {
      IListenableElement listenableElement = ((IListenableElement) element);
      listenableElement.addEventListeners(defaultEventHandlersProvider.getEventHandlers());
    }
  }

  private void setElementName(@NonNull IInteractiveElement element, @NonNull Field controllerField,
      @Nullable String elementId) {
    if (element instanceof INamed) {
      String name = null;
      var elementProps = getElementProps(controllerField);
      if (elementProps != null) {
        name = elementProps.name().trim();
      }
      if (StringUtils.isBlank(name)) {
        if (StringUtils.isNotBlank(elementId)) {
          name = WordUtils.capitalize(elementId.replace("_", " "));
        } else {
          name = StringUtils.capitalize(
              StringUtils.join(
                  StringUtils.splitByCharacterTypeCamelCase(controllerField.getName()),
                  " ")
          );
        }
      }
      ((INamed) element).setName(name);
    }
  }

  private void setReader(IInteractiveElement element, Field controllerField) {
    if (element instanceof ArmaElement) {
      var elementProps = getElementProps(controllerField);
      if (elementProps != null) {
        var readerProps = elementProps.reader();
        Class<? extends Function<IInteractiveElement, String>> type = readerProps.value();
        if (!Objects.equals(type, NoopReader.class)) {
          Object[] args = readerProps.args();
          Function<IInteractiveElement, String> reader = applicationContext.getBean(type, args);
          ((ArmaElement) element).setReader(reader);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends IBatchElementsContainer> T cloneContainer(T anotherContainer) {
    Class<? extends IBatchElementsContainer> clazz = anotherContainer.getClass();
    var container = (T) BeanUtils.instantiateClass(clazz);
    DefaultListableBeanFactory factory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext) applicationContext)
        .getBeanFactory();
    factory.autowireBean(container);
    factory.initializeBean(container, clazz.getName());
    container.setLocator(anotherContainer.getLocator());
    container.setContext(anotherContainer.getContext());
    return container;
  }

  private @NonNull
  List<String> getElementLocators(@NonNull Field controllerField) {
    var elementProps = getElementProps(controllerField);
    if (elementProps != null) {
      List<String> locators = Arrays.asList(elementProps.locators());
      if (CollectionUtils.isNotEmpty(locators)) {
        return resolvePlaceholders(locators);
      }
      locators = new ArrayList<>();
      String locator = getTypedElementLocator(elementProps);
      if (StringUtils.isNotBlank(locator)) {
        locators.add(locator);
        return resolvePlaceholders(locators);
      }
      locator = elementProps.value();
      if (StringUtils.isNotBlank(locator)) {
        locators.add(locator);
        return resolvePlaceholders(locators);
      }
      locator = elementProps.locator();
      if (StringUtils.isNotBlank(locator)) {
        locators.add(locator);
      }
      return resolvePlaceholders(locators);
    }
    return new ArrayList<>();
  }

  private String getTypedElementLocator(Element elementProps) {
    String locator = elementProps.byId();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildId(locator);
    }
    locator = elementProps.byName();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildName(locator);
    }
    locator = elementProps.byXpath();
    if (StringUtils.isNotBlank(locator)) {
      return (locator);
    }
    locator = elementProps.byCss();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildCss(locator);
    }
    locator = elementProps.byClass();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildClass(locator);
    }
    locator = elementProps.byTag();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildTag(locator);
    }
    locator = elementProps.byLink();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildLink(locator);
    }
    locator = elementProps.byPatrialLink();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildPartialLink(locator);
    }
    locator = elementProps.byText();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildText(locator);
    }
    locator = elementProps.byArg();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildArg(locator);
    }

    return null;
  }

  private static String getTypedContainerLocator(Container containerProps) {
    String locator = containerProps.byId();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildId(locator);
    }
    locator = containerProps.byName();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildName(locator);
    }
    locator = containerProps.byXpath();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildXpath(locator);
    }
    locator = containerProps.byCss();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildCss(locator);
    }
    locator = containerProps.byClass();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildClass(locator);
    }
    locator = containerProps.byTag();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildTag(locator);
    }
    locator = containerProps.byLink();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildLink(locator);
    }
    locator = containerProps.byPatrialLink();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildPartialLink(locator);
    }

    locator = containerProps.byArg();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildArg(locator);
    }

    return null;
  }

  private static String getTypedTableLocator(ITable tableProps) {
    String locator = tableProps.byId();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildId(locator);
    }
    locator = tableProps.byName();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildName(locator);
    }
    locator = tableProps.byXpath();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildXpath(locator);
    }
    locator = tableProps.byCss();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildCss(locator);
    }
    locator = tableProps.byClass();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildClass(locator);
    }
    locator = tableProps.byTag();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildTag(locator);
    }
    locator = tableProps.byLink();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildLink(locator);
    }
    locator = tableProps.byPatrialLink();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildPartialLink(locator);
    }

    locator = tableProps.byArg();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildArg(locator);
    }

    return null;
  }

  private static String getTypedColumnLocator(IColumn columnProps) {
    String locator = columnProps.byId();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildId(locator);
    }
    locator = columnProps.byName();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildName(locator);
    }
    locator = columnProps.byXpath();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildXpath(locator);
    }
    locator = columnProps.byCss();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildCss(locator);
    }
    locator = columnProps.byClass();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildClass(locator);
    }
    locator = columnProps.byTag();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildTag(locator);
    }
    locator = columnProps.byLink();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildLink(locator);
    }
    locator = columnProps.byPatrialLink();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildPartialLink(locator);
    }

    locator = columnProps.byArg();
    if (StringUtils.isNotBlank(locator)) {
      return LocatorType.buildArg(locator);
    }

    return null;
  }

  private void setElementOptionalMark(IInteractiveElement element, Field controllerField) {
    if (controllerField.isAnnotationPresent(Optional.class)) {
      element.setOptional(true);
    } else {
      var elementProps = getElementProps(controllerField);
      if (elementProps != null) {
        element.setOptional(elementProps.optional());
      }
    }
  }

  private void setElementContextLookup(@NonNull IInteractiveElement element,
      @NonNull Field controllerField) {
    var useContextLookup = true;
    var elementProps = getElementProps(controllerField);
    if (elementProps != null) {
      useContextLookup = elementProps.contextLookup();
    }
    element.setContextLookup(useContextLookup);
  }

  private void setNextPage(IInteractiveElement element, Field controllerField,
      IElementsContainer container) {
    var elementProps = getElementProps(controllerField);
    if (elementProps != null) {
      Class<? extends IInteractiveContainer> nextPageClass = elementProps.nextPage();
      if (!nextPageClass.equals(ArmaContainer.class) && IInteractiveContainer.class
          .isAssignableFrom(nextPageClass)) {
        element.setNextPage(nextPageClass);
        return;
      }
    }
    element.setNextPage((IInteractiveContainer) container);
  }

  public void initTable(@NonNull Table instance) throws IllegalAccessException {
    Map<String, Column<IInteractiveElement>> namedColumns = new LinkedHashMap<>();
    Map<Integer, Column<IInteractiveElement>> indexedColumns = new LinkedHashMap<>();

    Class<? extends Table> clazz = instance.getClass();
    List<Field> columnFields = FieldUtils.getFieldsListWithAnnotation(clazz, IColumn.class);
    columnFields.addAll(FieldUtils.getFieldsListWithAnnotation(clazz, IContextualColumn.class));

    checkIfNested(instance);
    setTableLocator(instance);
    setTableContext(instance);

    //Set container's name for logging
    setTableName(instance);

    for (var i = 0; i < columnFields.size(); i++) {
      var columnField = columnFields.get(i);
      Column<IInteractiveElement> column =
          Modifier.isStatic(columnField.getModifiers()) ?
              (Column<IInteractiveElement>) FieldUtils.readStaticField(columnField, true) :
              (Column<IInteractiveElement>) FieldUtils.readField(columnField, instance, true);
      if (column == null) {
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
      if (startRowIndex > 0) {
        column.setStartRowIndex(startRowIndex);
      }

      column.setTable(instance);
    }

    instance.setNamedColumns(namedColumns);
    instance.setIndexedColumns(indexedColumns);
  }

  private void setTableLocator(@NonNull Table instance) {
    ITable tableProps = getTableProps(instance.getClass());
    if (tableProps != null) {
      String locator = getTypedTableLocator(tableProps);
      if (StringUtils.isBlank(locator)) {
        locator = tableProps.locator();
      }
      if (StringUtils.isNotBlank(locator)) {
        instance.setLocator(locator);
      }
    }
  }

  private void setTableName(Table instance) {
    String name = null;
    ITable tableProps = getTableProps(instance.getClass());
    if (tableProps != null) {
      name = tableProps.value();
      if (StringUtils.isBlank(name)) {
        name = tableProps.name();
      }
    }
    if (StringUtils.isBlank(name)) {
      name = StringUtils
          .join(StringUtils.splitByCharacterTypeCamelCase(instance.getClass().getSimpleName()),
              " ");
    }
    instance.setName(StringUtils.normalizeSpace(name));
  }

  private void setTableContext(Table table) {
    ITable tableProps = getTableProps(table.getClass());
    if (tableProps != null) {
      Class<? extends ILocatable> contextClass = tableProps.context();
      if (!contextClass.equals(NoopElement.class)) {
        ILocatable context = applicationContext.getBean(contextClass);
        table.setContext(context);
      }
    }
  }

  private ITable getTableProps(Class<? extends Table> clazz) {
    if (clazz.isAnnotationPresent(IContextualTable.class)) {
      IContextualTable contextualTable = clazz.getAnnotation(IContextualTable.class);
      ITable[] tablesProps = contextualTable.value();
      ITable defaultParameters = null;
      for (ITable tableProps : tablesProps) {
        String property = tableProps.prop();
        String expectedValue = tableProps.val();
        String actualValue = env.getProperty(property, "");
        if (StringUtils.isNoneBlank(actualValue) && actualValue.equals(expectedValue)) {
          return tableProps;
        } else if (defaultParameters == null && StringUtils.isBlank(expectedValue)) {
          defaultParameters = tableProps;
        }
      }
      return defaultParameters;
    } else if (clazz.isAnnotationPresent(ITable.class)) {
      return clazz.getAnnotation(ITable.class);
    }
    return null;
  }

  @Nullable
  private IColumn getColumnProps(@NonNull Field columnField) {
    if (columnField.isAnnotationPresent(IContextualColumn.class)) {
      IContextualColumn contextualColumn = columnField.getAnnotation(IContextualColumn.class);
      IColumn[] columns = contextualColumn.value();
      IColumn defaultParameters = null;
      for (IColumn column : columns) {
        String property = column.prop();
        String expectedValue = column.val();
        String actualValue = env.getProperty(property, "");
        if (StringUtils.isNoneBlank(actualValue) && actualValue.equals(expectedValue)) {
          return column;
        } else if (defaultParameters == null && StringUtils.isBlank(expectedValue)) {
          defaultParameters = column;
        }
      }
      return defaultParameters;
    } else if (columnField.isAnnotationPresent(IColumn.class)) {
      return columnField.getAnnotation(IColumn.class);
    }
    return null;
  }

  private IInteractiveElement createCellInstance(@NonNull Field field) {
    Class<? extends IInteractiveElement> cellClass = getControllerClass(field);
    List<String> locators = getColumnLocators(field);
    IInteractiveElement element = createElementController(cellClass, locators);
    addDefaultListeners(element);
    return element;
  }

  private String getColumnName(@NonNull Field columnField) {
    String name = null;
    IColumn columnProps = getColumnProps(columnField);
    if (columnProps != null) {
      name = columnProps.name();
    }
    if (StringUtils.isBlank(name)) {
      name = StringUtils.capitalize(
          StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(columnField.getName()), " "));
    }
    return name;
  }

  private int getColumnIndex(@NonNull Field columnField) {
    var index = 0;
    IColumn columnProps = getColumnProps(columnField);
    if (columnProps != null) {
      index = columnProps.index();
    }
    return index;
  }

  private int getStartRowIndex(@NonNull Field columnField) {
    var startRowIndex = 0;
    IColumn columnProps = getColumnProps(columnField);
    if (columnProps != null) {
      startRowIndex = columnProps.startIndex();
    }
    return startRowIndex;
  }

  private List<String> getColumnLocators(@NonNull Field columnField) {
    IColumn columnProps = getColumnProps(columnField);
    if (columnProps != null) {
      List<String> locators = Arrays.asList(columnProps.locators());
      if (CollectionUtils.isNotEmpty(locators)) {
        return locators;
      }

      String locator = getTypedColumnLocator(columnProps);
      if (StringUtils.isBlank(locator)) {
        locator = columnProps.locator();
      }
      if (StringUtils.isNotBlank(locator)) {
        return List.of(locator);
      }
    }
    return List.of();
  }

  private static ContainerInitException getException(String message, Object... args) {
    return new ContainerInitException(String.format(message, args));
  }

  private static ContainerInitException getException(Throwable cause, String message,
      Object... args) {
    return new ContainerInitException(String.format(message, args), cause);
  }

  private @NonNull
  List<String> resolvePlaceholders(@NonNull List<String> values) {
    return values.stream().map(env::resolvePlaceholders).collect(Collectors.toList());
  }
}