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

package com.github.mishaninss.arma.html.elements;

import com.github.mishaninss.arma.html.containers.annotations.Element;
import com.github.mishaninss.arma.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;

@Element
public class NoopElement implements IInteractiveElement {
    private static final String WARNING = "You are using NOOP Element!";

    @Reporter
    private IReporter reporter;

    @Override
    public void changeValue(Object value) {
        reporter.warn(WARNING);
    }

    @Override
    public String readValue() {
        reporter.warn(WARNING);
        return null;
    }

    @Override
    public void performAction() {
        reporter.warn(WARNING);
    }

    @Override
    public boolean isDisplayed() {
        reporter.warn(WARNING);
        return false;
    }

    @Override
    public boolean isDisplayed(boolean shouldWait) {
        reporter.warn(WARNING);
        return false;
    }

    @Override
    public boolean isEnabled() {
        reporter.warn(WARNING);
        return false;
    }

    @Override
    public boolean isOptional() {
        reporter.warn(WARNING);
        return false;
    }

    @Override
    public void setOptional(boolean dynamic) {
        reporter.warn(WARNING);
    }

    @Override
    public IInteractiveContainer nextPage() {
        reporter.warn(WARNING);
        return null;
    }

    @Override
    public void setNextPage(Class<? extends IInteractiveContainer> nextPage) {
        reporter.warn(WARNING);
    }

    @Override
    public void setNextPage(IInteractiveContainer nextPage) {
        reporter.warn(WARNING);
    }

    @Override
    public String getLocator() {
        reporter.warn(WARNING);
        return null;
    }

    @Override
    public void setLocator(String locator) {
        reporter.warn(WARNING);
    }

    @Override
    public ILocatable getContext() {
        reporter.warn(WARNING);
        return null;
    }

    @Override
    public void setContext(ILocatable context) {
        reporter.warn(WARNING);
    }

    @Override
    public void setContextLookup(boolean contextLookup) {
        reporter.warn(WARNING);
    }

    @Override
    public boolean useContextLookup() {
        reporter.warn(WARNING);
        return false;
    }
}
