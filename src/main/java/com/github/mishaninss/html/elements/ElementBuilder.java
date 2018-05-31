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

package com.github.mishaninss.html.elements;

import com.github.mishaninss.data.UiCommonsProperties;
import com.github.mishaninss.html.containers.ContainersFactory;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.uidriver.LocatorType;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("unchecked")
public class ElementBuilder {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ContainersFactory containersFactory;
    @Autowired
    private UiCommonsProperties properties;

    private boolean withListeners;
    private ILocatable context;

    @PostConstruct
    private void init() {
        withListeners = properties.framework().areDefaultListenersEnabled;
    }

    public ElementBuilder withListeners() {
        withListeners = true;
        return this;
    }

    public ElementBuilder withListeners(boolean withListeners) {
        this.withListeners = withListeners;
        return this;
    }

    public ElementBuilder withContext(ILocatable context) {
        this.context = context;
        return this;
    }

    public ElementBuilder withoutListeners() {
        withListeners = false;
        return this;
    }

    public ElementBuilder raw() {
        return withoutListeners();
    }

    public ArmaElement xpath(String xpath) {
        return xpath(xpath, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T xpath(String xpath, Class<T> elementType) {
        return buildElement(LocatorType.buildXpath(xpath), elementType);
    }

    public ArmaElement css(String css) {
        return css(css, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T css(String css, Class<T> elementType) {
        return buildElement(LocatorType.buildCss(css), elementType);
    }

    public ArmaElement id(String id) {
        return id(id, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T id(String id, Class<T> elementType) {
        return buildElement(LocatorType.buildId(id), elementType);
    }

    public ArmaElement name(String name) {
        return name(name, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T name(String name, Class<T> elementType) {
        return buildElement(LocatorType.buildName(name), elementType);
    }

    public ArmaElement link(String linkText) {
        return link(linkText, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T link(String linkText, Class<T> elementType) {
        return buildElement(LocatorType.buildLink(linkText), elementType);
    }

    public ArmaElement arg(String argLocator) {
        return arg(argLocator, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T arg(String argLocator, Class<T> elementType) {
        return buildElement(LocatorType.buildArg(argLocator), elementType);
    }

    public ArmaElement partialLink(String partialLinkText) {
        return partialLink(partialLinkText, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T partialLink(String partialLinkText, Class<T> elementType) {
        return buildElement(LocatorType.buildPartialLink(partialLinkText), elementType);
    }

    public ArmaElement tag(String tag) {
        return tag(tag, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T tag(String tag, Class<T> elementType) {
        return buildElement(LocatorType.buildTag(tag), elementType);
    }

    public ArmaElement className(String className) {
        return className(className, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T className(String className, Class<T> elementType) {
        return buildElement(LocatorType.buildClass(className), elementType);
    }

    public ArmaElement text(String text) {
        return text(text, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T text(String text, Class<T> elementType) {
        return buildElement(LocatorType.buildText(text), elementType);
    }

    public ArmaElement partialText(String text) {
        return partialText(text, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T partialText(String text, Class<T> elementType) {
        return buildElement(LocatorType.buildPartialText(text), elementType);
    }

    public <T extends IInteractiveElement> T clone(IInteractiveElement elementToClone) {
        Preconditions.checkArgument(elementToClone != null, "element to clone cannot be null");
        return (T) applicationContext.getBean(getBeanName(elementToClone.getClass()), elementToClone);
    }

    private <T extends IInteractiveElement> T buildElement(String locator, Class<T> elementType) {
        T element = (T) applicationContext.getBean(getBeanName(elementType), locator);
        if (withListeners) {
            containersFactory.addDefaultListeners(element);
            INamed.setLoggableNameIfApplicable(element);
        }
        if (context != null) {
            element.setContext(context);
        }
        return element;
    }

    private static String getBeanName(Class<?> elementType) {
        return StringUtils.uncapitalize(elementType.getSimpleName());
    }
}
