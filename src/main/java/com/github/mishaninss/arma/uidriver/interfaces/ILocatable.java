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

package com.github.mishaninss.arma.uidriver.interfaces;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Provides common interface for an abstract UI element
 * @author Sergey Mishanin
  */
public interface ILocatable{

    default Deque<String> getLocatorDeque(){
        Deque<ILocatable> cascade = getObjectDeque();
        Deque<String> path = new ArrayDeque<>();
        cascade.forEach(element ->
        {
            String locator = element.getLocator();
            if (StringUtils.isNoneBlank(locator)){
                path.push(locator);
            }
        });
        return path;
    }

    default String getLocatorsPath(){
        Deque<String> path = getLocatorDeque();
        StringBuilder sb = new StringBuilder();
        if (!path.isEmpty()) {
            Iterator<String> iterator = path.descendingIterator();
            sb.append(iterator.next());
            while (iterator.hasNext()) {
                sb.append(" -> ").append(iterator.next());
            }
        }
        return sb.toString();
    }

    default Deque<ILocatable> getObjectDeque(){
        Deque<ILocatable> path = new ArrayDeque<>();
        path.push(this);
        ILocatable context = this.getContext();
        while (context != null){
            path.push(context);
            context = context.getContext();
        }
        return path;
    }

    default Deque<ILocatable> getRealLocatableObjectDeque(){
        Deque<ILocatable> path = new ArrayDeque<>();
        path.push(this);
        if (useContextLookup()) {
            ILocatable context = this.getContext();
            while (context != null) {
                if (StringUtils.isNoneBlank(context.getLocator())) {
                    path.push(context);
                }
                if (context.useContextLookup()) {
                    context = context.getContext();
                } else {
                    break;
                }

            }
        }
        return path;
    }
    
    /**
     * Returns locator of this element
     */
    String getLocator();
    
    /**
     * Sets locator of this element
     */
    void setLocator(String locator);
    
    /**
     * Returns a link to the object of the ElementSet type, that contains this element
     */
    ILocatable getContext();

    /**
     * Specifies an object of the ElementSet type, that contains this element
     * @param context - the object, that contains this element
     * @return this element
     */
    void setContext(ILocatable context);

    /**
     * Sets a mark if context lookup should be used for locating of this element
     * @param contextLookup - true to use context lookup; false if given locator is final
     * @return this element
     */
    void setContextLookup(boolean contextLookup);

    /**
     * Indicates if context lookup should be used for locating of this element
     * @return true to use context lookup; false if given locator is final
     */
    boolean useContextLookup();

}