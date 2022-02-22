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

package com.github.mishaninss.arma.html.interfaces;

import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayDeque;
import java.util.Deque;

public interface INamed extends ILocatable {

    static void setNameIfApplicable(Object element, String name){
        if (element instanceof INamed){
            ((INamed)element).setName(name);
        }
    }

    static String getNameIfApplicable(Object element){
        if (element instanceof INamed){
            String name = ((INamed)element).getName();
            return name == null ? "" : name;
        }
        return "";
    }

    static String getLoggableNameIfApplicable(Object element){
        if (element instanceof INamed){
            return ((INamed)element).getLoggableName();
        }
        return "";
    }

    static void setLoggableNameIfApplicable(Object element){
        setNameIfApplicable(element, getLoggableNameIfApplicable(element));
    }

    default Deque<String> getNameDeque(){
        Deque<ILocatable> cascade = getObjectDeque();
    Deque<String> path = new ArrayDeque<>();
        cascade.forEach(element ->
    {
        String name = getNameIfApplicable(element);
        if (StringUtils.isBlank(name)){
            String locator = element.getLocator();
            if (StringUtils.isNoneBlank(locator)) {
                name = "[" + element.getLocator() + "]";
            }
        }
        if (StringUtils.isNoneBlank(name)){
            path.add(name);
        }
    });
        return path;
}

    /**
     * Sets a name of this element for logging purposes
     * @param name - name of the element
     * @return this element
     */
    INamed setName(String name);

    /**
     * Returns a logging name of this element
     */
    String getName();

    default String getLoggableName(){
        String elementName = StringUtils.join(getNameDeque(), " > ").trim();
        String elementType = this.getClass().getSimpleName();
        if (!elementName.endsWith(elementType)){
            elementName += " " + elementType;
        }
        return elementName;
    }
}