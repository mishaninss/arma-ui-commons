package com.github.mishaninss.arma.uidriver;

import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.annotations.BrowserDriver;
import com.github.mishaninss.arma.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IBrowserDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class WindowsManager {

  @BrowserDriver
  private IBrowserDriver browserDriver;
  @WaitingDriver
  private IWaitingDriver waitingDriver;
  private final Deque<String> windowHandles = new LinkedList<>();
  @Reporter
  private IReporter reporter;

  public void clear() {
    windowHandles.clear();
  }

  public int refreshWindowHandles() {
    if (windowHandles.isEmpty()) {
      windowHandles.addAll(browserDriver.getWindowHandles());
      return windowHandles.size();
    } else {
      Set<String> currentHandles = browserDriver.getWindowHandles();
      if (currentHandles.size() > windowHandles.size()) { //New window was opened
        currentHandles.removeAll(windowHandles);
        for (String newHandle : currentHandles) {
          windowHandles.push(newHandle);
        }
        reporter.trace("Открытых окон %d. Новых %d", windowHandles.size(), currentHandles.size());
        reporter.trace("Окна: %s", windowHandles.toString());
        return currentHandles.size();
      } else if (currentHandles.size() < windowHandles.size()) { //window was closed
        List<String> diff = new ArrayList<>(windowHandles);
        diff.removeAll(currentHandles);
        for (String oldHandle : diff) {
          windowHandles.remove(oldHandle);
        }
        reporter.trace("Открытых окон %d. Закрыто %d", windowHandles.size(), diff.size());
        reporter.trace("Окна: %s", windowHandles.toString());
        return diff.size();
      }
    }
    return 0;
  }

  public String getCurrentWindowHandle() {
    return browserDriver.getWindowHandle();
  }

  public List<String> getOtherWindowHandles() {
    List<String> handles = new ArrayList<>(windowHandles);
    handles.remove(getCurrentWindowHandle());
    return handles;
  }

  public void switchToLastWindow() {
    refreshWindowHandles();
    switchToLastWindowWithoutCheck();
  }

  private void switchToLastWindowWithoutCheck() {
    reporter.trace("switch to %s", windowHandles.peek());
    browserDriver.switchToWindow(windowHandles.peek());
    waitingDriver.waitForPageUpdate();
  }

  public void ensureLastWindow() {
    if (refreshWindowHandles() != 0) {
      switchToLastWindowWithoutCheck();
    }
  }
}
