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

package com.github.mishaninss.html.listeners;

import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.uidriver.annotations.ElementDriver;
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import org.springframework.stereotype.Component;

@Component
public class ScrollingEventHandler implements IElementEventHandler {
    @ElementDriver
    private IElementDriver elementDriver;

    @Override
    public void beforeEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        if (event != ElementEvent.IS_DISPLAYED){
            elementDriver.scrollToElement(element);
        }
    }

    @Override
    public void afterEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        if (event == ElementEvent.IS_DISPLAYED && args.length > 0 && args[0] instanceof Boolean && (boolean)args[0]){
            elementDriver.scrollToElement(element);
        }
    }
}
