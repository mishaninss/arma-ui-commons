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

package com.github.mishaninss.html.containers.interfaces;

import com.github.mishaninss.html.interfaces.IInteractiveContainer;

import java.util.Map;

/**
 * Controller for abstract set of elements
 *
 * @author Sergey Mishanin
 */
public interface IBatchElementsContainer extends IInteractiveContainer {

    /**
     * Performs actions to change values of elements with given IDs
     *
     * @param inputData - a key-value map, where a key is and ID of an element, and value is a desired value
     * @return this container
     */
    IBatchElementsContainer changeValues(Map<String, ?> inputData);

    /**
     * Performs actions to read values of all elements in this container
     *
     * @return a key-value map, where a key is and ID of an element, and value is a value of an element
     */
    Map<String, String> readValues();

    Map<String, String> readValues(Iterable<String> elementIds);

    <T> T readValues(T object);

    <T> T readValues(Class<T> clazz) throws InstantiationException, IllegalAccessException;

    <T> T readValues(Class<T> clazz, Iterable<String> elementIds) throws InstantiationException, IllegalAccessException;
}