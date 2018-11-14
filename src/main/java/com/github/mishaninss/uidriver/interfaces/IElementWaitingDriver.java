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

import com.github.mishaninss.html.interfaces.IInteractiveElement;

import java.time.temporal.TemporalUnit;
import java.util.function.Function;

public interface IElementWaitingDriver {
    IElementQuietWaitingDriver quietly();

    void isVisible();

    void isVisible(long timeoutInSeconds);

    void isVisible(long timeout, TemporalUnit unit);

    void isNotVisible();

    void isNotVisible(long timeoutInSeconds);

    void isNotVisible(long timeout, TemporalUnit unit);

    void isClickable();

    void isClickable(long timeoutInSeconds);

    void isClickable(long timeout, TemporalUnit unit);

    void attributeToBeNotEmpty(String attribute);

    void attributeToBeNotEmpty(String attribute, long timeoutInSeconds);

    void attributeToBeNotEmpty(String attribute, long timeout, TemporalUnit unit);

    void attributeToBe(String attribute, String value);

    void attributeToBe(String attribute, String value, long timeoutInSeconds);

    void attributeToBe(String attribute, String value, long timeout, TemporalUnit unit);

    void attributeContains(String attribute, String value);

    void attributeContains(String attribute, String value, long timeoutInSeconds);

    void attributeContains(String attribute, String value, long timeout, TemporalUnit unit);

    <T> T condition(Function<IInteractiveElement, T> condition);

    <T> T condition(Function<IInteractiveElement, T> condition, String message);

    <T> T condition(Function<IInteractiveElement, T> condition, long timeoutInSeconds);

    <T> T condition(Function<IInteractiveElement, T> condition, long timeoutInSeconds, String message);

    <T> T condition(Function<IInteractiveElement, T> condition, long timeout, TemporalUnit unit);

    <T> T condition(Function<IInteractiveElement, T> condition, long timeout, TemporalUnit unit, String message);
}
