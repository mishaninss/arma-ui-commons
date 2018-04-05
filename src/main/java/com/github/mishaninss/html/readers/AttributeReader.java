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

package com.github.mishaninss.html.readers;

import com.github.mishaninss.uidriver.annotations.ElementDriver;
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Sergey_Mishanin on 3/30/17.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AttributeReader implements AbstractReader {
    private String defaultAttribute;

    public AttributeReader(){}

    public AttributeReader(String defaultAttribute){
        this.defaultAttribute = defaultAttribute;
    }

    @ElementDriver
    private IElementDriver elementDriver;

    @Override
    public String readProperty(ILocatable element, Object... args) {
        String attribute =
            args.length > 0 && StringUtils.isNoneBlank(args[0].toString())
            ? args[0].toString()
            : defaultAttribute;
        return elementDriver.getAttributeOfElement(element, attribute);
    }

    public static final String VALUE = "value";
    public static final String SRC = "src";
    public static final String HREF = "href";
    public static final String ALT = "alt";

}
