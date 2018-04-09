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

import com.github.mishaninss.html.listeners.ElementEvent;
import com.github.mishaninss.html.listeners.FiresEvent;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;

public interface IThisElementDriver extends ILocatableWrapper {
    IThisElementDriver scrollTo();

    @FiresEvent(ElementEvent.ACTION)
    IThisElementDriver contextClick();

    @FiresEvent(ElementEvent.IS_DISPLAYED)
    boolean isDisplayed();

    @FiresEvent(ElementEvent.IS_DISPLAYED)
    boolean isDisplayed(boolean waitForElement);

    boolean isEnabled();

    @FiresEvent(ElementEvent.ACTION)
    IThisElementDriver click();

    @FiresEvent(ElementEvent.ACTION)
    IThisElementDriver simpleClick();

    @FiresEvent(ElementEvent.ACTION)
    IThisElementDriver clickWithKeyPressed(Keys key);

    @FiresEvent(ElementEvent.CHANGE_VALUE)
    IThisElementDriver sendKeys(CharSequence... keysToSend);

    @FiresEvent(ElementEvent.CHANGE_VALUE)
    IThisElementDriver clear();

    byte[] takeScreenshot();

    IThisElementDriver highlight();

    IThisElementDriver unhighlight();

    IThisElementDriver addDebugInfo(String info, String tooltip);

    IThisElementDriver removeDebugInfo();

    Point getLocation();

    @FiresEvent(ElementEvent.ACTION)
    IThisElementDriver hover();

    @FiresEvent(ElementEvent.ACTION)
    IThisElementDriver clickWithDelayElement();

    @FiresEvent(ElementEvent.ACTION)
    Object executeJs(String javaScript);

    @FiresEvent(ElementEvent.ACTION)
    IThisElementDriver jsClick();

    @FiresEvent(ElementEvent.CHANGE_VALUE)
    IThisElementDriver setValue(String value);
}
