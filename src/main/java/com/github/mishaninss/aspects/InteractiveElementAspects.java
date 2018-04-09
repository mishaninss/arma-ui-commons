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

package com.github.mishaninss.aspects;

import com.github.mishaninss.exceptions.InteractionException;
import com.github.mishaninss.exceptions.SessionLostException;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.interfaces.IListenableElement;
import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.html.listeners.ElementEvent;
import com.github.mishaninss.html.listeners.FiresEvent;
import com.github.mishaninss.html.listeners.IElementEventHandler;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.annotations.BrowserDriver;
import com.github.mishaninss.uidriver.annotations.PageDriver;
import com.github.mishaninss.uidriver.interfaces.IBrowserDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatableWrapper;
import com.github.mishaninss.uidriver.interfaces.IPageDriver;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("unused")
@Aspect
public class InteractiveElementAspects {
    @PageDriver
    private IPageDriver pageDriver;
    @BrowserDriver
    private IBrowserDriver browserDriver;
    @Reporter
    private IReporter reporter;
    private static final Map<Signature, String> ACTION_NAMES = new Hashtable<>(); //NOSONAR

    @Pointcut("call(@com.github.mishaninss.html.listeners.FiresEvent * * (..))")
	public void firesEvent() {
		//NOSONAR
	}

    @Pointcut("withincode(@com.github.mishaninss.html.listeners.FiresEvent * * (..))")
    public void withinCodeFiresEvent() {
        //NOSONAR
    }

    @Before("firesEvent() && !withinCodeFiresEvent()")
    public void adviceBeforeFireEvent(JoinPoint joinPoint) {
	    Object target = joinPoint.getTarget();
        if (target != null) {
            if (target instanceof IListenableElement && target instanceof IInteractiveElement) {
                executeBeforeEvents((IInteractiveElement) target, joinPoint);
            } else if (target instanceof ILocatableWrapper){
                target = ((ILocatableWrapper) target).getElement();
                if (target instanceof IListenableElement && target instanceof IInteractiveElement) {
                    executeBeforeEvents((IInteractiveElement) target, joinPoint);
                }
            }
        }
    }

    private String getActionName(Signature signature){
        return ACTION_NAMES.computeIfAbsent(signature, sign ->
                StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(sign.getName()), " ")
                    .toLowerCase()
                    .trim());
    }

    private void executeBeforeEvents(IInteractiveElement element, JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        FiresEvent firesEvent = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(FiresEvent.class);
        ElementEvent event = firesEvent.value();
        LinkedHashSet<IElementEventHandler> listeners = ((IListenableElement) element).getEventListeners(event);
        if (CollectionUtils.isNotEmpty(listeners)) {
            listeners.forEach(listener -> listener.beforeEvent(element, event, getActionName(joinPoint.getSignature()), args));
        }
    }

    @AfterReturning(value = "firesEvent() && !withinCodeFiresEvent()", returning = "ret")
    public void adviceAfterFireEvent(Object ret, JoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        if (target != null) {
            if (target instanceof IListenableElement && target instanceof IInteractiveElement) {
                executeAfterEvents((IInteractiveElement) target, joinPoint, ret);
            } else if (target instanceof ILocatableWrapper){
                target = ((ILocatableWrapper) target).getElement();
                if (target instanceof IListenableElement && target instanceof IInteractiveElement) {
                    executeAfterEvents((IInteractiveElement) target, joinPoint, ret);
                }
            }
        }
    }

    private void executeAfterEvents(IInteractiveElement element, JoinPoint joinPoint, Object ret){
        FiresEvent firesEvent = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(FiresEvent.class);
        ElementEvent event = firesEvent.value();
        LinkedList<IElementEventHandler> listeners = new LinkedList<>(IListenableElement.getListenersIfApplicable(element, event));
        if (CollectionUtils.isNotEmpty(listeners)) {
            Iterator<IElementEventHandler> iterator = listeners.descendingIterator();
            while (iterator.hasNext()) {
                iterator.next().afterEvent(element, event, getActionName(joinPoint.getSignature()), ret);
            }
        }
    }

    @AfterThrowing(value="firesEvent() && !withinCodeFiresEvent()", throwing="e")
    public void adviceAfterThrowingFromEventFiringMethod(Exception e, JoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        if (target instanceof IInteractiveElement) {
            IInteractiveElement element = (IInteractiveElement) target;
            FiresEvent firesEvent = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(FiresEvent.class);
            ElementEvent event = firesEvent.value();
            rethrowException(element, event.getText(), e);
        }
    }

    private void rethrowException(IInteractiveElement element, String action, Exception ex){
        Throwable cause = ex;
        if (cause instanceof InvocationTargetException && cause.getCause() != null){
            cause = cause.getCause();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Error in attempt to ").append(action);

        String name = "";
        if (element instanceof INamed){
            name = ((INamed)element).getName();
        }

        if (!StringUtils.isBlank(name)){
            sb.append(" [").append(name).append("]");
        } else {
            sb.append(" [").append(element.getLocator()).append("]");
        }

        sb.append("\nLocator: ").append(element.getLocatorsPath());

        if (cause instanceof SessionLostException){
            throw new SessionLostException(sb.toString(), cause);
       } else {
        	if (browserDriver.isBrowserStarted()) {
				reporter.attachScreenshot(pageDriver.takeScreenshot());
				sb.append("\nURL: ").append(pageDriver.getCurrentUrl());
				sb.append("\nPage title: ").append(pageDriver.getPageTitle());
			}

            throw new InteractionException(sb.toString(), cause);
        }
    }
}
