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

package com.github.mishaninss.uidriver.interfaces;

import com.github.mishaninss.html.listeners.ElementEvent;
import com.github.mishaninss.html.listeners.FiresEvent;

public interface IElementReadActionDriver extends ILocatableWrapper{

    @FiresEvent(ElementEvent.READ_VALUE)
    boolean isSelected();

    @FiresEvent(value = ElementEvent.READ_VALUE, message = "attribute [${1}]")
    String attribute(String attribute);

    @FiresEvent(ElementEvent.READ_VALUE)
    String text();

    @FiresEvent(ElementEvent.READ_VALUE)
    String fullText();

    @FiresEvent(ElementEvent.READ_VALUE)
    String tagName();
}
