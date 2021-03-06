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

package com.github.mishaninss.html.containers.annotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Component
@Autowired
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public @interface Container {
    String prop() default Element.PROFILE_PROPERTY;
    String val() default "";
    String locator() default "";
    String locators() default "";
    String name() default "";
    String value() default "";
    /** Specifies XPath locator of an element */
    String byXpath() default "";

    /** Specifies CSS locator of an element */
    String byCss() default "";

    /** Specifies Name locator of an element */
    String byName() default "";

    /** Specifies Id locator of an element */
    String byId() default "";

    /** Specifies className locator of an element */
    String byClass() default "";

    /** Specifies tagName locator of an element */
    String byTag() default "";

    /** Specifies linkText locator of an element */
    String byLink() default "";

    /** Specifies partialLinkText locator of an element */
    String byPatrialLink() default "";

    String byArg() default "";
}
