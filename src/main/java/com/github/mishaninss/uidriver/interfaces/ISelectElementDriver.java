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
public interface ISelectElementDriver 
{

    ISelectElementDriver selectByIndex(ILocatable element, int index);

    ISelectElementDriver deselectAll(String locator);

    ISelectElementDriver deselectAll(ILocatable element);

    ISelectElementDriver deselectByValue(String locator, String value);
    
    ISelectElementDriver deselectByVisibleText(String locator, String text);
    
    ISelectElementDriver deselectByIndex(String locator, int index);

    ISelectElementDriver selectByValue(String locator, String value);

    ISelectElementDriver selectByValue(ILocatable element, String value);

    ISelectElementDriver selectByVisibleText(String locator, String text);

    ISelectElementDriver selectByVisibleText(ILocatable element, String text);

    ISelectElementDriver selectByIndex(String locator, int index);
    
    String[] getAllSelectedOptions(String locator);
    
    String[] getOptions(String locator);
    
}
