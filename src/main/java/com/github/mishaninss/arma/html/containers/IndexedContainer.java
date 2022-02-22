package com.github.mishaninss.arma.html.containers;

import com.github.mishaninss.arma.html.containers.interfaces.IBatchElementsContainer;
import com.github.mishaninss.arma.html.interfaces.INamed;
import com.github.mishaninss.arma.uidriver.annotations.ElementsDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementsDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author Sergey Mishanin
 */
@Component
@Primary
public class IndexedContainer<T extends IBatchElementsContainer> extends ArmaContainer implements
    Iterable<T>, InitializingBean {

  @ElementsDriver
  protected IElementsDriver elementsDriver;
  private T wrappedContainer;
  private final Map<Integer, T> indexedContainers = new HashMap<>();

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

  @Override
  public void afterPropertiesSet() {
    super.afterPropertiesSet();
    wrap((T) this);
  }

  void wrap(T wrappedContainer) {
    this.wrappedContainer = wrappedContainer;
  }

  @SuppressWarnings("unchecked")
  public T index(int index) {
    Preconditions.checkArgument(index != 0, "Индекс контейнера не должен быть равен нулю");
    if (index < 0) {
      index = count() + 1 + index;
    }
    return indexedContainers.computeIfAbsent(index,
        i -> {
          T clone = containersFactory.cloneContainer(wrappedContainer);
          String locator = clone.getLocator();
          if (StringUtils.isNotBlank(locator)) {
            clone.setLocator(getIndexedLocator(locator, i));
            clone.getElements().entrySet().stream()
                .filter(entry -> !entry.getValue().useContextLookup())
                .forEach(entry -> entry.getValue().setLocator(
                    getIndexedLocator(wrappedContainer.getElement(entry.getKey()).getLocator(),
                        i)));
          } else {
            clone.getElements()
                .forEach((elementId, element) -> element.setLocator(
                    getIndexedLocator(wrappedContainer.getElement(elementId).getLocator(), i)));
          }
          INamed.setNameIfApplicable(clone,
              INamed.getNameIfApplicable(clone).trim() + " [" + i + "]");
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
    return waitingDriver.executeWithoutWaiting(this::count);
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

  public <C> List<C> readAll(Class<C> clazz, Iterable<String> elemendIds)
      throws IllegalAccessException, InstantiationException {
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
    return findContainer(
        container -> StringUtils.equals(container.readValue(elementId), expectedValue));
  }

  public Optional<T> findContainer(Predicate<T> checker) {
    return findContainerWithKnownCount(checker, count());
  }

  public Optional<T> findContainer(Predicate<T> checker, int limit) {
    int count = Math.min(count(), limit);
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
    return findContainers(
        container -> StringUtils.equals(container.readValue(elementId), expectedValue));
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

  @Override
  public void setContext(ILocatable context) {
    super.setContext(context);
  }

  @Override
  public void setLocator(String locator) {
    super.setLocator(locator);
    if (wrappedContainer instanceof IndexedContainer) {
      ((IndexedContainer) wrappedContainer).superSetLocator(locator);
    }
  }

  private void superSetLocator(String locator) {
    super.setLocator(locator);
  }
}
