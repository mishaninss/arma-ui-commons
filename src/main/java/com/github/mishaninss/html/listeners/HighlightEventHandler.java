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
import com.github.mishaninss.utils.GenericUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class HighlightEventHandler implements IElementEventHandler {
    private static final String CHANGE_VALUE_MESSAGE = "Change value: %s";
    private static final String READ_VALUE_MESSAGE = "Read %s";
    private static final String PERFORM_ACTION_MESSAGE = "Perform %s";
    private static final String IS_DISPLAYED_MESSAGE = "Check if displayed";

    @ElementDriver
    private IElementDriver elementDriver;

    @Override
    public void beforeEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        String actionName;
        if (elementDriver.isElementDisplayed(element, false)) {
            switch (event) {
                case CHANGE_VALUE:
                    elementDriver.highlightElement(element);
                    Object value = ArrayUtils.isNotEmpty(args) ? args[0] : "";
                    elementDriver.addElementDebugInfo(element, String.format(CHANGE_VALUE_MESSAGE, value), "");
                    break;
                case READ_VALUE:
                    actionName = StringUtils.isNoneBlank(comment) ? StringUtils.stripStart(comment, "read").trim() : "value";
                    elementDriver.highlightElement(element);
                    elementDriver.addElementDebugInfo(element, String.format(READ_VALUE_MESSAGE, actionName), "");
                    break;
                case ACTION:
                    actionName = StringUtils.isNoneBlank(comment) ? StringUtils.stripStart(comment, "perform").trim() : "value";
                    elementDriver.highlightElement(element);
                    elementDriver.addElementDebugInfo(element, String.format(PERFORM_ACTION_MESSAGE, actionName), "");
                    break;
                default:
            }
        }
        GenericUtils.pause(TimeUnit.MILLISECONDS,700);
    }

    @Override
    public void afterEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        switch (event){
            case CHANGE_VALUE:
            case READ_VALUE:
            case ACTION:
                if (elementDriver.isElementDisplayed(element, false)) {
                    elementDriver.unhighlightElement(element);
                }
                elementDriver.removeElementDebugInfo();
                break;
            case IS_DISPLAYED:
                if (args.length > 0 && args[0] instanceof Boolean && (boolean)args[0]) {
                    elementDriver.highlightElement(element);
                    elementDriver.addElementDebugInfo(element, IS_DISPLAYED_MESSAGE, "");
                    GenericUtils.pause(TimeUnit.MILLISECONDS,700);
                    elementDriver.unhighlightElement(element);
                    elementDriver.removeElementDebugInfo();
                }
                break;
            default:
        }
    }
}
