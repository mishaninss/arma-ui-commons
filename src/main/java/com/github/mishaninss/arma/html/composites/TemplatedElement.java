package com.github.mishaninss.arma.html.composites;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.mishaninss.arma.html.containers.annotations.Element;
import com.github.mishaninss.arma.html.elements.ElementBuilder;
import com.github.mishaninss.arma.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.interfaces.INamed;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Element
public class TemplatedElement<T extends IInteractiveElement> implements IInteractiveElement, INamed {
    @Reporter
    private IReporter reporter;
    @Autowired
    private ElementBuilder elementBuilder;

    private final T element;
    private final Map<Object[], T> resolvedElements = new HashMap<>();

    public TemplatedElement(T element) {
        this.element = element;
    }

    @SuppressWarnings("unchecked")
    public T resolveTemplate(Object... args) {
        return resolvedElements.computeIfAbsent(args, key -> {
            T clone = elementBuilder.clone(element);
            clone.setLocator(String.format(clone.getLocator(), key));
            INamed.setNameIfApplicable(clone, INamed.getNameIfApplicable(clone).trim() + " [" + StringUtils.join(key, "; ") + "]");
            return clone;
        });
    }

    public List<T> resolveTemplates(Collection<?> args) {
        return
                args.stream()
                        .map(argsItem -> {
                            if (argsItem.getClass().isArray()) {
                                return resolveTemplate((Object[]) argsItem);
                            } else {
                                return resolveTemplate(argsItem);
                            }
                        })
                        .collect(Collectors.toList());
    }

    @Override
    public void changeValue(Object value) {
        resolveTemplate("").changeValue(value);
    }

    @Override
    public String readValue() {
        return resolveTemplate("").readValue();
    }

    @Override
    public void performAction() {
        resolveTemplate("").performAction();
    }

    @Override
    public boolean isDisplayed() {
        return resolveTemplate("").isDisplayed();
    }

    @Override
    public boolean isDisplayed(boolean shouldWait) {
        return resolveTemplate("").isDisplayed(shouldWait);
    }

    @Override
    public boolean isEnabled() {
        return resolveTemplate("").isEnabled();
    }

    @Override
    public boolean isOptional() {
        return element.isOptional();
    }

    @Override
    public void setOptional(boolean dynamic) {
        element.setOptional(dynamic);
    }

    @Override
    public IInteractiveContainer nextPage() {
        return element.nextPage();
    }

    @Override
    public void setNextPage(IInteractiveContainer nextPage) {
        element.setNextPage(nextPage);
    }

    @Override
    public void setNextPage(Class<? extends IInteractiveContainer> nextPage) {
        element.setNextPage(nextPage);
    }

    @Override
    public String getLocator() {
        return element.getLocator();
    }

    @Override
    public void setLocator(String locator) {
        element.setLocator(locator);
    }

    @Override
    public ILocatable getContext() {
        return element.getContext();
    }

    @Override
    public void setContext(ILocatable context) {
        element.setContext(context);
    }

    @Override
    public void setContextLookup(boolean contextLookup) {
        element.setContextLookup(contextLookup);
    }

    @Override
    public boolean useContextLookup() {
        return element.useContextLookup();
    }

    @Override
    public INamed setName(String name) {
        INamed.setNameIfApplicable(element, name);
        return this;
    }

    @Override
    public String getName() {
        return INamed.getNameIfApplicable(element);
    }
}
