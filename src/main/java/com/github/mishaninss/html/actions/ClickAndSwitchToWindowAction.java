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

package com.github.mishaninss.html.actions;


import com.github.mishaninss.uidriver.annotations.BrowserDriver;
import com.github.mishaninss.uidriver.annotations.ElementDriver;
import com.github.mishaninss.uidriver.interfaces.IBrowserDriver;
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClickAndSwitchToWindowAction implements AbstractAction {
    @ElementDriver
    private IElementDriver elementDriver;
    @BrowserDriver
    private IBrowserDriver browserDriver;

    @Override
    public void dispatchAction(ILocatable element, Object... args) {
        elementDriver.clickOnElement(element);
        int windowIndex = 0;
        if (args.length > 0){
            windowIndex = (int) args[0];
        }
        ArrayList<String> tabs = new ArrayList<>(browserDriver.getWindowHandles());
        browserDriver.switchToWindow(tabs.get(windowIndex-1));
    }
}
