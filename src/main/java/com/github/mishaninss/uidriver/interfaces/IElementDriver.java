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

package com.github.mishaninss.uidriver.interfaces;

/**
 * 
 * @author Sergey Mishanin
 *
 */
public interface IElementDriver {
    String QUALIFIER = "IElementDriver";

    /**
     * Can be used for text inputs to clear the current value
     * @param element - locator of the element
     */
    IElementDriver clearElement(ILocatable element);
    
    /**
     * Simulates left click on the element
     * @param element - locator of the element
     */
    IElementDriver clickOnElement(ILocatable element);

    /**
     * Simulates left click on the element without waiting for element is clickable
     * @param element - locator of the element
     */
    IElementDriver simpleClickOnElement(ILocatable element);
    
    /**
     * Simulates right click on the element
     * @param element - locator of the element
     */
    IElementDriver contextClickOnElement(ILocatable element);
    
    /**
     * Simulates left click with a pressed key (eg. CTRL, SHIFT, ALT)
     * @param element - locator of the element
     * @param key - pressed key
     */
    IElementDriver clickOnElementWithKeyPressed(ILocatable element, CharSequence key);
    
    /**
     * Get the value of a the given attribute of the element.
     * @param element - locator of the element
     * @param attribute - name of the attribute
     * @return the value of a the given attribute
     */
    String getAttributeOfElement(ILocatable element, String attribute);
    
    /**
     * Get the visible inner text of this element, including sub-elements, without any leading or trailing whitespace.
     * @param element - locator of the element
     * @return The visible inner text of this element.
     */
    String getTextFromElement(ILocatable element);
    
    /**
     * Get the full inner text of this element, including hidden text and text from sub-elements, without any leading or trailing whitespace.
     * @param element - locator of the element
     * @return The full inner text of this element.
     */
    String getFullTextFromElement(ILocatable element);
    
    /**
     * Checks if element with specified locator is displayed on the page or not.
     * @param element - locator of the element.
     * @param waitForElement - true if you want to wait for an element existence; 
     * false otherwise.
     * @return true if element exists on the page and displayed; false otherwise.
     */
    boolean isElementDisplayed(ILocatable element, boolean waitForElement);
    
    /**
     * Checks if the element is displayed on the page or not.
     * @param element - locator of the element.
     * @return true if the element exists on the page and displayed; false otherwise.
     */
    boolean isElementDisplayed(ILocatable element);
    
    /**
     * Checks if the element is enabled or not.
     * @param element - locator of the element.
     * @return true if the element is enabled; false otherwise.
     */
    boolean isElementEnabled(ILocatable element);
    
    /**
     * Checks if the element is selected or not.
     * @param element - locator of the element.
     * @return true if the element is selected; false otherwise.
     */
    boolean isElementSelected(ILocatable element);

    /**
     * Simulates typing into an element
     * @param element - locator of the element
     * @param keysToSend - keys to send
     */
    IElementDriver sendKeysToElement(ILocatable element, CharSequence... keysToSend);
    
    /**
     * Performs scrolling to make the element visible on screen 
     * @param element - locator of the element
     */
    IElementDriver scrollToElement(ILocatable element);

    byte[] takeElementScreenshot(ILocatable element);

    void clearCache();

    void highlightElement(ILocatable element);

    void unhighlightElement(ILocatable element);

    String getTagName(ILocatable element);

    IPoint getLocation(ILocatable element);

    IElementDriver hoverElement(ILocatable element);

    IElementDriver clickWithDelayElement(ILocatable element);

    void addElementDebugInfo(ILocatable element, final String info, final String tooltip);

    void removeElementDebugInfo();

    Object executeJsOnElement(String javaScript, ILocatable element);

    IElementDriver setValueToElement(ILocatable element, String value);
}