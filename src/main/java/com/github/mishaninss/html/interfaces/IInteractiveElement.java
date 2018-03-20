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

package com.github.mishaninss.html.interfaces;

import com.github.mishaninss.html.actions.AbstractAction;
import com.github.mishaninss.html.listeners.ElementEvent;
import com.github.mishaninss.html.listeners.FiresEvent;
import com.github.mishaninss.html.readers.AbstractReader;
import com.github.mishaninss.uidriver.Arma;
import com.github.mishaninss.uidriver.interfaces.ILocatable;

/**
 * Provides common interface for an abstract UI element
 * @author Sergey Mishanin
 */
public interface IInteractiveElement extends ILocatable {
    
    /**
     * Performs actions to change value in this element.
     * @param value - the object representation of a value
     */
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    void changeValue(final Object value);
    
    /**
     * Performs actions to read value from this element.
     * @return current value of this element 
     */
    @FiresEvent(ElementEvent.READ_VALUE)
    String readValue();

    default String readValue(AbstractReader reader, Object... args){
        return reader.readProperty(this, args);
    }

    /**
     * Performs default action on this element (e.g. click)
     */
    @FiresEvent(ElementEvent.ACTION)
    void performAction();

    @FiresEvent(ElementEvent.ACTION)
    default void performAction(AbstractAction action){
        action.dispatchAction(this);
    }

    @FiresEvent(ElementEvent.ACTION)
    default void performAction(AbstractAction action, Object... args){
        action.dispatchAction(this, args);
    }
    
    /**
     * Performs check if this element is displayed on a screen
     * @return true if element is displayed; false otherwise
     */
    @FiresEvent(ElementEvent.IS_DISPLAYED)
    boolean isDisplayed();

    @FiresEvent(ElementEvent.IS_DISPLAYED)
    boolean isDisplayed(boolean shouldWait);
    
    /**
     * Performs check if this element is ready for interaction
     * @return true if element is enabled; false otherwise
     */
    boolean isEnabled();

    /**
     * Returns true if this element is marked as optional and may not be presented on the screen;
     * false otherwise
     */
    boolean isOptional();

    /**
     * Sets a mark if this element is optional and may not be presented on the screen
     * @param dynamic - true, if this is an optional element; false otherwise
     */
    void setOptional(boolean dynamic);

    /**
     * Returns controller of the container, that expected to be displayed after performing an action on this element
     */
    IInteractiveContainer nextPage();

    /**
     * Sets controller of the container, that expected to be displayed after performing an action on this element
     * @param nextPage - Class of the next page controller
     */
    void setNextPage(Class<? extends IInteractiveContainer> nextPage);

    /**
     * Sets controller of the container, that expected to be displayed after performing an action on this element
     * @param nextPage - controller of the next page
     */
    void setNextPage(IInteractiveContainer nextPage);

    default String getAttribute(String attributeName){
        return Arma.get().element().getAttributeOfElement(this, attributeName);
    }

}