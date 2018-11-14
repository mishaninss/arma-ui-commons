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

public interface IElementQuietWaitingDriver {
    boolean isVisible();

    boolean isVisible(long timeoutInSeconds);

    boolean isVisible(long timeout, TemporalUnit unit);

    boolean isNotVisible();

    boolean isNotVisible(long timeoutInSeconds);

    boolean isNotVisible(long timeout, TemporalUnit unit);

    boolean isClickable();

    boolean isClickable(long timeoutInSeconds);

    boolean isClickable(long timeout, TemporalUnit unit);

    boolean attributeToBeNotEmpty(String attribute);

    boolean attributeToBeNotEmpty(String attribute, long timeoutInSeconds);

    boolean attributeToBeNotEmpty(String attribute, long timeout, TemporalUnit unit);

    boolean attributeToBe(String attribute, String value);

    boolean attributeToBe(String attribute, String value, long timeoutInSeconds);

    boolean attributeToBe(String attribute, String value, long timeout, TemporalUnit unit);

    boolean attributeContains(String attribute, String value);

    boolean attributeContains(String attribute, String value, long timeoutInSeconds);

    boolean attributeContains(String attribute, String value, long timeout, TemporalUnit unit);
}
