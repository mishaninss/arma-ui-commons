package com.github.mishaninss.uidriver.interfaces;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.logging.LogEntries;

import java.util.Set;

public interface IBrowserDriver {
    String QUALIFIER = "IBrowserDriver";

    void deleteAllCookies();

    void deleteCookieNamed(String cookieName);

    Set<Cookie> getAllCookies();

    void addCookie(Cookie cookie);

    Cookie getCookieNamed(String cookieName);

    Set<String> getWindowHandles();

    void switchToWindow(String windowHandle);

    void closeCurrentWindow();

    void closeWindow(String windowHandle);

    void maximizeWindow();

    LogEntries getLogEntries(String logType);

    boolean isBrowserStarted();

    void setWindowSize(int width, int height);
}
