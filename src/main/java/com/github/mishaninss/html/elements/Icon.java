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
import com.github.mishaninss.html.elements.interfaces.IReadable;
import com.github.mishaninss.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.listeners.ElementEvent;
import com.github.mishaninss.html.listeners.FiresEvent;
import com.github.mishaninss.html.readers.AttributeReader;

import javax.annotation.PostConstruct;

@Element
public class Icon extends ArmaElement implements IReadable {

    public Icon(){
        super();
    }

    public Icon(String locator) {
        super(locator);
    }

    public Icon(String locator, IInteractiveContainer context) {
        super(locator, context);
    }

    public Icon(IInteractiveElement element){
        super(element);
    }

    @Override
    @PostConstruct
    protected void init(){
        reader = applicationContext.getBean(AttributeReader.class, AttributeReader.ALT);
    }

    @FiresEvent(ElementEvent.READ_VALUE)
    public String getSrc(){
        return read().attribute("src");
    }

    @FiresEvent(ElementEvent.READ_VALUE)
    public String getHref() {return read().attribute("href");}

}
