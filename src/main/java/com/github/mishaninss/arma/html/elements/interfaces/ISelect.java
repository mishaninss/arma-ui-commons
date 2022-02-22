package com.github.mishaninss.arma.html.elements.interfaces;

import com.github.mishaninss.arma.html.listeners.ElementEvent;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.listeners.FiresEvent;

import java.util.List;

public interface ISelect extends IInteractiveElement {

    @FiresEvent(ElementEvent.CHANGE_VALUE)
    void selectByVisibleText(String value);

    @FiresEvent(ElementEvent.READ_VALUE)
    List<String> getOptions();
}
