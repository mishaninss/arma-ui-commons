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

package com.github.mishaninss.uidriver.interfaces;

import java.time.temporal.TemporalUnit;
import java.util.function.BiConsumer;

public interface IWaitingDriver {
    String QUALIFIER = "IWaitingDriver";

    void setWaitForPageUpdateMethod(BiConsumer<Long, TemporalUnit> method);

    void waitForElementIsVisible(ILocatable element);

    void waitForElementIsVisible(ILocatable element, long timeoutInSeconds);

    void waitForElementIsVisible(ILocatable element, long timeout, TemporalUnit unit);

    void waitForElementIsNotVisible(ILocatable element);

    void waitForElementIsNotVisible(ILocatable element, long timeoutInSeconds);

    void waitForElementIsNotVisible(ILocatable element, long timeout, TemporalUnit unit);

    void waitForElementIsClickable(ILocatable element);

    void waitForElementIsClickable(ILocatable element, long timeoutInSeconds);

    void waitForElementIsClickable(ILocatable element, long timeout, TemporalUnit unit);

    void waitForElementToBeSelected(ILocatable element);

    void waitForElementToBeSelected(ILocatable element, long timeoutInSeconds);

    void waitForElementToBeSelected(ILocatable element, long timeout, TemporalUnit unit);

    void waitForElementToBeNotSelected(ILocatable element);

    void waitForElementToBeNotSelected(ILocatable element, long timeoutInSeconds);

    void waitForElementToBeNotSelected(ILocatable element, long timeout, TemporalUnit unit);

    void waitForAlertIsPresent();

    void waitForAlertIsPresent(long timeoutInSeconds);

    void waitForAlertIsPresent(long timeout, TemporalUnit unit);

    void waitForPageUpdate();

    void waitForPageUpdate(long timeoutInSeconds);

    void waitForPageUpdate(long timeout, TemporalUnit unit);
}
