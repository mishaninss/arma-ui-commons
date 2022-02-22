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

package com.github.mishaninss.arma.html.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.uidriver.Arma;

@Component
public class IFrameEventHandler implements IElementEventHandler {
    @Autowired
    private Arma arma;

    @Override
    public void beforeEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
    }

    @Override
    public void afterEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        arma.page().switchToDefaultContent();
    }
}
