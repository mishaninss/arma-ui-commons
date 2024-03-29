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

package com.github.mishaninss.arma.uidriver.interfaces;

import java.time.temporal.TemporalUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public interface IWaitingDriver extends InitializingBean, DisposableBean {

  String QUALIFIER = "IWaitingDriver";

  ThreadLocal<IWaitingDriver> INSTANCES = new InheritableThreadLocal<>();

  @Override
  default void destroy() throws Exception {
    INSTANCES.remove();
  }

  @Override
  default void afterPropertiesSet() throws Exception {
    INSTANCES.set(this);
  }

  static IWaitingDriver get() {
    return INSTANCES.get();
  }

  /**
   * Use this method to specify Java Script to check if page is updated
   *
   * @param script - Java Script must return true, if page is updated or false otherwise
   */
  void setWaitForPageUpdateScript(String script);

  void setWaitForPageUpdateMethod(BiConsumer<Long, TemporalUnit> method);

  void waitForElementIsVisible(ILocatable element);

  void waitForElementIsVisible(ILocatable element, long timeoutInSeconds);

  void waitForElementIsVisible(ILocatable element, long timeout, TemporalUnit unit);

  void waitForElementIsNotVisible(ILocatable element);

  void waitForElementIsNotVisible(ILocatable element, String message);

  void waitForElementIsNotVisible(ILocatable element, long timeoutInSeconds, String message);

  void waitForElementIsNotVisible(ILocatable element, long timeoutInSeconds);

  void waitForElementIsNotVisible(ILocatable element, long timeout, TemporalUnit unit);

  void waitForElementIsNotVisible(ILocatable element, long timeout, TemporalUnit unit,
      String message);

  void waitForElementIsClickable(ILocatable element);

  void waitForElementIsClickable(ILocatable element, long timeoutInSeconds);

  void waitForElementIsClickable(ILocatable element, long timeout, TemporalUnit unit);

  void waitForElementToBeSelected(ILocatable element);

  void waitForElementToBeSelected(ILocatable element, long timeoutInSeconds);

  void waitForElementToBeSelected(ILocatable element, long timeout, TemporalUnit unit);

  void waitForElementToBeNotSelected(ILocatable element);

  void waitForElementToBeNotSelected(ILocatable element, long timeoutInSeconds);

  void waitForElementToBeNotSelected(ILocatable element, long timeout, TemporalUnit unit);

  void waitForElementAttributeToBeNotEmpty(ILocatable element, String attribute);

  void waitForElementAttributeToBe(ILocatable element, String attribute, String value);

  void waitForElementAttributeToBeNotEmpty(ILocatable element, String attribute,
      long timeoutInSeconds);

  void waitForElementAttributeToBeNotEmpty(ILocatable element, String attribute, long timeout,
      TemporalUnit unit);

  void waitForUrlToBe(String url);

  void waitForUrlToBe(String url, long timeoutInSeconds);

  void waitForUrlToBe(String url, long timeout, TemporalUnit unit);

  void waitForAlertIsPresent();

  void waitForAlertIsPresent(long timeoutInSeconds);

  void waitForAlertIsPresent(long timeout, TemporalUnit unit);

  void waitForElementAttributeToBe(ILocatable element, String attribute, String value,
      long timeoutInSeconds);

  void waitForElementAttributeToBe(ILocatable element, String attribute, String value, long timeout,
      TemporalUnit unit);

  void waitForElementAttributeContains(ILocatable element, String attribute, String value);

  void waitForElementAttributeContains(ILocatable element, String attribute, String value,
      long timeoutInSeconds);

  void waitForElementAttributeContains(ILocatable element, String attribute, String value,
      long timeout, TemporalUnit unit);

  <T> T waitForCondition(Supplier<T> condition);

  <T, R> R waitForCondition(Function<T, R> condition, T arg);

  <T> T waitForCondition(Supplier<T> condition, String message);

  <T, R> R waitForCondition(Function<T, R> condition, T arg, String message);

  <T> T waitForCondition(Supplier<T> condition, long timeoutInSeconds);

  <T, R> R waitForCondition(Function<T, R> condition, T arg, long timeoutInSeconds);

  <T> T waitForCondition(Supplier<T> condition, long timeoutInSeconds, String message);

  <T, R> R waitForCondition(Function<T, R> condition, T arg, long timeoutInSeconds, String message);

  <T> T waitForCondition(Supplier<T> condition, long timeout, TemporalUnit unit);

  <T, R> R waitForCondition(Function<T, R> condition, T arg, long timeout, TemporalUnit unit);

  <T, R> R waitForCondition(Function<T, R> condition, T arg, long timeout, TemporalUnit unit,
      String message);

  <T> T waitForCondition(Supplier<T> condition, long timeout, TemporalUnit unit, String message);

  void waitForPageUpdate();

  void waitForPageUpdate(long timeoutInSeconds);

  void waitForPageUpdate(long timeout, TemporalUnit unit);

  <T> T executeWithoutWaiting(Supplier<T> supplier);

  void executeWithoutWaiting(Runnable runnable);

  void waitForElementExists(ILocatable element);

  void waitForElementExists(ILocatable element, long timeoutInSeconds);

  void waitForElementExists(ILocatable element, long timeout, TemporalUnit unit);
}
