package com.github.mishaninss.arma.uidriver.interfaces;

import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;

import java.time.temporal.TemporalUnit;
import java.util.function.Function;

public interface IElementWaitingDriver {
    IElementQuietWaitingDriver quietly();

    void exists();

    void exists(long timeoutInSeconds);

    void exists(long timeout, TemporalUnit unit);

    void isVisible();

    void isVisible(long timeoutInSeconds);

    void isVisible(long timeout, TemporalUnit unit);

    void isNotVisible();

    void isNotVisible(long timeoutInSeconds);

    void isNotVisible(long timeout, TemporalUnit unit);

    void isClickable();

    void isClickable(long timeoutInSeconds);

    void isClickable(long timeout, TemporalUnit unit);

    void attributeToBeNotEmpty(String attribute);

    void attributeToBeNotEmpty(String attribute, long timeoutInSeconds);

    void attributeToBeNotEmpty(String attribute, long timeout, TemporalUnit unit);

    void attributeToBe(String attribute, String value);

    void attributeToBe(String attribute, String value, long timeoutInSeconds);

    void attributeToBe(String attribute, String value, long timeout, TemporalUnit unit);

    void attributeContains(String attribute, String value);

    void attributeContains(String attribute, String value, long timeoutInSeconds);

    void attributeContains(String attribute, String value, long timeout, TemporalUnit unit);

    void valueToBe(String value);

    void valueNotToBe(String value);

    <T> T condition(Function<IInteractiveElement, T> condition);

    <T> T condition(Function<IInteractiveElement, T> condition, String message);

    <T> T condition(Function<IInteractiveElement, T> condition, long timeoutInSeconds);

    <T> T condition(Function<IInteractiveElement, T> condition, long timeoutInSeconds, String message);

    <T> T condition(Function<IInteractiveElement, T> condition, long timeout, TemporalUnit unit);

    <T> T condition(Function<IInteractiveElement, T> condition, long timeout, TemporalUnit unit, String message);
}
