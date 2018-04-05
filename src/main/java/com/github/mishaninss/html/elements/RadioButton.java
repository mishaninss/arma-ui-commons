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

package com.github.mishaninss.html.elements;

import com.github.mishaninss.html.containers.annotations.Element;
import com.github.mishaninss.html.elements.interfaces.IEditable;
import com.github.mishaninss.html.elements.interfaces.IReadable;
import com.github.mishaninss.html.elements.interfaces.ISelectable;
import com.github.mishaninss.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.listeners.ElementEvent;
import com.github.mishaninss.html.listeners.FiresEvent;

@Element
public class RadioButton extends ArmaElement implements ISelectable, IEditable, IReadable {

    public RadioButton(){}
    
    public RadioButton(final String baseLocator) {
        super(baseLocator);
    }

    public RadioButton(String locator, IInteractiveContainer context) {
        super(locator, context);
    }

    public RadioButton(IInteractiveElement element){
        super(element);
    }

    @Override
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public void changeValue(Object value) {
        if (value instanceof Boolean){
            setValue((Boolean)value);
        } else if (value instanceof String){
            setValue(Boolean.parseBoolean(value.toString()));
        } else {
            throw new IllegalArgumentException(String.format(EXCEPTION_ILLEGAL_TYPE_OF_VALUE, value.getClass().getCanonicalName()));
        }
    }

    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public RadioButton setValue(boolean value) {
        if (value){
            select();
        } else {
            deselect();
        }
        return this;
    }

    @Override
    @FiresEvent(ElementEvent.READ_VALUE)
    public String readValue() {
        return String.valueOf(isSelected());
    }

    @Override
    public boolean isSelected() {
        return elementDriver.isElementSelected(this);
    }

    @Override
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public RadioButton select() {
        if (!isSelected()){
            performAction();
        }
        return this;
    }

    @Override
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public RadioButton deselect() {
        if (isSelected()){
            performAction();
        }
        return this;
    }

}