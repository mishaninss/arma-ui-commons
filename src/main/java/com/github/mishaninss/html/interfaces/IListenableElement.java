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

package com.github.mishaninss.html.interfaces;

import com.github.mishaninss.html.listeners.ElementEvent;
import com.github.mishaninss.html.listeners.IElementEventHandler;

import java.util.List;
import java.util.Map;

/**
 * Provides common interface for an abstract UI element
 * @author Sergey Mishanin
  */
public interface IListenableElement {

    IListenableElement addEventListener(ElementEvent event, IElementEventHandler listener);

    IListenableElement setEventListeners(Map<ElementEvent, List<IElementEventHandler>> listeners);

    default IListenableElement addEventListeners(ElementEvent event, IElementEventHandler... listeners){
        for (IElementEventHandler listener: listeners){
            addEventListener(event, listener);
        }
        return this;
    }

    default IListenableElement addEventListeners(ElementEvent event, Iterable<IElementEventHandler> listeners){
        for (IElementEventHandler listener: listeners){
            addEventListener(event, listener);
        }
        return this;
    }

    Map<ElementEvent, List<IElementEventHandler>> getEventListeners();

    default List<IElementEventHandler> getEventListeners(ElementEvent event){
        return getEventListeners().get(event);
    }
}