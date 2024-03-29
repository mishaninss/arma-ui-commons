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

package com.github.mishaninss.arma.html.elements;

import com.github.mishaninss.arma.html.elements.interfaces.IEditable;
import com.github.mishaninss.arma.html.elements.interfaces.IReadable;
import com.github.mishaninss.arma.html.listeners.ElementEvent;
import com.github.mishaninss.arma.html.listeners.FiresEvent;
import com.github.mishaninss.arma.html.containers.annotations.Element;
import com.github.mishaninss.arma.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.readers.AttributeReader;
import com.github.mishaninss.arma.utils.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;

@Element
@Primary
public class TextBox extends ArmaElement implements IEditable, IReadable, InitializingBean {

    public TextBox() {
    }

    public TextBox(String locator) {
        super(locator);
    }

    public TextBox(String locator, IInteractiveContainer context) {
        super(locator, context);
    }

    public TextBox(IInteractiveElement element) {
        super(element);
    }

    @Override
    public void afterPropertiesSet() {
        reader = arma.applicationContext().getBean(AttributeReader.class, AttributeReader.VALUE);
    }

    @Override
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public void changeValue(@Nullable Object value) {
        arma.element().clearElement(this);
        arma.element().sendKeysToElement(this, String.valueOf(value));
    }

    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public void addText(Object value) {
        arma.element().sendKeysToElement(this, String.valueOf(value));
    }

    public void pressEnter() {
        arma.element().sendKeysToElement(this, Keys.ENTER);
    }

}
