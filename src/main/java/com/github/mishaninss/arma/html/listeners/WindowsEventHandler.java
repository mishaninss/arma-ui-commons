package com.github.mishaninss.arma.html.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.uidriver.WindowsManager;

import java.util.*;

@Component
public class WindowsEventHandler implements IElementEventHandler {

    @Autowired
    private WindowsManager windowsManager;

    @Override
    public void beforeEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        windowsManager.ensureLastWindow();
    }

    @Override
    public void afterEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        windowsManager.ensureLastWindow();
    }
}
