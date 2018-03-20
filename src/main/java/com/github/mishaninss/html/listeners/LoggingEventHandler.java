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

import com.github.mishaninss.html.actions.AbstractAction;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggingEventHandler implements IElementEventHandler {
    private static Map<Class<? extends AbstractAction>, String> actionNames = new HashMap<>();
    private static final String MESSAGE_PERFORM_ACTION = "Perform %s on %s";
    private static final String MESSAGE_SET_VALUE = "Set value to %s: %s";
    private static final String MESSAGE_GET_VALUE = "Get value from %s: %s";
    private static final String MESSAGE_IS_DISPLAYED = "Check if element %s is displayed: %s";

    @Autowired
    private IReporter reporter;

    @Override
    public void beforeEvent(IInteractiveElement element, ElementEvent event, Object... args) {
        String message;
        switch (event){
            case CHANGE_VALUE:
                message = getLogMessage(MESSAGE_SET_VALUE, element, args);
                reporter.info(message);
                break;
            case ACTION:
                String actionName = "action";
                if (ArrayUtils.isNotEmpty(args) && args[0] instanceof AbstractAction){
                    actionName = getActionName((AbstractAction) args[0]);
                }
                message = getLogMessage(MESSAGE_PERFORM_ACTION, actionName, element);
                reporter.info(message);
                break;
            default:
        }
    }

    @Override
    public void afterEvent(IInteractiveElement element, ElementEvent event, Object... args) {
        String message;
        switch (event){
            case READ_VALUE:
                message = getLogMessage(MESSAGE_GET_VALUE, element, args);
                reporter.info(message);
                break;
            case IS_DISPLAYED:
                message = getLogMessage(MESSAGE_IS_DISPLAYED, element, args);
                reporter.info(message);
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

    private static String getActionName(AbstractAction action){
        return actionNames.computeIfAbsent(action.getClass(),
                aClass ->
                        StringUtils.join(
                                StringUtils.splitByCharacterTypeCamelCase(aClass.getSimpleName()), " ")
                                .toLowerCase()
                                .replace("action", "")
                                .trim()
        );
    }
}
