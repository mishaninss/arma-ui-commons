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

import com.github.mishaninss.data.UiCommonsProperties;
import com.github.mishaninss.html.composites.IndexedElementBuilder;
import com.github.mishaninss.html.containers.ArmaContainer;
import com.github.mishaninss.html.elements.ElementBuilder;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.annotations.*;
import com.github.mishaninss.uidriver.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class Arma {
    private static final ThreadLocal<Arma> INSTANCES = new ThreadLocal<>();
    private static AnnotationConfigApplicationContext staticContext;
    private static ContextBuilder contextBuilder;

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
    @AlertHandler
    private IAlertHandler alertHandler;
    @Autowired
    private ElementBuilder elementBuilder;
    @Autowired
    private IndexedElementBuilder indexedElementBuilder;
    @Autowired
    private UiCommonsProperties properties;

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
        Arma arma = INSTANCES.get();
        if (arma == null){
            return up(null);
        } else {
            return arma;
        }

    }

    public static Arma get(String browserName){
        Arma arma = INSTANCES.get();
        if (arma == null){
            return up(browserName);
        } else {
            return arma;
        }
    }

    public static void kill(){
        Arma arma = INSTANCES.get();
        if (arma != null) {
            arma.close();
        }
    }

    public void close(){
        ((ConfigurableApplicationContext)applicationContext).close();
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

    public <T> T page(Class<T> pageClass){
        return applicationContext.getBean(pageClass);
    }

    public <T extends ArmaContainer> T open(Class<T> pageClass){
        T page = applicationContext.getBean(pageClass);
        page.goToUrl();
        return page;
    }

    public <T extends ArmaContainer> T open(String url, Class<T> pageClass){
        pageDriver.goToUrl(url);
        return applicationContext.getBean(pageClass);
    }

    public IBrowserDriver browser(){
        return browserDriver;
    }

    public IAlertHandler alert(){
        return alertHandler;
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

    public ElementBuilder by(){
        return applicationContext.getBean(ElementBuilder.class);
    }

    public ElementBuilder by(boolean withListeners){
        return by().withListeners(withListeners);
    }

    public IndexedElementBuilder bys(){
        return applicationContext.getBean(IndexedElementBuilder.class);
    }

    public IndexedElementBuilder bys(boolean withListeners){
        return bys().withListeners(withListeners);
    }

    public UiCommonsProperties config(){
        return properties;
    }

    public static Arma chrome(){
        return get("chrome");
    }

    public static ContextBuilder using(){
        if (contextBuilder == null){
            contextBuilder = new ContextBuilder();
        }
        return contextBuilder;
    }

    private static Arma up(String browserName){
        if (staticContext == null){
            using().profiles(browserName).build();
        } else {
            staticContext.getEnvironment().addActiveProfile(browserName);
        }
        staticContext.refresh();
        return staticContext.getBean(Arma.class);
    }

    static void setApplicationContext(AnnotationConfigApplicationContext context){
        staticContext = context;
    }
}