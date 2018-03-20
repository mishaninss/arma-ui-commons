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

import java.time.Duration;

public interface IActionsChain {
    IActionsChain click(ILocatable element);

    IActionsChain moveToElement(ILocatable element);

    IActionsChain moveToElement(ILocatable element, int xOffset, int yOffset);

    IActionsChain pause(long pause);

    IActionsChain pause(Duration duration);

    IActionsChain keyDown(CharSequence key);

    IActionsChain keyDown(ILocatable target, CharSequence key);

    IActionsChain keyUp(CharSequence key);

    IActionsChain keyUp(ILocatable target, CharSequence key);

    IActionsChain sendKeys(CharSequence... keys);

    IActionsChain sendKeys(ILocatable target, CharSequence... keys);

    IActionsChain clickAndHold(ILocatable target);

    IActionsChain clickAndHold();

    IActionsChain release(ILocatable target);

    IActionsChain release();

    IActionsChain click();

    IActionsChain doubleClick(ILocatable target);

    IActionsChain doubleClick();

    IActionsChain moveByOffset(int xOffset, int yOffset);

    IActionsChain contextClick(ILocatable target);

    IActionsChain contextClick();

    IActionsChain dragAndDrop(ILocatable source, ILocatable target);

    IActionsChain dragAndDropBy(ILocatable source, int xOffset, int yOffset);

    IActionsChain build();

    IActionsChain perform();
}
