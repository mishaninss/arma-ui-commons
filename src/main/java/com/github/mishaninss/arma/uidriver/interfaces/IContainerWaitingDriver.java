package com.github.mishaninss.arma.uidriver.interfaces;

import com.github.mishaninss.arma.html.interfaces.IElementsContainer;

import java.time.temporal.TemporalUnit;
import java.util.function.Function;

public interface IContainerWaitingDriver {
    IContainerQuietWaitingDriver quietly();

    void isVisible();

    void isVisible(long timeoutInSeconds);

    void isVisible(long timeout, TemporalUnit unit);

    void isNotVisible();

    void isNotVisible(long timeoutInSeconds);

    void isNotVisible(long timeout, TemporalUnit unit);

    void allElementsAreVisible();

    void allElementsAreVisible(long timeoutInSeconds);

    void allElementsAreVisible(long timeout, TemporalUnit unit);

    void allElementsAreClickable();

    void allElementsAreClickable(long timeoutInSeconds);

    void allElementsAreClickable(long timeout, TemporalUnit unit);

    <T> T condition(Function<IElementsContainer, T> condition);

    <T> T condition(Function<IElementsContainer, T> condition, String message);

    <T> T condition(Function<IElementsContainer, T> condition, long timeoutInSeconds, String message);

    <T> T condition(Function<IElementsContainer, T> condition, long timeoutInSeconds);

    <T> T condition(Function<IElementsContainer, T> condition, long timeout, TemporalUnit unit);

    <T> T condition(Function<IElementsContainer, T> condition, long timeout, TemporalUnit unit, String message);
}
