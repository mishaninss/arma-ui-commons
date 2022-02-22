package com.github.mishaninss.arma.uidriver.interfaces;

import com.github.mishaninss.arma.html.listeners.ElementEvent;
import com.github.mishaninss.arma.html.listeners.FiresEvent;

public interface IElementReadActionDriver extends ILocatableWrapper {

    @FiresEvent(ElementEvent.READ_VALUE)
    boolean isSelected();

    @FiresEvent(value = ElementEvent.READ_VALUE, message = "attribute [${1}]")
    String attribute(String attribute);

    @FiresEvent(ElementEvent.READ_VALUE)
    String text();

    @FiresEvent(ElementEvent.READ_VALUE)
    String fullText();

    @FiresEvent(ElementEvent.READ_VALUE)
    String tagName();

    @FiresEvent(value = ElementEvent.READ_VALUE, message = "css value [${1}]")
    String cssValue(String property);
}
