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

import com.github.mishaninss.arma.html.elements.interfaces.IReadable;
import com.github.mishaninss.arma.html.containers.annotations.Element;
import com.github.mishaninss.arma.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.readers.AttributeReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Primary;

/**
 * Controller for the simple Button control.
 *
 * @author Sergey Mishanin
 */
@Element
@Primary
public class Button extends ArmaElement implements IReadable, InitializingBean {

    public Button() {
    }

    public Button(String locator) {
        super(locator);
    }

    public Button(String locator, IInteractiveContainer container) {
        super(locator, container);
    }

    public Button(IInteractiveElement element) {
        super(element);
    }

    @Override
    public void afterPropertiesSet() {
        reader = arma.applicationContext().getBean(AttributeReader.class, AttributeReader.VALUE);
    }

    @Override
    public void changeValue(Object value) {
        throw new UnsupportedOperationException();
    }

}