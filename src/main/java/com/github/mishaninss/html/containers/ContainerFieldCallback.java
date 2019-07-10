/*
 * Copyright 2019 Sergey Mishanin
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

package com.github.mishaninss.html.containers;

import com.github.mishaninss.html.containers.annotations.Container;
import com.github.mishaninss.html.containers.annotations.Nested;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class ContainerFieldCallback implements ReflectionUtils.FieldCallback {

    private Object bean;

    public ContainerFieldCallback(Object bean) {
        this.bean = bean;
    }

    @Override
    public void doWith(Field field) throws IllegalAccessException {
        if (field.isAnnotationPresent(Container.class)) {
            Container containerProps = field.getAnnotation(Container.class);
            if (ILocatable.class.isAssignableFrom(field.getType())) {
                ILocatable container = (ILocatable) FieldUtils.readField(field, bean);

                String locator = ContainersFactory.getContainerLocator(containerProps);
                if (StringUtils.isNotBlank(locator)) {
                    container.setLocator(locator);
                }

                if (field.isAnnotationPresent(Nested.class) && bean instanceof ILocatable) {
                    container.setContext((ILocatable) bean);
                }
            }
        }
    }
}