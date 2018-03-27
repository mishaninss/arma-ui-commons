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

import com.github.mishaninss.html.containers.ContainersFactory;
import com.github.mishaninss.html.containers.annotations.Element;
import com.github.mishaninss.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.interfaces.IListenableElement;
import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.html.listeners.ElementEvent;
import com.github.mishaninss.html.listeners.FiresEvent;
import com.github.mishaninss.html.listeners.IElementEventHandler;
import com.github.mishaninss.html.readers.AbstractReader;
import com.github.mishaninss.html.readers.TextReader;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.uidriver.interfaces.*;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.*;

/**
 * Controller for the abstract UI Element.
 *
 * @author Sergey Mishanin
 */
@Element
public class ArmaElement implements IInteractiveElement, IListenableElement, INamed{
    @Autowired
    protected IReporter reporter;
    @Autowired
    protected IElementDriver elementDriver;
    @Autowired
    protected ContainersFactory containersFactory;
    @Autowired
    protected ApplicationContext applicationContext;
    @Resource(type = TextReader.class)
    protected AbstractReader reader;

    static final String EXCEPTION_ILLEGAL_TYPE_OF_VALUE = "Illegal type of a value [%s]";
    private static final String EXCEPTION_ILLEGAL_LOCATOR = "Locator cannot be null or empty string";

    protected String name;
    protected String locator;
    private   boolean optional = false;
    private   boolean contextLookup = true;
    private ILocatable context;
    private IInteractiveContainer nextPage;
    private Map<ElementEvent, List<IElementEventHandler>> eventListeners = new EnumMap<>(ElementEvent.class);

// Constructors ********************************************************************************************************

    protected ArmaElement(){}

    /**
     * Creates an instance of Basic element
     * @param locator - locator of the element
     */
    public ArmaElement(String locator){
        if (StringUtils.isBlank(locator)){
            throw new IllegalArgumentException(EXCEPTION_ILLEGAL_LOCATOR);
        }
        this.locator = locator;
    }

    /**
     * Creates an instance of Basic element
     * @param locator - locator of the element
     * @param context - container of the element
     */
    public ArmaElement(String locator, IInteractiveContainer context){
        this(locator);
        this.context = context;
    }

    public ArmaElement(IInteractiveElement element){
        this.locator = element.getLocator();
        this.optional = element.isOptional();
        this.contextLookup = element.useContextLookup();
        this.context = element.getContext();
        this.nextPage = element.nextPage();
        if (element instanceof IListenableElement){
            setEventListeners(((IListenableElement)element).getEventListeners());
        }
        if (element instanceof INamed){
            setName(((INamed)element).getName());
        }
        if (element instanceof ArmaElement){
            this.reader = ((ArmaElement) element).reader;
        }
    }

// IInteractiveElement *************************************************************************************************

    @Override
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public void changeValue(final Object value){
        elementDriver.sendKeysToElement(this, value.toString());
    }

    @Override
    @FiresEvent(ElementEvent.READ_VALUE)
    public String readValue(){
        return reader.readProperty(this);
    }

    @Override
    @FiresEvent(ElementEvent.ACTION)
    public void performAction(){
        elementDriver.clickOnElement(this);
    }

    @Override
    @FiresEvent(ElementEvent.IS_DISPLAYED)
    public boolean isDisplayed(){
        return elementDriver.isElementDisplayed(this);
    }

    @Override
    @FiresEvent(ElementEvent.IS_DISPLAYED)
    public boolean isDisplayed(boolean shouldWait){
        return elementDriver.isElementDisplayed(this, shouldWait);
    }

    @Override
    public boolean isEnabled(){
        return elementDriver.isElementEnabled(this);
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
    public IInteractiveContainer nextPage(){
        if (nextPage == null && IInteractiveContainer.class.isAssignableFrom(context.getClass())){
            nextPage = (IInteractiveContainer) context;
        }
        return nextPage;
    }

    @Override
    public void setNextPage(Class<? extends IInteractiveContainer> nextPage) {
        this.nextPage = containersFactory.initContainer(nextPage);
    }

    @Override
    public void setNextPage(IInteractiveContainer nextPage) {
        this.nextPage = nextPage;
    }

// IListenableElement **************************************************************************************************

    @Override
    public IListenableElement addEventListener(ElementEvent event, IElementEventHandler listener) {
        List<IElementEventHandler> typeListeners = eventListeners.computeIfAbsent(event, k -> new LinkedList<>());
        typeListeners.add(listener);
        return this;
    }

    @Override
    public IListenableElement setEventListeners(Map<ElementEvent, List<IElementEventHandler>> listeners) {
        eventListeners = new EnumMap<>(listeners);
        return this;
    }

    @Override
    public Map<ElementEvent, List<IElementEventHandler>> getEventListeners() {
        return eventListeners;
    }

// INamed **************************************************************************************************************

    @Override
    public ArmaElement setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }


// Other stuff *********************************************************************************************************

    public IThisElementDriver perform(){
        return applicationContext.getBean(IThisElementDriver.class, this);
    }

    public IElementActionsChain action(){
        return applicationContext.getBean(IElementActionsChain.class, this, true);
    }

    public IElementActionsChain actions(){
        return applicationContext.getBean(IElementActionsChain.class, this);
    }

    public IElementReadActionDriver read(){
        return applicationContext.getBean(IElementReadActionDriver.class, this);
    }

    public IElementWaitingDriver waitUntil(){
        return applicationContext.getBean(IElementWaitingDriver.class, this);
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

    public AbstractReader getReader() {
        return reader;
    }

    public void setReader(AbstractReader reader) {
        this.reader = reader;
    }
}