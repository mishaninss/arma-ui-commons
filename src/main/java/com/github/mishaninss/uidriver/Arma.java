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
import com.github.mishaninss.exceptions.FrameworkConfigurationException;
import com.github.mishaninss.html.composites.IndexedElementBuilder;
import com.github.mishaninss.html.containers.ArmaContainer;
import com.github.mishaninss.html.containers.ContainersFactory;
import com.github.mishaninss.html.elements.ElementBuilder;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.annotations.AlertHandler;
import com.github.mishaninss.uidriver.annotations.BrowserDriver;
import com.github.mishaninss.uidriver.annotations.ElementDriver;
import com.github.mishaninss.uidriver.annotations.ElementsDriver;
import com.github.mishaninss.uidriver.annotations.PageDriver;
import com.github.mishaninss.uidriver.annotations.SelectElementDriver;
import com.github.mishaninss.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IActionsChain;
import com.github.mishaninss.uidriver.interfaces.IAlertHandler;
import com.github.mishaninss.uidriver.interfaces.IBrowserDriver;
import com.github.mishaninss.uidriver.interfaces.IElementActionsChain;
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.uidriver.interfaces.IElementWaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IElementsDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.uidriver.interfaces.IPageDriver;
import com.github.mishaninss.uidriver.interfaces.ISelectElementDriver;
import com.github.mishaninss.uidriver.interfaces.IThisElementDriver;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
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
    @Autowired
    private Environment environment;
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
    @Autowired
    private ContainersFactory containersFactory;

    private ArmaContainer currentPage;

    private Arma() {
    }

    @PostConstruct
    private void init() {
        INSTANCES.set(this);
    }

    @PreDestroy
    private void destroy() {
        INSTANCES.remove();
    }

    public static Arma get() {
        Arma arma = INSTANCES.get();
        if (arma == null) {
            return up(null);
        } else {
            return arma;
        }

    }

    public static Arma get(String browserName) {
        Arma arma = INSTANCES.get();
        if (arma == null) {
            return up(browserName);
        } else {
            return arma;
        }
    }

    public static void kill() {
        Arma arma = INSTANCES.get();
        if (arma != null) {
            arma.close();
        }
    }

    public void close() {
        ((ConfigurableApplicationContext) applicationContext).close();
    }

    public ContainersFactory containersFactory() {
        return containersFactory;
    }

    public IElementDriver element() {
        return elementDriver;
    }

    public ISelectElementDriver selectElement() {
        return selectElementDriver;
    }

    public IElementsDriver elements() {
        return elementsDriver;
    }

    public void setCurrentPage(ArmaContainer currentPage) {
        this.currentPage = currentPage;
    }

    public void setCurrentPage(Class<? extends ArmaContainer> currentPageClass) {
        this.currentPage = page(currentPageClass);
    }

    public void setCurrentPage(String currentPageName) {
        this.currentPage = page(currentPageName);
    }

    public ArmaContainer currentPage() {
        return currentPage;
    }

    public IPageDriver page() {
        return pageDriver;
    }

    public <T extends ArmaContainer> T page(Class<T> pageClass) {
        return applicationContext.getBean(pageClass);
    }

    public ArmaContainer page(String pageName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(pageName), "Page name cannot be blank");
        String[] words = StringUtils.normalizeSpace(pageName.trim()).split(" ");
        StringBuilder sb = new StringBuilder(words[0].toLowerCase());
        for (int i = 1; i < words.length; i++) {
            sb.append(StringUtils.capitalize(words[i].toLowerCase()));
        }
        String pageClassName = sb.toString();
        try {
            return (ArmaContainer) applicationContext.getBean(pageClassName);
        } catch (Exception ex) {
            throw new FrameworkConfigurationException("There is no container with name " + pageClassName, ex);
        }
    }

    public <T extends ArmaContainer> T open(Class<T> pageClass) {
        T page = page(pageClass);
        page.goToUrl();
        return page;
    }

    public <T extends ArmaContainer> T open(String url, Class<T> pageClass) {
        pageDriver.goToUrl(url);
        return applicationContext.getBean(pageClass);
    }

    public ApplicationContext applicationContext() {
        return applicationContext;
    }

    public Environment env() {
        return environment;
    }

    public IBrowserDriver browser() {
        return browserDriver;
    }

    public IAlertHandler alert() {
        return alertHandler;
    }

    public IWaitingDriver waiting() {
        return waitingDriver;
    }

    public IElementWaitingDriver waiting(ILocatable element) {
        return applicationContext.getBean(IElementWaitingDriver.class, element);
    }

    public IActionsChain actionsChain() {
        return applicationContext.getBean(IActionsChain.class);
    }

    public IElementActionsChain actionsChain(ILocatable element) {
        return applicationContext.getBean(IElementActionsChain.class, element);
    }

    public IThisElementDriver element(ILocatable element) {
        return applicationContext.getBean(IThisElementDriver.class, element);
    }

    public IReporter reporter() {
        return reporter;
    }

    public ElementBuilder elementBy() {
        return applicationContext.getBean(ElementBuilder.class);
    }

    public ElementBuilder elementBy(boolean withListeners) {
        return elementBy().withListeners(withListeners);
    }

    public ElementBuilder elementBy(ILocatable context) {
        return elementBy().withContext(context);
    }

    public IndexedElementBuilder elementsBy() {
        return applicationContext.getBean(IndexedElementBuilder.class);
    }

    public IndexedElementBuilder elementsBy(boolean withListeners) {
        return elementsBy().withListeners(withListeners);
    }

    public IndexedElementBuilder elementsBy(ILocatable context) {
        return elementsBy().withContext(context);
    }

    public UiCommonsProperties config() {
        return properties;
    }

    public static Arma chrome() {
        return get("chrome");
    }

    public static ContextBuilder using() {
        if (contextBuilder == null) {
            contextBuilder = new ContextBuilder();
        }
        return contextBuilder;
    }

    private static Arma up(String browserName) {
        if (staticContext == null) {
            using().profiles(browserName).build();
        } else {
            staticContext.getEnvironment().addActiveProfile(browserName);
        }
        staticContext.refresh();
        return staticContext.getBean(Arma.class);
    }

    static void setApplicationContext(AnnotationConfigApplicationContext context) {
        staticContext = context;
    }
}