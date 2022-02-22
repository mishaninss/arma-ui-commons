package com.github.mishaninss.arma.aspects;

import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;

public interface IInteractiveElementExceptionBuilder {
    RuntimeException buildException(IInteractiveElement element, String action, Exception ex);
}