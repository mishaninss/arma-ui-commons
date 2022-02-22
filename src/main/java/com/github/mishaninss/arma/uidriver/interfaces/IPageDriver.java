package com.github.mishaninss.arma.uidriver.interfaces;

import com.github.mishaninss.arma.uidriver.Arma;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Provides methods to interact with a page in browser.
 *
 * @author Sergey Mishanin
 */
public interface IPageDriver {
    String QUALIFIER = "IPageDriver";

    void setPostPageOpenMethod(BiConsumer<String, Arma> postPageOpenMethod);

    boolean isInFrame();

    IPageDriver goToUrl(String url);

    IPageDriver refreshPage();

    IPageDriver navigateBack();

    Object executeJS(String javaScript);

    Object executeJS(String javaScript, Map<String, Object> params);

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