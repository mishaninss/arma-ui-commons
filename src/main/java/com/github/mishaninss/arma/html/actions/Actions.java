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

package com.github.mishaninss.arma.html.actions;

import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Sergey_Mishanin
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class Actions {
    @Autowired
    private ApplicationContext applicationContext;
    private IInteractiveElement element;

    public Actions(IInteractiveElement element){
        this.element = element;
    }

    public Actions click(){
        element.performAction(applicationContext.getBean(ClickAction.class));
        return this;
    }

    public Actions simpleClick(){
        element.performAction(applicationContext.getBean(SimpleClickAction.class));
        return this;
    }

    public Actions clickAndSwitchToWindow(int windowIndex){
        element.performAction(applicationContext.getBean(ClickAndSwitchToWindowAction.class), windowIndex);
        return this;
    }

    public Actions clickWithDelay(){
        element.performAction(applicationContext.getBean(ClickWithDelayAction.class));
        return this;
    }

    public Actions clickAndHold(){
        element.performAction(applicationContext.getBean(ClickAndHoldAction.class));
        return this;
    }

    public Actions jsClick(){
        element.performAction(applicationContext.getBean(JsClickAction.class));
        return this;
    }

    public Actions contextClick(){
        element.performAction(applicationContext.getBean(ContextClickAction.class));
        return this;
    }

    public Actions hover(){
        element.performAction(applicationContext.getBean(HoverAction.class));
        return this;
    }

}
