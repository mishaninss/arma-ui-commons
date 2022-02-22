package com.github.mishaninss.arma.aspects;

import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.interfaces.IListenableElement;
import com.github.mishaninss.arma.html.listeners.ElementEvent;
import com.github.mishaninss.arma.html.listeners.FiresEvent;
import com.github.mishaninss.arma.html.listeners.IElementEventHandler;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatableWrapper;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@SuppressWarnings("unused")
@Aspect
public class InteractiveElementAspects {

  private static final Map<Signature, String> ACTION_NAMES = new Hashtable<>(); //NOSONAR

  @Autowired
  private ApplicationContext applicationContext;


  @Pointcut("call(@com.github.mishaninss.arma.html.listeners.FiresEvent * * (..))")
  public void firesEvent() {
    //NOSONAR
  }

  @Pointcut("withincode(@com.github.mishaninss.arma.html.listeners.FiresEvent * * (..))")
  public void withinCodeFiresEvent() {
    //NOSONAR
  }

  @Before("firesEvent() && !withinCodeFiresEvent()")
  public void adviceBeforeFireEvent(JoinPoint joinPoint) {
    Object target = joinPoint.getTarget();
    if (target != null) {
      if (target instanceof IListenableElement && target instanceof IInteractiveElement) {
        executeBeforeEvents((IInteractiveElement) target, joinPoint);
      } else if (target instanceof ILocatableWrapper) {
        target = ((ILocatableWrapper) target).getElement();
        if (target instanceof IListenableElement && target instanceof IInteractiveElement) {
          executeBeforeEvents((IInteractiveElement) target, joinPoint);
        }
      }
    }
  }

  private String getActionName(Signature signature, String message, Object[] args) {
    if (StringUtils.isNotBlank(message)) {
      if (args.length > 0 && message.contains("{")) {
        Map<String, String> values = IntStream.range(0, args.length).boxed()
            .collect(Collectors.<Integer, String, String>toMap(
                i -> String.valueOf(i + 1),
                i -> String.valueOf(args[i])
            ));
        message = new StringSubstitutor(values).replace(message);
      }
      return message;
    } else {
      return ACTION_NAMES.computeIfAbsent(signature, sign ->
          StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(sign.getName()), " ")
              .toLowerCase()
              .trim());
    }
  }

  private void executeBeforeEvents(IInteractiveElement element, JoinPoint joinPoint) {
    Object[] args = joinPoint.getArgs();
    FiresEvent firesEvent = ((MethodSignature) joinPoint.getSignature()).getMethod()
        .getAnnotation(FiresEvent.class);
    ElementEvent event = firesEvent.value();
    LinkedHashSet<IElementEventHandler> listeners = ((IListenableElement) element)
        .getEventListeners(event);
    if (CollectionUtils.isNotEmpty(listeners)) {
      listeners.forEach(listener -> listener.beforeEvent(element, event,
          getActionName(joinPoint.getSignature(), firesEvent.message(), joinPoint.getArgs()),
          args));
    }
  }

  @AfterReturning(value = "firesEvent() && !withinCodeFiresEvent()", returning = "ret")
  public void adviceAfterFireEvent(Object ret, JoinPoint joinPoint) {
    Object target = joinPoint.getTarget();
    if (target != null) {
      if (target instanceof IListenableElement && !((IListenableElement) target)
          .areAfterEventsSupressed() && target instanceof IInteractiveElement) {
        executeAfterEvents((IInteractiveElement) target, joinPoint, ret);
      } else if (target instanceof ILocatableWrapper) {
        target = ((ILocatableWrapper) target).getElement();
        if (target instanceof IListenableElement && !((IListenableElement) target)
            .areAfterEventsSupressed() && target instanceof IInteractiveElement) {
          executeAfterEvents((IInteractiveElement) target, joinPoint, ret);
        }
      }
    }
  }

  private void executeAfterEvents(IInteractiveElement element, JoinPoint joinPoint, Object ret) {
    var firesEvent = ((MethodSignature) joinPoint.getSignature()).getMethod()
        .getAnnotation(FiresEvent.class);
    ElementEvent event = firesEvent.value();
    LinkedList<IElementEventHandler> listeners = new LinkedList<>(
        IListenableElement.getListenersIfApplicable(element, event));
    if (CollectionUtils.isNotEmpty(listeners)) {
      Iterator<IElementEventHandler> iterator = listeners.descendingIterator();
      while (iterator.hasNext()) {
        try {
          iterator.next().afterEvent(element, event,
              getActionName(joinPoint.getSignature(), firesEvent.message(), joinPoint.getArgs()),
              ret);
        } catch (Exception ex) {

        }
      }
    }
  }

  @AfterThrowing(value = "firesEvent() && !withinCodeFiresEvent()", throwing = "e")
  public void adviceAfterThrowingFromEventFiringMethod(Exception e, JoinPoint joinPoint) {
    Object target = joinPoint.getTarget();
    IInteractiveElement element = null;
    if (target instanceof IInteractiveElement) {
      element = (IInteractiveElement) target;
    } else if (target instanceof ILocatableWrapper) {
      ILocatable locatable = ((ILocatableWrapper) target).getElement();
      if (locatable instanceof IInteractiveElement) {
        element = (IInteractiveElement) locatable;
      }
    }
    if (element != null) {
      var firesEvent = ((MethodSignature) joinPoint.getSignature()).getMethod()
          .getAnnotation(FiresEvent.class);
      ElementEvent event = firesEvent.value();
      rethrowException(element, event.getText(), e);
    }
  }

  private void rethrowException(IInteractiveElement element, String action, Exception ex) {
    throw applicationContext.getBean("exceptionBuilder", IInteractiveElementExceptionBuilder.class)
        .buildException(element, action, ex);
  }
}
