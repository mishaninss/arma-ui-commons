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

package com.github.mishaninss.html.containers;

import com.github.mishaninss.data.UiCommonsProperties;
import com.github.mishaninss.html.containers.interfaces.IDefaultEventHandlersProvider;
import com.github.mishaninss.html.listeners.*;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DefaultEventHandlersProviderImpl implements IDefaultEventHandlersProvider {
    private final List<IElementEventHandler> defaultEventHandlers = new ArrayList<>();

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UiCommonsProperties properties;
    @Reporter
    private IReporter reporter;

    @PostConstruct
    @SuppressWarnings("unchecked")
    private void init(){
        Set<String> eventHandlersClasses = getEventHandlersDefinitions();
        if (CollectionUtils.isNotEmpty(eventHandlersClasses)){
            for(String eventHandlerClassName: eventHandlersClasses){
                try{
                    Class<? extends IElementEventHandler> eventHandlerClass = (Class<? extends IElementEventHandler>) Class.forName(eventHandlerClassName.trim());
                    defaultEventHandlers.add(applicationContext.getBean(eventHandlerClass));
                } catch (ClassNotFoundException ex){
                    reporter.warn("Provided event handler class " + eventHandlerClassName + " was not found", ex);
                } catch (ClassCastException ex){
                    reporter.warn("Provided event handler class " + eventHandlerClassName + " is not compatible with " + IElementEventHandler.class.getCanonicalName(), ex);
                }

            }
        } else {
            defaultEventHandlers.add(applicationContext.getBean(WaitingEventHandler.class));
            defaultEventHandlers.add(applicationContext.getBean(LoggingEventHandler.class));
            defaultEventHandlers.add(applicationContext.getBean(ScrollingEventHandler.class));
            if (properties.framework().debugMode) {
                defaultEventHandlers.add(applicationContext.getBean(HighlightEventHandler.class));
            }
        }
    }

    private Set<String> getEventHandlersDefinitions(){
        Set<String> eventHandlersClasses = properties.framework().defaultEventHandlers;
        if (CollectionUtils.isEmpty(eventHandlersClasses)){
            return eventHandlersClasses;
        }
        if (eventHandlersClasses.size() == 1 && StringUtils.isEmpty(eventHandlersClasses.iterator().next())){
            return new HashSet<>();
        }
        return eventHandlersClasses;
    }

    @Override
    public List<IElementEventHandler> getEventHandlers() {
        return defaultEventHandlers;
    }
}
