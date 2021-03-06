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

import com.github.mishaninss.html.containers.interfaces.IBatchElementsContainer;
import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.uidriver.annotations.ElementsDriver;
import com.github.mishaninss.uidriver.interfaces.IElementsDriver;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Sergey Mishanin
 */
@Component
@Primary
public class IndexedContainer<T extends IBatchElementsContainer> extends ArmaContainer implements Iterable<T> {
    @ElementsDriver
    protected IElementsDriver elementsDriver;
    private T wrappedContainer;
    private Map<Integer, T> indexedContainers = new HashMap<>();

    public IndexedContainer() {
    }

    public IndexedContainer(T wrappedContainer) {
        wrap(wrappedContainer);
    }

    public static String getIndexedLocator(String locator, int index) {
        return locator.contains("%d")
                ? String.format(locator, index)
                : "#" + index + "#" + locator;
    }

    public static String getLocatorForCounting(String locator) {
        return locator.contains("%d")
                ? StringUtils.replace(locator, "%d", "*")
                : locator;
    }

    @PostConstruct
    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        wrap((T) this);
    }

    void wrap(T wrappedContainer) {
        this.wrappedContainer = wrappedContainer;
    }

    @SuppressWarnings("unchecked")
    public T index(int index) {
        Preconditions.checkArgument(index != 0, "Container's index mast not be 0");
        if (index < 0) {
            index = count() + 1 + index;
        }
        return indexedContainers.computeIfAbsent(index,
                i -> {
                    T clone = arma.applicationContext().getBean((Class<T>) wrappedContainer.getClass());
                    String locator = clone.getLocator();
                    if (StringUtils.isNoneBlank(locator)) {
                        clone.setLocator(getIndexedLocator(locator, i));
                        clone.getElements().entrySet().stream()
                                .filter(entry -> !entry.getValue().useContextLookup())
                                .forEach(entry -> entry.getValue().setLocator(getIndexedLocator(wrappedContainer.getElement(entry.getKey()).getLocator(), i)));
                    } else {
                        clone.getElements()
                                .forEach((elementId, element) -> element.setLocator(getIndexedLocator(wrappedContainer.getElement(elementId).getLocator(), i)));
                    }
                    INamed.setNameIfApplicable(clone, INamed.getNameIfApplicable(clone).trim() + " [" + i + "]");
                    return clone;
                });
    }

    public List<T> getContainers() {
        int count = count();
        return IntStream.rangeClosed(1, count)
                .mapToObj(this::index)
                .collect(Collectors.toList());
    }

    public int count() {
        if (StringUtils.isNotBlank(getLocator())) {
            return elementsDriver.getElementsCount(this);
        } else if (StringUtils.isNotBlank(wrappedContainer.getLocator())) {
            return elementsDriver.getElementsCount(getLocatorForCounting(wrappedContainer.getLocator()));
        } else {
            String locator = wrappedContainer.getElements().values().iterator().next().getLocator();
            return elementsDriver.getElementsCount(getLocatorForCounting(locator));
        }
    }

    public int fastCount() {
        return arma.waiting().executeWithoutWaiting(this::count);
    }

    public List<Map<String, String>> readAll() {
        List<Map<String, String>> values = new ArrayList<>();
        int count = count();
        for (int index = 1; index <= count; index++) {
            values.add(index(index).readValues());
        }
        return values;
    }

    public <C> List<C> readAll(Class<C> clazz) throws IllegalAccessException, InstantiationException {
        List<C> values = new ArrayList<>();
        int count = count();
        for (int index = 1; index <= count; index++) {
            values.add(index(index).readValues(clazz));
        }
        return values;
    }

    public <C> List<C> readAll(Class<C> clazz, Iterable<String> elemendIds) throws IllegalAccessException, InstantiationException {
        List<C> values = new ArrayList<>();
        int count = count();
        for (int index = 1; index <= count; index++) {
            values.add(index(index).readValues(clazz, elemendIds));
        }
        return values;
    }

    public List<Map<String, String>> readAll(Iterable<String> elementIds) {
        List<Map<String, String>> values = new ArrayList<>();
        int count = count();
        for (int index = 1; index <= count; index++) {
            values.add(index(index).readValues(elementIds));
        }
        return values;
    }

    public List<String> readAll(String elementId) {
        List<String> values = new ArrayList<>();
        int count = count();
        for (int index = 1; index <= count; index++) {
            values.add(index(index).readValue(elementId));
        }
        return values;
    }

    public Optional<T> findContainer(Map<String, String> expectedValues) {
        return findContainer(container -> {
            for (Map.Entry<String, String> entry : expectedValues.entrySet()) {
                if (!StringUtils.equals(container.readValue(entry.getKey()), entry.getValue())) {
                    return false;
                }
            }
            return true;
        });
    }

    public Optional<T> findContainer(String elementId, String expectedValue) {
        return findContainer(container -> StringUtils.equals(container.readValue(elementId), expectedValue));
    }

    public Optional<T> findContainer(Predicate<T> checker) {
        return findContainerWithKnownCount(checker, count());
    }

    public Optional<T> findContainer(Predicate<T> checker, int limit) {
        int count = count();
        count = count > limit ? limit : count;
        return findContainerWithKnownCount(checker, count);
    }

    private Optional<T> findContainerWithKnownCount(Predicate<T> checker, int count) {
        for (int index = 1; index <= count; index++) {
            T container = index(index);
            if (checker.test(container)) {
                return Optional.of(container);
            }
        }
        return Optional.empty();
    }

    public List<T> findContainers(Map<String, String> expectedValues) {
        return findContainers(container -> {
            for (Map.Entry<String, String> entry : expectedValues.entrySet()) {
                if (!StringUtils.equals(container.readValue(entry.getKey()), entry.getValue())) {
                    return false;
                }
            }
            return true;
        });
    }

    public List<T> findContainers(String elementId, String expectedValue) {
        return findContainers(container -> StringUtils.equals(container.readValue(elementId), expectedValue));
    }

    public List<T> findContainers(Predicate<T> checker) {
        List<T> containers = new LinkedList<>();
        int count = count();
        for (int index = 1; index <= count; index++) {
            T container = index(index);
            if (checker.test(container)) {
                containers.add(container);
            }
        }
        return containers;
    }

    public T first() {
        return index(1);
    }

    public T last() {
        return index(count());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Iterator<T> iterator() {
        return getContainers().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        getContainers().forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return getContainers().spliterator();
    }

    public Stream<T> stream() {
        return getContainers().stream();
    }
}
