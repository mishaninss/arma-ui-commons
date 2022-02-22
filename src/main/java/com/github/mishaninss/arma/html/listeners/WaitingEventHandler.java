package com.github.mishaninss.arma.html.listeners;

import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.exceptions.SessionLostException;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver;

@Component
public class WaitingEventHandler implements IElementEventHandler {
    @WaitingDriver
    private IWaitingDriver waitingDriver;
    @Reporter
    private IReporter reporter;

    @Override
    public void beforeEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        try {
            if (event != ElementEvent.IS_DISPLAYED) {
                waitingDriver.waitForPageUpdate();
            }
        } catch (SessionLostException ex) {
            throw ex;
        } catch (Exception ex) {
            reporter.trace("Exception during before event waiting", ex);
        }
    }

    @Override
    public void afterEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        try {
            if (event != ElementEvent.IS_DISPLAYED) {
                waitingDriver.waitForPageUpdate();
            }
        } catch (SessionLostException ex) {
            throw ex;
        } catch (Exception ex) {
            reporter.trace("Exception during after event waiting", ex);
        }
    }
}
