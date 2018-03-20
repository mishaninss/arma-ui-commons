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

import com.github.mishaninss.uidriver.interfaces.ILocatable;

import java.util.Map;

/**
 * Controller for abstract set of elements
 * @author Sergey Mishanin
 */
public interface IElementsContainer extends ILocatable {

    /**
     * Returns controller with a given ID from the collection of elements.
     * @param elementId - the ID of the element
     * @return controller of the element
     */
    IInteractiveElement getElement(String elementId);

    /**
     * Returns current collection of element controllers of this container
     */
    Map<String, IInteractiveElement> getElements();

    /**
     * Adds an element controller with given ID to the collection of elements of this container
     * @param elementId - ID of an element
     * @param element - controller of an element
     * @return this container
     */
    IElementsContainer addElement(String elementId, IInteractiveElement element);

    /**
     * Adds given collection of elements to the current collection
     * @param elements - a map, where a key is an element ID and a value is an element controller
     * @return this container
     */
    IElementsContainer addElements(Map<String, IInteractiveElement> elements);

    /**
     * Checks that all non-optional input and action elements are displayed.
     * @return true if all non-optional input and action elements are displayed; false otherwise.
     */
    boolean isDisplayed();

    boolean isDisplayed(boolean shouldWait);
}