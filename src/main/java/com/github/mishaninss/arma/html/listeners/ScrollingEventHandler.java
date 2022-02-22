package com.github.mishaninss.arma.html.listeners;

import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.exceptions.SessionLostException;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.annotations.ElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementDriver;

@Component
public class ScrollingEventHandler implements IElementEventHandler {
    private static final String COULD_NOT_SCROLL_TO_ELEMENT = "Не удалось проскроллить на элемент";

    @ElementDriver
    private IElementDriver elementDriver;
    @Reporter
    private IReporter reporter;

    @Override
    public void beforeEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        if (event != ElementEvent.IS_DISPLAYED) {
            try {
                elementDriver.scrollToElement(element);
            } catch (SessionLostException ex) {
                throw ex;
            } catch (Exception ex) {
                reporter.warn(COULD_NOT_SCROLL_TO_ELEMENT, ex);
            }
        }
    }

    @Override
    public void afterEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        if (event == ElementEvent.IS_DISPLAYED && args.length > 0 && args[0] instanceof Boolean && (boolean) args[0]) {
            try {
                elementDriver.scrollToElement(element);
            } catch (SessionLostException ex) {
                throw ex;
            } catch (Exception ex) {
                reporter.warn(COULD_NOT_SCROLL_TO_ELEMENT, ex);
            }
        }
    }
}
