package com.github.mishaninss.arma.html.listeners;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.exceptions.SessionLostException;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.annotations.ElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.arma.utils.GenericUtils;

import java.util.concurrent.TimeUnit;

@Component
public class HighlightEventHandler implements IElementEventHandler {
    private static final String CHANGE_VALUE_MESSAGE = "Изменить значение: %s";
    private static final String READ_VALUE_MESSAGE = "Прочитать %s";
    private static final String PERFORM_ACTION_MESSAGE = "Выполнить %s";
    private static final String IS_DISPLAYED_MESSAGE = "Проверить отображение";

    @Reporter
    private IReporter reporter;

    @ElementDriver
    private IElementDriver elementDriver;

    @Override
    public void beforeEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        String actionName;
        try {
            if (elementDriver.isElementDisplayed(element, false)) {
                switch (event) {
                    case CHANGE_VALUE:
                        elementDriver.highlightElement(element);
                        Object value = ArrayUtils.isNotEmpty(args) ? args[0] : "";
                        elementDriver.addElementDebugInfo(element, String.format(CHANGE_VALUE_MESSAGE, value), "");
                        break;
                    case READ_VALUE:
                        actionName = StringUtils.isNoneBlank(comment) ? StringUtils.stripStart(comment, "read").trim() : "value";
                        elementDriver.highlightElement(element);
                        elementDriver.addElementDebugInfo(element, String.format(READ_VALUE_MESSAGE, actionName), "");
                        break;
                    case ACTION:
                        actionName = StringUtils.isNoneBlank(comment) ? StringUtils.stripStart(comment, "perform").trim() : "value";
                        elementDriver.highlightElement(element);
                        elementDriver.addElementDebugInfo(element, String.format(PERFORM_ACTION_MESSAGE, actionName), "");
                        break;
                    default:
                }
            }
        } catch (SessionLostException ex) {
            throw ex;
        } catch (Exception ex) {
            reporter.trace("Could not inject debug highlight", ex);
        }
        GenericUtils.pause(TimeUnit.MILLISECONDS, 700);
    }

    @Override
    public void afterEvent(IInteractiveElement element, ElementEvent event, String comment, Object... args) {
        try {
            switch (event) {
                case CHANGE_VALUE:
                case READ_VALUE:
                case ACTION:
                    if (elementDriver.isElementDisplayed(element, false)) {
                        elementDriver.unhighlightElement(element);
                    }
                    elementDriver.removeElementDebugInfo();
                    break;
                case IS_DISPLAYED:
                    if (args.length > 0 && args[0] instanceof Boolean && (boolean) args[0]) {
                        elementDriver.highlightElement(element);
                        elementDriver.addElementDebugInfo(element, IS_DISPLAYED_MESSAGE, "");
                        GenericUtils.pause(TimeUnit.MILLISECONDS, 700);
                        elementDriver.unhighlightElement(element);
                        elementDriver.removeElementDebugInfo();
                    }
                    break;
                default:
            }
        } catch (SessionLostException ex) {
            throw ex;
        } catch (Exception ex) {
            reporter.trace("Could not remove debug highlight", ex);
        }
    }
}
