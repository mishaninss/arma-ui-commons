package com.github.mishaninss.arma.uidriver.interfaces;

import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;

import java.time.temporal.TemporalUnit;
import java.util.function.Function;

public interface IElementQuietWaitingDriver {
    boolean exists();

    boolean exists(long timeoutInSeconds);

    boolean exists(long timeout, TemporalUnit unit);

    boolean isVisible();

    boolean isVisible(long timeoutInSeconds);

    boolean isVisible(long timeout, TemporalUnit unit);

    boolean isNotVisible();

    boolean isNotVisible(long timeoutInSeconds);

    boolean isNotVisible(long timeout, TemporalUnit unit);

    boolean isClickable();

    boolean isClickable(long timeoutInSeconds);

    boolean isClickable(long timeout, TemporalUnit unit);

    boolean attributeToBeNotEmpty(String attribute);

    boolean attributeToBeNotEmpty(String attribute, long timeoutInSeconds);

    boolean attributeToBeNotEmpty(String attribute, long timeout, TemporalUnit unit);

    boolean attributeToBe(String attribute, String value);

    boolean attributeToBe(String attribute, String value, long timeoutInSeconds);

    boolean attributeToBe(String attribute, String value, long timeout, TemporalUnit unit);

    boolean attributeContains(String attribute, String value);

    boolean attributeContains(String attribute, String value, long timeoutInSeconds);

    boolean attributeContains(String attribute, String value, long timeout, TemporalUnit unit);

    <T> boolean condition(Function<IInteractiveElement, T> condition);

    <T> boolean condition(Function<IInteractiveElement, T> condition, String message);

    <T> boolean condition(Function<IInteractiveElement, T> condition, long timeoutInSeconds);

    <T> boolean condition(Function<IInteractiveElement, T> condition, long timeoutInSeconds, String message);

    <T> boolean condition(Function<IInteractiveElement, T> condition, long timeout, TemporalUnit unit);

    <T> boolean condition(Function<IInteractiveElement, T> condition, long timeout, TemporalUnit unit, String message);

}
