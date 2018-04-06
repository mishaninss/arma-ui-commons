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

import com.github.mishaninss.uidriver.LocatorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ElementBuilder {
    @Autowired
    private ApplicationContext applicationContext;
    private static final String BEAN_NAME = "armaElement";

    public ArmaElement xpath(String xpath){
        return (ArmaElement) applicationContext.getBean(BEAN_NAME, LocatorType.buildXpath(xpath));
    }

    public ArmaElement css(String css){
        return (ArmaElement) applicationContext.getBean(BEAN_NAME, LocatorType.buildCss(css));
    }

    public ArmaElement id(String id){
        return (ArmaElement) applicationContext.getBean(BEAN_NAME, LocatorType.buildId(id));
    }

    public ArmaElement name(String name){
        return (ArmaElement) applicationContext.getBean(BEAN_NAME, LocatorType.buildName(name));
    }

    public ArmaElement link(String linkText){
        return (ArmaElement) applicationContext.getBean(BEAN_NAME, LocatorType.buildLink(linkText));
    }

    public ArmaElement partialLink(String partialLinkText){
        return (ArmaElement) applicationContext.getBean(BEAN_NAME, LocatorType.buildPartialLink(partialLinkText));
    }

    public ArmaElement tag(String tag){
        return (ArmaElement) applicationContext.getBean(BEAN_NAME, LocatorType.buildTag(tag));
    }

    public ArmaElement className(String className){
        return (ArmaElement) applicationContext.getBean(BEAN_NAME, LocatorType.buildClass(className));
    }
}
