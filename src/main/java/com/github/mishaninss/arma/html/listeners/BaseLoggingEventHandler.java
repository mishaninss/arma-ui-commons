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

package com.github.mishaninss.arma.html.listeners;

import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.interfaces.INamed;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public abstract class BaseLoggingEventHandler implements IElementEventHandler {
    private static final String MESSAGE_PERFORM_ACTION = "%s -> выполнить %s";
    private static final String MESSAGE_SET_VALUE = "%s -> %s: %s";
    private static final String MESSAGE_GET_VALUE = "%s -> прочитать %s: %s";
    private static final String MESSAGE_IS_DISPLAYED = "%s -> проверить отображение: %s";

    protected abstract void logMessage(String message);

    @Override
    public void beforeEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        String message;
        String actionName;
        switch (event){
            case CHANGE_VALUE:
                actionName = StringUtils.isNoneBlank(comment) ? comment: "изменить значение";
                message = getLogMessage(MESSAGE_SET_VALUE, element, actionName, args);
                logMessage(message);
                break;
            case ACTION:
                actionName = StringUtils.isNoneBlank(comment) ? StringUtils.stripStart(comment, "perform").trim(): "действие";
                message = getLogMessage(MESSAGE_PERFORM_ACTION, element, actionName);
                logMessage(message);
                break;
            default:
        }
    }

    @Override
    public void afterEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        String message;
        switch (event){
            case READ_VALUE:
                String actionName = StringUtils.isNoneBlank(comment) ? StringUtils.removeStart(comment, "read").trim(): "значение";
                message = getLogMessage(MESSAGE_GET_VALUE, element, actionName, args);
                logMessage(message);
                break;
            case IS_DISPLAYED:
                message = getLogMessage(MESSAGE_IS_DISPLAYED, element, args);
                logMessage(message);
                break;
            default:
        }
    }

    private String getLogMessage(String format, Object... args){
        for (int i=0; i<args.length; i++){
            Object arg = args[i];
            if (arg instanceof ILocatable){
                args[i] = INamed.getLoggableNameIfApplicable(arg);
            } else if (arg.getClass().isArray()){
                args[i] = Arrays.deepToString((Object[])arg);
            }
        }
        return String.format(format, args);
    }
}