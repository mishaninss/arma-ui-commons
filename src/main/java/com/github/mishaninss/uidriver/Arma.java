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

package com.github.mishaninss.uidriver;

import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.annotations.*;
import com.github.mishaninss.uidriver.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class Arma {
    private static final ThreadLocal<Arma> INSTANCES = new ThreadLocal<>();

    @Autowired
    private ApplicationContext applicationContext;
    @ElementDriver
    private IElementDriver elementDriver;
    @ElementsDriver
    private IElementsDriver elementsDriver;
    @SelectElementDriver
    private ISelectElementDriver selectElementDriver;
    @PageDriver
    private IPageDriver pageDriver;
    @WaitingDriver
    private IWaitingDriver waitingDriver;
    @BrowserDriver
    private IBrowserDriver browserDriver;
    @Reporter
    private IReporter reporter;

    private Arma(){}

    @PostConstruct
    private void init(){
        INSTANCES.set(this);
    }

    @PreDestroy
    private void destroy(){
        INSTANCES.remove();
    }

    public static Arma get(){
        return INSTANCES.get();
    }

    public IElementDriver element(){
        return elementDriver;
    }
    
    public ISelectElementDriver selectElement(){
        return selectElementDriver;
    }
    
    public IElementsDriver elements(){
        return elementsDriver;
    }
    
    public IPageDriver page(){
        return pageDriver;
    }

    public IBrowserDriver browser(){
        return browserDriver;
    }

    public IWaitingDriver waiting(){
        return waitingDriver;
    }

    public IElementWaitingDriver waiting(ILocatable element){
        return applicationContext.getBean(IElementWaitingDriver.class, element);
    }

    public IActionsChain actionsChain(){
        return applicationContext.getBean(IActionsChain.class);
    }

    public IElementActionsChain actionsChain(ILocatable element){
        return applicationContext.getBean(IElementActionsChain.class, element);
    }

    public IThisElementDriver element(ILocatable element){
        return applicationContext.getBean(IThisElementDriver.class, element);
    }

    public IReporter reporter(){
        return reporter;
    }
}