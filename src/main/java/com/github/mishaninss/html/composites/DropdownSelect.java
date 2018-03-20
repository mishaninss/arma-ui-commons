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

package com.github.mishaninss.html.composites;

import com.github.mishaninss.html.elements.ArmaElement;
import com.github.mishaninss.html.elements.interfaces.IEditable;
import com.github.mishaninss.html.elements.interfaces.IReadable;
import com.github.mishaninss.html.containers.annotations.Element;

@Element
public class DropdownSelect extends CompositeElement implements IReadable, IEditable {

    @Element(locator = ".//div[contains(@class,'select__value')]")
    public ArmaElement selectedValueElement;

    @Element(locator = ".//ul/li[@data-value='%s']")
    public TemplatedElement<ArmaElement> optionElement;

    @Element(locator = ".//ul/li[.='%s']")
    public TemplatedElement<ArmaElement> optionElementText;

    public DropdownSelect(String locator){
        super(locator);
    }

    @Override
    public void changeValue(Object value) {
        selectedValueElement.perform().jsClick();
        optionElement.resolveTemplate(value.toString()).perform().jsClick();
    }

    @Override
    public String readValue() {
        return selectedValueElement.read().text();
    }

    @Override
    public void performAction() {
        selectedValueElement.perform().click();
    }

}
