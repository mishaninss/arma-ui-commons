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

package com.github.mishaninss.arma.html.interfaces;

/**
 * Controller for abstract set of elements
 * @author Sergey Mishanin
 */
public interface IInteractiveContainer extends IElementsContainer {

    /**
     * Performs actions to change value of an element with given ID
     * @param elementId - ID of an element
     * @param value - desired value
     */
    void changeValue(String elementId, Object value);

    /**
     * Performs actions to read value from an element with guven ID
     * @param elementId - ID of an element
     * @return vaue of an element
     */
    String readValue(String elementId);

    /**
     * Performs an action on element with given ID
     * @param elementId - ID of an element
     */
    void performAction(String elementId);
}