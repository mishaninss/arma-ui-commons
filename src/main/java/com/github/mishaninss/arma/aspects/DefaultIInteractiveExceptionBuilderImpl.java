package com.github.mishaninss.arma.aspects;

import com.github.mishaninss.arma.data.UiCommonsProperties;
import com.github.mishaninss.arma.exceptions.InteractionException;
import com.github.mishaninss.arma.exceptions.SessionLostException;
import com.github.mishaninss.arma.uidriver.annotations.BrowserDriver;
import com.github.mishaninss.arma.uidriver.annotations.PageDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IBrowserDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IPageDriver;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.interfaces.INamed;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

@Component
public class DefaultIInteractiveExceptionBuilderImpl implements IInteractiveElementExceptionBuilder {

    @BrowserDriver
    protected IBrowserDriver browserDriver;
    @Reporter
    protected IReporter reporter;
    @Autowired
    protected UiCommonsProperties properties;
    @PageDriver
    protected IPageDriver pageDriver;

    @Override
    public RuntimeException buildException(IInteractiveElement element, String action, Exception ex) {
        Throwable cause = ex;
        if (cause instanceof InvocationTargetException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Ошибка при попытке ").append(action);

        String name = "";
        if (element instanceof INamed) {
            name = ((INamed) element).getName();
        }

        if (!StringUtils.isBlank(name)) {
            sb.append(" [").append(name).append("]");
        } else {
            sb.append(" [").append(element.getLocator()).append("]");
        }

        sb.append("\nЛокатор: ").append(element.getLocatorsPath());

        if (cause instanceof SessionLostException) {
            return clearStacktrace(new SessionLostException(sb.toString(), cause));
        } else {
            if (browserDriver.isBrowserStarted()) {
                reporter.attachScreenshot(pageDriver.takeScreenshot());
                if (properties.driver().areConsoleLogsEnabled()) {
                    reporter.attachText(StringUtils.join(browserDriver.getLogEntries("browser"), "\n"), "Browser logs");
                }
                sb.append("\nURL: ").append(pageDriver.getCurrentUrl());
                sb.append("\nЗаголовок страницы: ").append(pageDriver.getPageTitle());
            }

            return clearStacktrace(new InteractionException(sb.toString(), cause));
        }
    }

    @SuppressWarnings("ThrowableNotThrown")
    protected  <T extends Throwable> T clearStacktrace(T ex) {
        String[] stacktraceWhitelist = properties.framework().stackTraceWhiteList;
        if (ArrayUtils.isNotEmpty(stacktraceWhitelist)) {
            StackTraceElement[] trace = ex.getStackTrace();
            List<StackTraceElement> clearTrace = new LinkedList<>();
            for (StackTraceElement element : trace) {
                String className = element.getClassName();
                if ((StringUtils.startsWith(className, "com.github.mishaninss.arma") || StringUtils.startsWithAny(className, stacktraceWhitelist))
                        && !StringUtils.startsWith(className, "com.github.mishaninss.arma.aspects")
                        && element.getLineNumber() > 1) {
                    clearTrace.add(element);
                }
            }
            ex.setStackTrace(clearTrace.toArray(new StackTraceElement[0]));
            Throwable cause = ex.getCause();
            if (cause != null) {
                clearStacktrace(cause);
            }
        }
        return ex;
    }
}
