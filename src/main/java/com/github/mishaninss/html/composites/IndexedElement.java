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

package com.github.mishaninss.html.composites;

import com.github.mishaninss.html.containers.IndexedContainer;
import com.github.mishaninss.html.containers.annotations.Element;
import com.github.mishaninss.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.uidriver.interfaces.IElementsDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Element
public class IndexedElement<T extends IInteractiveElement> implements IInteractiveElement, INamed, Iterable<T> {
    @Autowired
    private IReporter reporter;
    @Autowired
    private IElementsDriver elementsDriver;
    @Autowired
    private ApplicationContext applicationContext;

	private T wrappedElement;
    private Map<Integer, T> indexedElements = new HashMap<>();
	
	public IndexedElement(T element){
		this.wrappedElement = element;
	}
	
	@SuppressWarnings("unchecked")
	public T index(int index){
        return indexedElements.computeIfAbsent(index,
            i -> {
                T clone = applicationContext.getBean((Class<T>) wrappedElement.getClass(), wrappedElement);
                clone.setLocator(IndexedContainer.getIndexedLocator(clone.getLocator(), i));
                INamed.setNameIfApplicable(clone, INamed.getNameIfApplicable(clone).trim() + " [" + i + "]");
                indexedElements.put(index, clone);
                return clone;
            });
	}

	public int count(){
        return elementsDriver.getElementsCount(IndexedContainer.getLocatorForCounting(wrappedElement.getLocator()));
    }

    public List<T> getElements(){
        int count = count();
        List<T> elements = new ArrayList<>();
        for (int index = 1; index<=count; index++){
            elements.add(index(index));
        }
        return elements;
    }

    public List<String> readValues(){
        List<String> values = new ArrayList<>();
        forEach(item -> values.add(item.readValue()));
        return values;
    }

    public void performActions(){
        forEach(IInteractiveElement::performAction);
    }

    public void changeValues(Object value){
	    forEach(item -> item.changeValue(value));
    }

    public Optional<T> findElement(String expectedValue){
	    return findElement(actualValue -> StringUtils.equals(actualValue, expectedValue));
    }

    public Optional<T> findElement(Function<String, Boolean> checker){
        int count = count();
        for (int index = 1; index<=count; index++){
            T element = index(index);
            if (checker.apply(element.readValue())){
                return Optional.of(element);
            }
        }
        return Optional.empty();
    }

    public List<T> findElements(String expectedValue){
	    return findElements(actualValue -> StringUtils.equals(actualValue, expectedValue));
    }

    public List<T> findElements(Function<String, Boolean> checker){
        List<T> elements = new LinkedList<>();
        forEach(item -> {
            if (checker.apply(item.readValue())){
                elements.add(item);
            }
        });
        return elements;
    }

    public void changeValues(Iterable<Object> values){
        AtomicInteger index = new AtomicInteger(0);
        values.forEach(value -> index(index.incrementAndGet()).changeValue(value));
    }

    @Override
    public void changeValue(Object value) {
        index(1).changeValue(value);
    }

    @Override
    public String readValue() {
        return index(1).readValue();
    }

    @Override
    public void performAction() {
        index(1).performAction();
    }

    @Override
    public boolean isDisplayed() {
        return index(1).isDisplayed();
    }

    @Override
    public boolean isDisplayed(boolean shouldWait) {
        return index(1).isDisplayed(shouldWait);
    }

    @Override
    public boolean isEnabled() {
        return index(1).isEnabled();
    }

    @Override
    public boolean isOptional() {
        return wrappedElement.isOptional();
    }

    @Override
    public void setOptional(boolean dynamic) {
        wrappedElement.setOptional(dynamic);
    }

    @Override
    public IInteractiveContainer nextPage() {
        return wrappedElement.nextPage();
    }

    @Override
    public void setNextPage(IInteractiveContainer nextPage) {
        wrappedElement.setNextPage(nextPage);
    }

    @Override
    public void setNextPage(Class<? extends IInteractiveContainer> nextPage) {
        wrappedElement.setNextPage(nextPage);
    }

    @Override
    public String getLocator() {
        return wrappedElement.getLocator();
    }

    @Override
    public void setLocator(String locator) {
        wrappedElement.setLocator(locator);
    }

    @Override
    public ILocatable getContext() {
        return wrappedElement.getContext();
    }

    @Override
    public void setContext(ILocatable context) {
        wrappedElement.setContext(context);
    }

    @Override
    public void setContextLookup(boolean contextLookup) {
        wrappedElement.setContextLookup(contextLookup);
    }

    @Override
    public boolean useContextLookup() {
        return wrappedElement.useContextLookup();
    }

    @Override
    public INamed setName(String name) {
        INamed.setNameIfApplicable(wrappedElement, name);
        return this;
    }

    @Override
    public String getName() {
        return INamed.getNameIfApplicable(wrappedElement);
    }

    @Override
    public Iterator<T> iterator() {
        return getElements().iterator();
    }
}
