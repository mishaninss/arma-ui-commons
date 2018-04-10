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

import com.github.mishaninss.html.interfaces.IInteractiveElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * Indicates that a field describes an element of a page object.
 * Fields, annotated with this annotation will be used during a container initialization.
 * An element controller instance will be parametrized with values, specified using this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Reader {
    /** If an element constructor requires multiple locators to be passed, use this parameter to specify a list of locators */
    String[] args() default {};

    Class<? extends Function<IInteractiveElement, String>> value();
}
