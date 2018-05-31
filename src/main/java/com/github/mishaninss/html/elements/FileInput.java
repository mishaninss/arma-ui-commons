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
import com.github.mishaninss.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.listeners.ElementEvent;
import com.github.mishaninss.html.listeners.FiresEvent;
import com.github.mishaninss.html.readers.AttributeReader;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.io.File;

@Element
@Primary
public class FileInput extends ArmaElement implements IEditable, IReadable{

    public FileInput(){}

    public FileInput(String locator) {
        super(locator);
    }

    public FileInput(String locator, IInteractiveContainer context) {
        super(locator, context);
    }

    public FileInput(IInteractiveElement element){
        super(element);
    }

    @Override
    @PostConstruct
    protected void init(){
        reader = arma.applicationContext().getBean(AttributeReader.class, AttributeReader.VALUE);
    }

    @Override
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public void changeValue(Object value) {
        if (value instanceof File){
            changeValue((File) value);
        } else {
            changeValue(value.toString());
        }
    }

    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public void changeValue(String pathToFile) {
        arma.element().sendKeysToElement(this, pathToFile);
    }

    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public void changeValue(File file) {
        changeValue(file.getAbsolutePath());
    }

}
