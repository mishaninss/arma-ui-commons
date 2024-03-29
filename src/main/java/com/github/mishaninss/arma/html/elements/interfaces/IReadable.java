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

package com.github.mishaninss.arma.html.elements.interfaces;

import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;

/**
 * Supposed to be applied to a class of element controller to mark that value of an element of this type can be rad
 */
public interface IReadable {
    static boolean isReadable(IInteractiveElement element){
        return element instanceof IReadable;
    }
}
