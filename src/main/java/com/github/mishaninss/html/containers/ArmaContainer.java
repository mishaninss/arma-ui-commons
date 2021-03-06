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

import com.github.mishaninss.data.DataObjectUtils;
import com.github.mishaninss.html.composites.IndexedElementBuilder;
import com.github.mishaninss.html.containers.annotations.Container;
import com.github.mishaninss.html.containers.interfaces.IBatchElementsContainer;
import com.github.mishaninss.html.containers.interfaces.IHaveUrl;
import com.github.mishaninss.html.elements.ElementBuilder;
import com.github.mishaninss.html.elements.interfaces.IEditable;
import com.github.mishaninss.html.elements.interfaces.IReadable;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.uidriver.Arma;
import com.github.mishaninss.uidriver.interfaces.IContainerWaitingDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller for abstract set of elements
 *
 * @author Sergey Mishanin
 */
@SuppressWarnings("unused")
@Container
public class ArmaContainer implements IBatchElementsContainer, INamed, IHaveUrl {

    @Autowired
    protected Arma arma;

    @PostConstruct
    protected void init() {
        arma.containersFactory().initContainer(this);
    }

    private Map<String, IInteractiveElement> elements = new LinkedHashMap<>();

    protected String name;
    protected String locator;
    protected boolean contextLookup = true;
    protected ILocatable context;
    protected String url;

    private static final String EXCEPTION_EMPTY_ELEMENT_ID = "element ID cannot be null or blank string";
    private static final String EXCEPTION_UNKNOWN_ELEMENT_ID = "Unknown element ID [%s] for container [%s]. Available IDs: %s";
    private static final String EXCEPTION_ELEMENT_IS_NOT_EDITABLE = "Element of type [%s] whith ID [%s] is not editable";
    private static final String EXCEPTION_ELEMENT_IS_NOT_READABLE = "Element of type [%s] whith ID [%s] is not readable";

// Constructors ********************************************************************************************************

    protected ArmaContainer() {
    }

    protected ArmaContainer(String locator) {
        this.locator = locator;
    }

    protected ArmaContainer(String locator, ArmaContainer context) {
        this.locator = locator;
        this.context = context;
    }

    protected <T extends IBatchElementsContainer> ArmaContainer(T container) {
        setLocator(container.getLocator());
        setContextLookup(container.useContextLookup());
        setContext(container.getContext());
        addElements(container.getElements());
        setName(INamed.getNameIfApplicable(container));
        setUrl(IHaveUrl.getUrlIfApplicable(container));
    }

    public IContainerWaitingDriver waitUntil() {
        return arma.applicationContext().getBean(IContainerWaitingDriver.class, this);
    }

// IBatchElementsContainer *********************************************************************************************

    /**
     * Performs actions to change values of elements with given IDs
     *
     * @param inputData - a key-value map, where a key is and ID of an element, and value is a desired value
     * @return this container
     */
    @Override
    public @NonNull
    ArmaContainer changeValues(@NonNull Map<String, ?> inputData) {
        final Map<String, IInteractiveElement> editableElements = getEditableElements();
        final Map<String, ?> adjustedInputData = adjustDataKeys(inputData);
        editableElements.forEach((elementId, element) -> {
            Object value = adjustedInputData.get(elementId);
            if (value != null) {
                element.changeValue(value);
            }
        });
        return this;
    }

    private @NonNull
    Map<String, ?> adjustDataKeys(@NonNull Map<String, ?> inputData) {
        return inputData.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> ContainersFactory.sanitizeElementId(entry.getKey()),
                        Map.Entry::getValue));
    }

    /**
     * Performs actions to change values of elements based on the given data object
     *
     * @param dataObject - data object
     * @return this container
     */
    public @NonNull
    ArmaContainer changeValues(@NonNull Object dataObject) {
        Map<String, Object> inputData = DataObjectUtils.readDataFromObject(elements.keySet(), dataObject);
        changeValues(inputData);
        return this;
    }

    /**
     * Performs actions to read values of all readable elements in this container.
     * Elements, marked as Optional and not displayed on a page will be skipped.
     *
     * @return a key-value map, where a key is an ID of an element, and value is a value of an element
     */
    @Override
    public @NonNull
    Map<String, String> readValues() {
        Map<String, String> values = new LinkedHashMap<>();
        getReadableElements().forEach((elementId, element) -> {
            String value = readValueOrDefault(element, null);
            if (value != null) {
                values.put(elementId, value);
            }
        });
        return values;
    }

    /**
     * Performs actions to read values of elements of this container with given IDs
     * Elements, marked as Optional and not displayed on a page will be skipped.
     *
     * @param elementIds - IDs of elements to read values
     * @return a key-value map, where a key is an ID of an element, and value is a value of this element
     */
    @Override
    public @NonNull Map<String, String> readValues(@NonNull Iterable<String> elementIds) {
        Map<String, String> values = new LinkedHashMap<>();
        getReadableElements(Lists.newArrayList(elementIds))
                .forEach((elementId, element) -> {
                    String value = readValueOrDefault(element, null);
                    if (value != null) {
                        values.put(elementId, value);
                    }
                });
        return values;
    }

    /**
     * Reads values of all elements in this container
     *
     * @param object - data object to store values
     * @return given data object filled with values
     */
    @Override
    public <T> T readValues(T object) {
        return DataObjectUtils.putDataToObject(readValues(), object);
    }

    /**
     * Reads values of all elements in this container
     *
     * @param clazz - class of a data object to store values
     * @return given data object filled with values
     */
    @Override
    public <T> T readValues(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        Map<String, String> values = readValues();
        return DataObjectUtils.putDataToObject(values, clazz);
    }

    /**
     * Reads values of all elements in this container
     *
     * @param clazz - class of a data object to store values
     * @return given data object filled with values
     */
    @Override
    public <T> T readValues(Class<T> clazz, Iterable<String> elementIds) throws InstantiationException, IllegalAccessException {
        return DataObjectUtils.putDataToObject(readValues(elementIds), clazz);
    }

// IInteractiveContainer ***********************************************************************************************

    /**
     * Performs actions to change value of an element with given ID
     *
     * @param elementId - ID of an element
     * @param value     - desired value
     */
    @Override
    public void changeValue(final String elementId, final Object value) {
        IInteractiveElement element = getEditableElement(elementId);
        element.changeValue(value);
    }

    /**
     * Performs actions to read value from an element with given ID
     *
     * @param elementId - ID of an element
     * @return vaue of an element
     */
    @Override
    public String readValue(final String elementId) {
        IInteractiveElement element = getReadableElement(elementId);
        return element.readValue();
    }

    /**
     * Performs actions to read value from an element with given ID
     *
     * @param element      - element to read value
     * @param defaultValue - default value of an element
     * @return value of an element, if this element is displayed. Default value if this element is Optional
     * and not displayed on a page.
     */
    private @javax.annotation.Nullable String readValueOrDefault(@NonNull final IInteractiveElement element, @Nullable final String defaultValue) {
        if (element.isOptional()) {
            if (element.isDisplayed(false)) {
                try {
                    return element.readValue();
                } catch (Exception ex) {
                    return defaultValue;
                }
            } else {
                return defaultValue;
            }
        } else {
            return element.readValue();
        }
    }

    /**
     * Performs an action on element with given ID
     *
     * @param elementId - ID of an element
     */
    @Override
    public void performAction(final String elementId) {
        Preconditions.checkArgument(StringUtils.isNoneBlank(elementId), EXCEPTION_EMPTY_ELEMENT_ID);
        IInteractiveElement element = getElement(elementId);
        element.performAction();
    }

// IElementsContainer **************************************************************************************************

    /**
     * Determines if this container is displayed
     *
     * @return true if all non-dynamic elements from this container are displayed; false otherwise
     */
    @Override
    public boolean isDisplayed() {
        arma.waiting().waitForPageUpdate();
        if (StringUtils.isNoneBlank(locator)) {
            return arma.element().isElementDisplayed(this);
        } else {
            for (IInteractiveElement element : elements.values()) {
                if (!element.isOptional() && !element.isDisplayed()) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Determines if this container is displayed
     *
     * @param shouldWait - indicates if waiting for elements are displayed is required
     * @return true if all non-dynamic elements from this container are displayed; false otherwise
     */
    @Override
    public boolean isDisplayed(boolean shouldWait) {
        arma.waiting().waitForPageUpdate();
        if (StringUtils.isNoneBlank(locator)) {
            return arma.element().isElementDisplayed(this, shouldWait);
        } else {
            for (IInteractiveElement element : elements.values()) {
                if (!element.isOptional() && !element.isDisplayed(shouldWait)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Adds an element controller with given ID to the collection of elements of this container
     *
     * @param elementId - ID of an element
     * @param element   - controller of an element
     * @return this container
     */
    @Override
    public ArmaContainer addElement(String elementId, IInteractiveElement element) {
        element.setContext(this);
        elements.put(ContainersFactory.sanitizeElementId(elementId), element);
        return this;
    }

    /**
     * Adds given collection of elements to the current collection
     *
     * @param elements - a map, where a key is an element ID and a value is an element controller
     */
    @Override
    public ArmaContainer addElements(Map<String, IInteractiveElement> elements) {
        elements.values().parallelStream().forEach(element -> element.setContext(this));
        this.elements.putAll(elements);
        return this;
    }

    /**
     * Returns controller with a given ID from the collection of input or action elements.
     *
     * @param elementId - the ID of action element
     * @return controller of the element
     */
    @Override
    public @NonNull
    IInteractiveElement getElement(@NonNull String elementId) {
        Preconditions.checkArgument(StringUtils.isNoneBlank(elementId), EXCEPTION_EMPTY_ELEMENT_ID);
        elementId = ContainersFactory.sanitizeElementId(elementId);
        IInteractiveElement element = elements.get(elementId);
        Preconditions.checkArgument(element != null, EXCEPTION_UNKNOWN_ELEMENT_ID, elementId, getName(), elements.keySet());
        return element;
    }

    /**
     * Returns current collection of element controllers of this container
     */
    @Override
    public Map<String, IInteractiveElement> getElements() {
        return elements;
    }

// ILocatable **********************************************************************************************************

    @Override
    public String getLocator() {
        return locator;
    }

    @Override
    public void setLocator(String locator) {
        this.locator = locator;
    }

    @Override
    public ILocatable getContext() {
        return context;
    }

    @Override
    public void setContext(ILocatable context) {
        this.context = context;
    }

    @Override
    public void setContextLookup(boolean contextLookup) {
        this.contextLookup = contextLookup;
    }

    @Override
    public boolean useContextLookup() {
        return contextLookup;
    }

// INamed **************************************************************************************************************

    @Override
    public String getName() {
        return name;
    }


    @Override
    public ArmaContainer setName(String name) {
        this.name = name;
        return this;
    }

// Other stuff *********************************************************************************************************

    /**
     * Returns controller with a given ID from the collection of elements.
     *
     * @param elementId - the ID of action element
     * @return controller of the element
     */
    private @NonNull
    IInteractiveElement getEditableElement(@NonNull String elementId) {
        Preconditions.checkArgument(StringUtils.isNoneBlank(elementId), EXCEPTION_EMPTY_ELEMENT_ID);
        IInteractiveElement element = getElement(elementId);
        Preconditions.checkArgument(element instanceof IEditable, EXCEPTION_ELEMENT_IS_NOT_EDITABLE, element.getClass().getSimpleName(), elementId);
        return element;
    }

    /**
     * Returns collection of editable elements
     */
    private Map<String, IInteractiveElement> getEditableElements() {
        return elements.entrySet().stream().
                filter(map -> map.getValue() instanceof IEditable).
                collect(LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        HashMap::putAll);
    }

    /**
     * Returns controller with a given ID from the collection of input elements.
     *
     * @param elementId - the ID of action element
     * @return controller of the element
     */
    private @NonNull
    IInteractiveElement getReadableElement(@NonNull String elementId) {
        Preconditions.checkArgument(StringUtils.isNoneBlank(elementId), EXCEPTION_EMPTY_ELEMENT_ID);
        IInteractiveElement element = getElement(elementId);
        Preconditions.checkArgument(element instanceof IReadable, EXCEPTION_ELEMENT_IS_NOT_READABLE, element.getClass().getSimpleName(), elementId);
        return element;
    }

    /**
     * Returns collection of readable elements
     */
    private Map<String, IInteractiveElement> getReadableElements() {
        return elements.entrySet().stream().
                filter(map -> map.getValue() instanceof IReadable).
                collect(LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        HashMap::putAll);
    }

    /**
     * Returns collection of readable elements based on the given collection of element IDs
     *
     * @param elementIds - required Element IDs
     */
    private Map<String, IInteractiveElement> getReadableElements(Collection<String> elementIds) {
        return elements.entrySet().stream().
                filter(entry -> elementIds.contains(entry.getKey()) && entry.getValue() instanceof IReadable).
                collect(LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        HashMap::putAll);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArmaContainer)) return false;
        ArmaContainer that = (ArmaContainer) o;
        return contextLookup == that.contextLookup &&
                Objects.equals(elements, that.elements) &&
                Objects.equals(name, that.name) &&
                Objects.equals(getLocatorDeque(), that.getLocatorDeque()) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements, name, getLocatorDeque(), contextLookup, url);
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public ElementBuilder elementBy() {
        return arma.elementBy(this);
    }

    public IndexedElementBuilder elementsBy() {
        return arma.elementsBy(this);
    }
}