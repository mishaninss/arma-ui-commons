package com.github.mishaninss.arma.html.composites;

import com.github.mishaninss.arma.html.containers.IndexedContainer;
import com.github.mishaninss.arma.html.containers.annotations.Element;
import com.github.mishaninss.arma.html.elements.ElementBuilder;
import com.github.mishaninss.arma.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.interfaces.INamed;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.annotations.ElementsDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementsDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Element
@SuppressWarnings("unused")
public class IndexedElement<T extends IInteractiveElement> implements IInteractiveElement, INamed,
    Iterable<T> {

  @Reporter
  private IReporter reporter;
  @ElementsDriver
  private IElementsDriver elementsDriver;
  @Autowired
  private ElementBuilder elementBuilder;

  private final T wrappedElement;
  private final Map<Integer, T> indexedElements = new HashMap<>();

  public IndexedElement(T element) {
    this.wrappedElement = element;
  }

  @SuppressWarnings("unchecked")
  public T index(int index) {
    Preconditions.checkArgument(index != 0, "Индекс элемента не может быть равен нулю");
    if (index < 0) {
      index = count() + 1 + index;
    }
    return indexedElements.computeIfAbsent(index,
        i -> {
          T clone = elementBuilder.clone(wrappedElement);
          clone.setLocator(IndexedContainer.getIndexedLocator(clone.getLocator(), i));
          INamed.setNameIfApplicable(clone,
              INamed.getNameIfApplicable(clone).trim() + " [" + i + "]");
          return clone;
        });
  }

  public int count() {
    return elementsDriver.getElementsCount(wrappedElement);
  }

  public List<T> getElements() {
    return IntStream.rangeClosed(1, count())
        .mapToObj(this::index)
        .collect(Collectors.toList());
  }

  public List<String> readValues() {
    List<String> values = new ArrayList<>();
    forEach(item -> values.add(item.readValue()));
    return values;
  }

  public void performActions() {
    forEach(IInteractiveElement::performAction);
  }

  public void changeValues(Object value) {
    forEach(item -> item.changeValue(value));
  }

  public void changeValues(Iterable<Object> values) {
    AtomicInteger index = new AtomicInteger(0);
    values.forEach(value -> index(index.incrementAndGet()).changeValue(value));
  }

  public Optional<T> findElement(String expectedValue) {
    return findElement(actualValue -> StringUtils.equals(actualValue, expectedValue));
  }

  public Optional<T> findElement(Predicate<String> checker) {
    int count = count();
    for (int index = 1; index <= count; index++) {
      T element = index(index);
      if (checker.test(element.readValue())) {
        return Optional.of(element);
      }
    }
    return Optional.empty();
  }

  public List<T> findElements(String expectedValue) {
    return findElements(actualValue -> StringUtils.equals(actualValue, expectedValue));
  }

  public List<T> findElements(Predicate<String> checker) {
    return getElements().stream()
        .filter(element -> checker.test(element.readValue()))
        .collect(Collectors.toList());
  }

  public List<Integer> findIndexes(Predicate<String> checker) {
    int count = count();
    List<Integer> indexes = new ArrayList<>();
    for (int i = 1; i <= count; i++) {
      if (checker.test(index(i).readValue())) {
        indexes.add(i);
      }
    }
    return indexes;
  }

  public List<Integer> findIndexes(String expected) {
    return findIndexes(v -> StringUtils.equals(v, expected));
  }

  public int findIndex(Predicate<String> predicate) {
    int count = count();
    for (int i = 1; i <= count; i++) {
      if (predicate.test(index(i).readValue())) {
        return i;
      }
    }
    return 0;
  }

  public int findIndex(String expected) {
    return findIndex(v -> StringUtils.equals(v, expected));
  }

  public List<Integer> findIndexesOfAllValues(Collection<String> values) {
    int count = count();
    List<Integer> indexes = new ArrayList<>();
    List<String> newValues = new ArrayList<>(values);
    for (int i = 1; i <= count; i++) {
      if (newValues.remove(index(i).readValue())) {
        indexes.add(i);
      }
      if (newValues.isEmpty()) {
        break;
      }
    }
    if (!newValues.isEmpty()) {
      throw new RuntimeException(
          "List of " + this.getLoggableName() + " elements doesn't contain values: " + newValues);
    }
    return indexes;
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

  public Stream<T> stream() {
    return getElements().stream();
  }

  public T first() {
    return index(1);
  }

  public T last() {
    return index(count());
  }
}
