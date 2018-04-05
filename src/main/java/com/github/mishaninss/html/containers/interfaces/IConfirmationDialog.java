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

package com.github.mishaninss.html.containers.interfaces;

import com.github.mishaninss.html.interfaces.IInteractiveContainer;

/**
 * Controller for an abstract confirmation dialog.
 * @author Sergey Mishanin
 */
public interface IConfirmationDialog extends IInteractiveContainer {
    
    /** Contains available IDs of action elements*/
    class Buttons{
        public static final String ACCEPT = "Accept";
        public static final String DECLINE = "Cancel";

        private Buttons(){}
    }
    
    /** Performs click on the Accept button */
    default void accept(){
        performAction(Buttons.ACCEPT);
    }
    
    /** Performs click on the Cancel button */
    default void decline(){
        performAction(Buttons.DECLINE);
    }
}