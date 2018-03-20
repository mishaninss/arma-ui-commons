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

import com.github.mishaninss.html.containers.BaseContainer;
import com.github.mishaninss.html.containers.annotations.Element;
import com.github.mishaninss.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.html.interfaces.IInteractiveElement;

@Element
public abstract class CompositeElement extends BaseContainer implements IInteractiveElement {

    private boolean optional = false;
    private IInteractiveContainer nextPage;

    public CompositeElement(String locator){
        this.locator = locator;
    }

    public CompositeElement(CompositeElement element){
        super(element);
        setOptional(element.isOptional());
        setNextPage(element.nextPage());
    }

    @Override
    public boolean isEnabled() {
        return elementDriver.isElementEnabled(this);
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public IInteractiveContainer nextPage(){
        if (nextPage == null && IInteractiveContainer.class.isAssignableFrom(context.getClass())){
            nextPage = (IInteractiveContainer) context;
        }
        return nextPage;
    }

    @Override
    public void setNextPage(Class<? extends IInteractiveContainer> nextPage) {
        this.nextPage = applicationContext.getBean(nextPage);
    }

    @Override
    public void setNextPage(IInteractiveContainer nextPage) {
        this.nextPage = nextPage;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
