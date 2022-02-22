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

import java.time.Duration;

public interface IElementActionsChain {

  IElementActionsChain moveToElement();

  IElementActionsChain moveToElement(int xOffset, int yOffset);

  IElementActionsChain pause(long pause);

  IElementActionsChain pause(Duration duration);

  IElementActionsChain keyDown(CharSequence key);

  IElementActionsChain keyDownOnElement(CharSequence key);

  IElementActionsChain keyUp(CharSequence key);

  IElementActionsChain keyUpOnElement(CharSequence key);

  IElementActionsChain sendKeys(CharSequence... keys);

  IElementActionsChain clickAndHold();

  IElementActionsChain clickElementAndHold();

  IElementActionsChain release();

  IElementActionsChain releaseOnElement();

  IElementActionsChain click();

  IElementActionsChain clickOnElement();

  IElementActionsChain doubleClick();

  IElementActionsChain moveByOffset(int xOffset, int yOffset);

  IElementActionsChain contextClick();

  IElementActionsChain dragAndDrop(ILocatable target);

  IElementActionsChain dragAndDropBy(int xOffset, int yOffset);

  IElementActionsChain build();

  IElementActionsChain perform();
}
