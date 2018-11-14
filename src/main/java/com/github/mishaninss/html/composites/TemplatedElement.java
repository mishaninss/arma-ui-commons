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

package com.github.mishaninss.html.composites;

import com.github.mishaninss.html.containers.annotations.Element;
import com.github.mishaninss.html.elements.ElementBuilder;
import com.github.mishaninss.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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

    private T element;
    private Map<Object[], T> resolvedElements = new HashMap<>();

    public TemplatedElement(T element) {
        this.element = element;
    }

    @SuppressWarnings("unchecked")
    public T resolveTemplate(Object... args) {
        return resolvedElements.computeIfAbsent(args, key -> {
            T clone = elementBuilder.clone(element);
            clone.setLocator(String.format(clone.getLocator(), key));
            INamed.setNameIfApplicable(clone, INamed.getNameIfApplicable(clone).trim() + " [" + StringUtils.join(key, "; ") + "]");
            resolvedElements.put(key, clone);
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
