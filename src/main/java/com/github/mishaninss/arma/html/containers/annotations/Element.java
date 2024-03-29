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

package com.github.mishaninss.arma.html.containers.annotations;

import com.github.mishaninss.arma.html.containers.ArmaContainer;
import com.github.mishaninss.arma.html.elements.NoopElement;
import com.github.mishaninss.arma.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.readers.NoopReader;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Indicates that a field describes an element of a page object.
 * Fields, annotated with this annotation will be used during a container initialization.
 * An element controller instance will be parametrized with values, specified using this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Repeatable(ContextualElement.class)
public @interface Element {
    String PROFILE_PROPERTY = "profile";

    String prop() default PROFILE_PROPERTY;

    String val() default "";

    /**
     * Alias for locator()
     */
    String value() default "";

    /**
     * Specifies a locator of an element
     */
    String locator() default "";

    /**
     * If an element constructor requires multiple locators to be passed, use this parameter to specify a list of locators
     */
    String[] locators() default {};

    /**
     * Specifies internal ID of an element
     */
    String id() default "";

    /**
     * Specifies a human readable name of an element for logging purposes
     */
    String name() default "";

    /**
     * Specifies XPath locator of an element
     */
    String byXpath() default "";

    /**
     * Specifies CSS locator of an element
     */
    String byCss() default "";

    /**
     * Specifies Name locator of an element
     */
    String byName() default "";

    /**
     * Specifies Id locator of an element
     */
    String byId() default "";

    /**
     * Specifies className locator of an element
     */
    String byClass() default "";

    /**
     * Specifies tagName locator of an element
     */
    String byTag() default "";

    /**
     * Specifies linkText locator of an element
     */
    String byLink() default "";

    /**
     * Specifies partialLinkText locator of an element
     */
    String byPatrialLink() default "";

    String byText() default "";

    String byArg() default "";

    /**
     * Specifies if this element is optional
     */
    boolean optional() default false;

    boolean contextLookup() default true;

    Class<? extends IInteractiveContainer> nextPage() default ArmaContainer.class;

    Class<? extends IInteractiveElement> type() default NoopElement.class;

    Reader reader() default @Reader(NoopReader.class);

    Class<? extends ILocatable> context() default NoopElement.class;
}
