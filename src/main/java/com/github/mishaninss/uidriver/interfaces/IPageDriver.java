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

import org.openqa.selenium.Cookie;
import org.openqa.selenium.logging.LogEntries;

import java.util.Set;

/**
 * 
 * @author Sergey Mishanin
 *
 */
public interface IPageDriver 
{
    IPageDriver goToUrl(String url);

	boolean isAlertDisplayed();

	boolean isAlertDisplayed(boolean waitForAlert);
	
	IPageDriver acceptAlert();
	
	IPageDriver dismissAlert();
	
	String getAlertMessage();
	
	IPageDriver refreshPage();

	IPageDriver navigateBack();

	Object executeJS(String javaScript);
	
	Object executeJS(String javaScript, String locator);
	
	String getCurrentUrl();

	String getPageTitle();

    String getPageSource();

    byte[] takeScreenshot();

	void deleteAllCookies();

    void deleteCookieNamed(String cookieName);

    Set<Cookie> getAllCookies();

    void addCookie(Cookie cookie);

    Cookie getCookieNamed(String cookieName);

	Set<String> getWindowHandles();

    void switchToWindow(String windowHandle);

    void switchToFrame(String nameOrId);

    void switchToFrame(ILocatable frameElement);

    void closeCurrentWindow();

    void closeWindow(String windowHandle);

    void switchToDefaultContent();

    void maximizeWindow();

    boolean scrollToBottom();

    void scrollToTop();

    LogEntries getLogEntries(String logType);

    boolean isBrowserStarted();
}