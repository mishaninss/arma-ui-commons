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

    //  XPATH
    public ArmaElement xpath(String xpath) {
        return xpath(xpath, ArmaElement.class);
    }

    public ArmaElement xpath(String xpath, Object... args) {
        return xpath(String.format(xpath, args));
    }

    public <T extends IInteractiveElement> T xpath(Class<T> elementType, String xpath, Object... args) {
        return xpath(String.format(xpath, args), elementType);
    }

    public <T extends IInteractiveElement> T xpath(String xpath, Class<T> elementType) {
        return buildElement(LocatorType.buildXpath(xpath), elementType);
    }

    //  CSS
    public ArmaElement css(String css, Object... args) {
        return css(String.format(css, args));
    }

    public ArmaElement css(String css) {
        return css(css, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T css(Class<T> elementType, String css, Object... args) {
        return css(String.format(css, args), elementType);
    }

    public <T extends IInteractiveElement> T css(String css, Class<T> elementType) {
        return buildElement(LocatorType.buildCss(css), elementType);
    }

    //    ID
    public ArmaElement id(String id, Object... args) {
        return id(String.format(id, args));
    }

    public ArmaElement id(String id) {
        return id(id, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T id(Class<T> elementType, String id, Object... args) {
        return id(String.format(id, args), elementType);
    }

    public <T extends IInteractiveElement> T id(String id, Class<T> elementType) {
        return buildElement(LocatorType.buildId(id), elementType);
    }

    //    NAME
    public ArmaElement name(String name, Object... args) {
        return name(String.format(name, args));
    }

    public ArmaElement name(String name) {
        return name(name, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T name(Class<T> elementType, String name, Object... args) {
        return name(String.format(name, args), elementType);
    }

    public <T extends IInteractiveElement> T name(String name, Class<T> elementType) {
        return buildElement(LocatorType.buildName(name), elementType);
    }

    //    LINK
    public ArmaElement link(String linkText, Object... args) {
        return link(String.format(linkText, args));
    }

    public ArmaElement link(String linkText) {
        return link(linkText, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T link(Class<T> elementType, String linkText, Object... args) {
        return link(String.format(linkText, args), elementType);
    }

    public <T extends IInteractiveElement> T link(String linkText, Class<T> elementType) {
        return buildElement(LocatorType.buildLink(linkText), elementType);
    }

    //    ARG
    public ArmaElement arg(String argLocator, Object... args) {
        return arg(String.format(argLocator, args));
    }

    public ArmaElement arg(String argLocator) {
        return arg(argLocator, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T arg(Class<T> elementType, String argLocator, Object... args) {
        return arg(String.format(argLocator, args), elementType);
    }

    public <T extends IInteractiveElement> T arg(String argLocator, Class<T> elementType) {
        return buildElement(LocatorType.buildArg(argLocator), elementType);
    }

    //    PARTIAL LINK
    public ArmaElement partialLink(String partialLinkText, Object... args) {
        return partialLink(String.format(partialLinkText, args));
    }

    public ArmaElement partialLink(String partialLinkText) {
        return partialLink(partialLinkText, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T partialLink(Class<T> elementType, String partialLinkText, Object... args) {
        return partialLink(String.format(partialLinkText, args), elementType);
    }

    public <T extends IInteractiveElement> T partialLink(String partialLinkText, Class<T> elementType) {
        return buildElement(LocatorType.buildPartialLink(partialLinkText), elementType);
    }

    //    TAG
    public ArmaElement tag(String tag, Object... args) {
        return tag(String.format(tag, args));
    }

    public ArmaElement tag(String tag) {
        return tag(tag, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T tag(String tag, Class<T> elementType) {
        return buildElement(LocatorType.buildTag(tag), elementType);
    }

    //    CLASS NAME
    public ArmaElement className(String className, Object... args) {
        return className(String.format(className, args));
    }

    public ArmaElement className(String className) {
        return className(className, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T className(Class<T> elementType, String className, Object... args) {
        return className(String.format(className, args), elementType);
    }

    public <T extends IInteractiveElement> T className(String className, Class<T> elementType) {
        return buildElement(LocatorType.buildClass(className), elementType);
    }

    //    TEXT
    public ArmaElement text(String text, Object... args) {
        return text(String.format(text, args));
    }

    public ArmaElement text(String text) {
        return text(text, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T text(Class<T> elementType, String text, Object... args) {
        return text(String.format(text, args), elementType);
    }

    public <T extends IInteractiveElement> T text(String text, Class<T> elementType) {
        return buildElement(LocatorType.buildText(text), elementType);
    }

    //    PARTIAL TEXT
    public ArmaElement partialText(String text, Object... args) {
        return partialText(String.format(text));
    }

    public ArmaElement partialText(String text) {
        return partialText(text, ArmaElement.class);
    }

    public <T extends IInteractiveElement> T partialText(Class<T> elementType, String text, Object... args) {
        return partialText(String.format(text, args), elementType);
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
