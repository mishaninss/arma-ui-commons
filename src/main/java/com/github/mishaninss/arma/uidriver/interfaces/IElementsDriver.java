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

package com.github.mishaninss.arma.uidriver.interfaces;

import java.util.Map;

/**
 * 
 * @author Sergey Mishanin
 *
 */
public interface IElementsDriver {
    String QUALIFIER = "IElementsDriver";

    /**
     * Can be used for text inputs to clear the current value
     * @param locator - locator of elements
     */
    IElementsDriver clearElements(String locator);
    
    /**
     * Simulates left click on the element
     * @param locator - locator of elements
     */
    IElementsDriver clickOnElements(String locator);
    
    /**
     * Simulates left click with a pressed key (eg. CTRL, SHIFT, ALT)
     * @param locator - locator of elements
     * @param key - pressed key
     */
    IElementsDriver clickOnElementsWithKeyPressed(String locator, CharSequence key);
    
    /**
     * Get the value of a the given attribute of elements.
     * @param locator - locator of  element
     * @param attribute - name of the attribute
     * @return the value of a the given attribute
     */
    String[] getAttributeOfElements(String locator, String attribute);
    
    /**
     * Get the visible inner text of elements, including sub-elements, without any leading or trailing whitespace.
     * @param locator - locator of elements
     * @return The visible inner text elements.
     */
    String[] getTextFromElements(String locator);
    
    /**
     * Get the full inner text of elements, including hidden text and text from sub-elements, without any leading or trailing whitespace.
     * @param locator - locator of elements
     * @return The full inner text of elements.
     */
    String[] getFullTextFromElements(String locator);
    
    /**
     * Checks if all elements with specified locator are displayed on the page.
     * @param locator - locator of elements.
     * @param waitForElement - true if you want to wait for element existence; 
     * false otherwise.
     * @return true if elements exist on the page and displayed; false otherwise.
     */
    boolean areElementsDisplayed(final String locator, boolean waitForElement);
    
    /**
     * Checks if all elements are displayed on the page or not.
     * @param locator - locator of elements.
     * @return true if elements exist on the page and displayed; false otherwise.
     */
    boolean areElementsDisplayed(final String locator);
    
    
    /**
     * Checks if all elements with specified locator are enabled.
     * @param locator - locator of elements.
     * @return true if elements are enabled; false otherwise.
     */
    boolean areElementsEnabled(final String locator);
    
    /**
     * Checks if all element with specified locator are selected.
     * @param locator - locator of elements.
     * @return true if elements are selected; false otherwise.
     */
    boolean areElementsSelected(final String locator);

    /**
     * Simulates typing into elements
     * @param locator - locator of elements 
     * @param keysToSend - keys to send
     */
    IElementsDriver sendKeysToElements(String locator, CharSequence... keysToSend);
    
    /**
     * Gets count of elements with specified locator
     * @param locator - locator of elements 
     */
    int getElementsCount(String locator);

    int getElementsCount(ILocatable element);

    Map<String,String> getSrcOfAllImages();
}