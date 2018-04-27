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

import com.github.mishaninss.html.elements.ArmaElement;
import com.github.mishaninss.html.elements.ElementBuilder;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("unchecked")
public class IndexedElementBuilder {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ElementBuilder elementBuilder;

    public IndexedElementBuilder withListeners(){
        elementBuilder.withListeners();
        return this;
    }

    public IndexedElementBuilder withListeners(boolean withListeners){
        elementBuilder.withListeners(withListeners);
        return this;
    }

    public IndexedElementBuilder withoutListeners(){
        elementBuilder.withoutListeners();
        return this;
    }

    public IndexedElementBuilder withContext(ILocatable context){
        elementBuilder.withContext(context);
        return this;
    }

    public IndexedElementBuilder raw(){
        return withoutListeners();
    }

    public IndexedElement<ArmaElement> xpath(String xpath){
        return xpath(xpath, ArmaElement.class);
    }

    public <T extends IInteractiveElement> IndexedElement<T> xpath(String xpath, Class<T> elementType){
        return applicationContext.getBean(IndexedElement.class, elementBuilder.xpath(xpath, elementType));
    }

    public IndexedElement<ArmaElement> css(String css){
        return css(css, ArmaElement.class);
    }

    public <T extends IInteractiveElement> IndexedElement<T> css(String css, Class<T> elementType){
        return applicationContext.getBean(IndexedElement.class, elementBuilder.css(css, elementType));
    }

    public IndexedElement<ArmaElement> id(String id){
        return id(id, ArmaElement.class);
    }

    public <T extends IInteractiveElement> IndexedElement<T> id(String id, Class<T> elementType){
        return applicationContext.getBean(IndexedElement.class, elementBuilder.id(id, elementType));
    }

    public IndexedElement<ArmaElement> name(String name){
        return name(name, ArmaElement.class);
    }

    public <T extends IInteractiveElement> IndexedElement<T> name(String name, Class<T> elementType){
        return applicationContext.getBean(IndexedElement.class, elementBuilder.name(name, elementType));
    }

    public IndexedElement<ArmaElement> link(String linkText){
        return link(linkText, ArmaElement.class);
    }

    public <T extends IInteractiveElement> IndexedElement<T> link(String linkText, Class<T> elementType){
        return applicationContext.getBean(IndexedElement.class, elementBuilder.link(linkText, elementType));
    }

    public IndexedElement<ArmaElement> partialLink(String partialLinkText){
        return partialLink(partialLinkText, ArmaElement.class);
    }

    public <T extends IInteractiveElement> IndexedElement<T> partialLink(String partialLink, Class<T> elementType){
        return applicationContext.getBean(IndexedElement.class, elementBuilder.partialLink(partialLink, elementType));
    }

    public IndexedElement<ArmaElement> tag(String tag){
        return tag(tag, ArmaElement.class);
    }

    public <T extends IInteractiveElement> IndexedElement<T> tag(String tag, Class<T> elementType){
        return applicationContext.getBean(IndexedElement.class, elementBuilder.tag(tag, elementType));
    }

    public IndexedElement<ArmaElement> className(String className){
        return className(className, ArmaElement.class);
    }

    public <T extends IInteractiveElement> IndexedElement<T> className(String className, Class<T> elementType){
        return applicationContext.getBean(IndexedElement.class, elementBuilder.className(className, elementType));
    }

    public IndexedElement<ArmaElement> text(String text){
        return text(text, ArmaElement.class);
    }

    public <T extends IInteractiveElement> IndexedElement<T> text(String text, Class<T> elementType){
        return applicationContext.getBean(IndexedElement.class, elementBuilder.text(text, elementType));
    }

    public IndexedElement<ArmaElement> partialText(String text){
        return partialText(text, ArmaElement.class);
    }

    public <T extends IInteractiveElement> IndexedElement<T> partialText(String text, Class<T> elementType){
        return applicationContext.getBean(IndexedElement.class, elementBuilder.partialText(text, elementType));
    }
}
