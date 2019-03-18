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

package com.github.mishaninss.html.elements;

import com.github.mishaninss.html.composites.IndexedElementBuilder;
import com.github.mishaninss.html.containers.annotations.Element;
import com.github.mishaninss.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.interfaces.IListenableElement;
import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.html.listeners.ElementEvent;
import com.github.mishaninss.html.listeners.FiresEvent;
import com.github.mishaninss.html.listeners.IElementEventHandler;
import com.github.mishaninss.uidriver.Arma;
import com.github.mishaninss.uidriver.interfaces.IElementActionsChain;
import com.github.mishaninss.uidriver.interfaces.IElementReadActionDriver;
import com.github.mishaninss.uidriver.interfaces.IElementWaitingDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.uidriver.interfaces.IThisElementDriver;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Controller for the abstract UI Element.
 *
 * @author Sergey Mishanin
 */
@Element
@Primary
public class ArmaElement implements IInteractiveElement, IListenableElement, INamed {
    static final String EXCEPTION_ILLEGAL_TYPE_OF_VALUE = "Illegal type of a value [%s]";
    private static final String EXCEPTION_ILLEGAL_LOCATOR = "Locator cannot be null or empty string";
    @Autowired
    protected Arma arma;
    protected Function<IInteractiveElement, String> reader;
    protected String name;
    protected String locator;
    private boolean optional = false;
    private boolean contextLookup = true;
    private ILocatable context;
    private IInteractiveContainer nextPage;
    private Map<ElementEvent, LinkedHashSet<IElementEventHandler>> eventListeners = new EnumMap<>(ElementEvent.class);

// Constructors ********************************************************************************************************

    public ArmaElement() {
    }

    /**
     * Creates an instance of Basic element
     *
     * @param locator - locator of the element
     */
    public ArmaElement(String locator) {
        if (StringUtils.isBlank(locator)) {
            throw new IllegalArgumentException(EXCEPTION_ILLEGAL_LOCATOR);
        }
        this.locator = locator;
    }

    /**
     * Creates an instance of Basic element
     *
     * @param locator - locator of the element
     * @param context - container of the element
     */
    public ArmaElement(String locator, IInteractiveContainer context) {
        this(locator);
        this.context = context;
    }

    public ArmaElement(IInteractiveElement element) {
        this.locator = element.getLocator();
        this.optional = element.isOptional();
        this.contextLookup = element.useContextLookup();
        this.context = element.getContext();
        this.nextPage = element.nextPage();
        if (element instanceof IListenableElement) {
            setEventListeners(((IListenableElement) element).getEventListeners());
        }
        if (element instanceof INamed) {
            setName(((INamed) element).getName());
        }
        if (element instanceof ArmaElement) {
            this.reader = ((ArmaElement) element).reader;
        }
    }

    @PostConstruct
    protected void init() {
        reader = arma.element()::getTextFromElement;
    }

// IInteractiveElement *************************************************************************************************

    @Override
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public void changeValue(final Object value) {
        arma.element().sendKeysToElement(this, value.toString());
    }

    @Override
    @FiresEvent(ElementEvent.READ_VALUE)
    public String readValue() {
        return reader.apply(this);
    }

    @Override
    @FiresEvent(ElementEvent.ACTION)
    public void performAction() {
        arma.element().clickOnElement(this);
    }

    @Override
    @FiresEvent(ElementEvent.IS_DISPLAYED)
    public boolean isDisplayed() {
        return arma.element().isElementDisplayed(this);
    }

    @Override
    @FiresEvent(ElementEvent.IS_DISPLAYED)
    public boolean isDisplayed(boolean shouldWait) {
        return arma.element().isElementDisplayed(this, shouldWait);
    }

    @Override
    public boolean isEnabled() {
        return arma.element().isElementEnabled(this);
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public void setOptional(boolean dynamic) {
        this.optional = dynamic;
    }

    @Override
    public String getLocator() {
        return locator;
    }

    @Override
    public void setLocator(String locator) {
        Preconditions.checkArgument(StringUtils.isNoneBlank(locator), EXCEPTION_ILLEGAL_LOCATOR);
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

    @Override
    public IInteractiveContainer nextPage() {
        if (nextPage == null && context != null && IInteractiveContainer.class.isAssignableFrom(context.getClass())) {
            nextPage = (IInteractiveContainer) context;
        }
        return nextPage;
    }

    @Override
    public void setNextPage(Class<? extends IInteractiveContainer> nextPage) {
        this.nextPage = arma.containersFactory().initContainer(nextPage);
    }

    @Override
    public void setNextPage(IInteractiveContainer nextPage) {
        this.nextPage = nextPage;
    }

// IListenableElement **************************************************************************************************

    @Override
    public IListenableElement addEventListener(ElementEvent event, IElementEventHandler listener) {
        LinkedHashSet<IElementEventHandler> typeListeners = eventListeners.computeIfAbsent(event, k -> new LinkedHashSet<>());
        typeListeners.add(listener);
        return this;
    }

    @Override
    public IListenableElement setEventListeners(Map<ElementEvent, LinkedHashSet<IElementEventHandler>> listeners) {
        eventListeners = new EnumMap<>(listeners);
        return this;
    }

    @Override
    public Map<ElementEvent, LinkedHashSet<IElementEventHandler>> getEventListeners() {
        return eventListeners;
    }

// INamed **************************************************************************************************************

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArmaElement setName(String name) {
        this.name = name;
        return this;
    }


// Other stuff *********************************************************************************************************

    public IThisElementDriver perform() {
        return arma.applicationContext().getBean(IThisElementDriver.class, this);
    }

    @FiresEvent(ElementEvent.ACTION)
    public <R> R perform(Function<IInteractiveElement, R> function) {
        return function.apply(this);
    }

    public IElementActionsChain action() {
        return arma.applicationContext().getBean(IElementActionsChain.class, this, true);
    }

    public IElementActionsChain actions() {
        return arma.applicationContext().getBean(IElementActionsChain.class, this);
    }

    public IElementReadActionDriver read() {
        return arma.applicationContext().getBean(IElementReadActionDriver.class, this);
    }

    public IElementWaitingDriver waitUntil() {
        return arma.applicationContext().getBean(IElementWaitingDriver.class, this);
    }

    public <R> R raw(Function<IInteractiveElement, R> function) {
        Map<ElementEvent, LinkedHashSet<IElementEventHandler>> eventHandlers = getEventListeners();
        try {
            eventListeners = Collections.emptyMap();
            return perform(function);
        } finally {
            setEventListeners(eventHandlers);
        }
    }

    public ElementBuilder elementBy() {
        return arma.elementBy(this);
    }

    public IndexedElementBuilder elementsBy() {
        return arma.elementsBy(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("locator", locator)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArmaElement)) return false;
        ArmaElement that = (ArmaElement) o;
        return optional == that.optional &&
                contextLookup == that.contextLookup &&
                Objects.equals(name, that.name) &&
                Objects.equals(getLocatorDeque(), that.getLocatorDeque());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, getLocatorDeque(), optional, contextLookup);
    }

    public Function<IInteractiveElement, String> getReader() {
        return reader;
    }

    public void setReader(Function<IInteractiveElement, String> reader) {
        this.reader = reader;
    }
}