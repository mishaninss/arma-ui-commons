package com.github.mishaninss.arma.uidriver.interfaces;

import java.util.List;
import java.util.Set;

public interface IBrowserDriver {
    String QUALIFIER = "IBrowserDriver";

    void deleteAllCookies();

    void deleteCookieNamed(String cookieName);

    Set<ICookie> getAllCookies();

    void addCookie(ICookie cookie);

    ICookie getCookieNamed(String cookieName);

    Set<String> getWindowHandles();

    String getWindowHandle();

    void switchToWindow(String windowHandle);

    void switchToWindow(int windowIndex);

    void switchToLastWindow();

    void closeCurrentWindow();

    void closeWindow(String windowHandle);

    void maximizeWindow();

    List<ILogEntry> getLogEntries(String logType);

    boolean isBrowserStarted();

    void setWindowSize(int width, int height);
}
