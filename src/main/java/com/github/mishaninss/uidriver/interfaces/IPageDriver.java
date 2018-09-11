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

package com.github.mishaninss.uidriver.interfaces;

import com.github.mishaninss.uidriver.Arma;

import java.util.function.BiConsumer;

/**
 * Provides methods to interact with a page in browser.
 * @author Sergey Mishanin
 */
public interface IPageDriver {
	String QUALIFIER = "IPageDriver";

    void setPostPageOpenMethod(BiConsumer<String, Arma> postPageOpenMethod);

    IPageDriver goToUrl(String url);
	
	IPageDriver refreshPage();

	IPageDriver navigateBack();

	Object executeJS(String javaScript);

    Object executeAsyncJS(String javaScript);

    Object executeAsyncJS(String javaScript, Object... args);

    Object executeAsyncJS(String javaScript, ILocatable element, Object... args);

    Object executeJS(String javaScript, String locator, Object... args);

    String getCurrentUrl();

	String getPageTitle();

    String getPageSource();

    byte[] takeScreenshot();

    IPageDriver switchToFrame(String nameOrId);

    IPageDriver switchToFrame(ILocatable frameElement);

    IPageDriver switchToDefaultContent();

    boolean scrollToBottom();

    IPageDriver scrollToTop();
}