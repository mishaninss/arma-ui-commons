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

package com.github.mishaninss.data;

import com.github.mishaninss.html.containers.ContainersFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DataObjectUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataObjectUtils.class);

    private DataObjectUtils() {
    }

    public static Map<String, String> normalizeKeys(Map<String, String> data) {
        return data.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> ContainersFactory.sanitizeElementId(entry.getKey()),
                        Map.Entry::getValue
                ));
    }

    public static Map<String, Object> readDataFromObject(Iterable<String> desiredProperties, Object object) {
        Map<String, Object> data = new LinkedHashMap<>();
        desiredProperties.forEach(property -> {
            Object value = readProperty(object, property);
            if (value != null) {
                data.put(property, value);
            }
        });
        return data;
    }

    public static <T> T putDataToObject(Map<String, String> data, T object) {
        data.forEach((property, value) -> setProperty(object, property, value));
        return object;
    }

    public static <T> T putDataToObject(Map<String, String> data, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T object = clazz.newInstance();
        return putDataToObject(data, object);
    }

    private static <T> void setProperty(T object, String property, String value) {
        Class<?> clazz = object.getClass();
        try {
            Method setter = findSetter(clazz, property);
            if (setter != null) {
                MethodUtils.invokeMethod(object, true, setter.getName(), value);
            } else {
                Field field = findPropertyField(clazz, property);
                if (field != null) {
                    FieldUtils.writeField(field, object, value, true);
                }
            }
        } catch (Exception ex) {
            LOGGER.trace("Could not set {} property to {} data object", property, object.getClass());
        }
    }

    private static Object readProperty(Object object, String property) {
        Class<?> clazz = object.getClass();
        try {
            Method getter = findGetter(clazz, property);
            if (getter != null) {
                return MethodUtils.invokeMethod(object, true, getter.getName());
            } else {
                Field field = findPropertyField(clazz, property);
                if (field != null) {
                    return FieldUtils.readField(field, object, true);
                }
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    private static Method findSetter(Class<?> clazz, String property) {
        Method[] methods = clazz.getMethods();
        String getterName = "set_" + ContainersFactory.sanitizeElementId(property);
        for (Method method : methods) {
            if (StringUtils.equals(getterName, ContainersFactory.sanitizeElementId(method.getName()))) {
                return method;
            }
        }
        return null;
    }

    private static Method findGetter(Class<?> clazz, String property) {
        Method[] methods = clazz.getMethods();
        String getterName = "get_" + ContainersFactory.sanitizeElementId(property);
        for (Method method : methods) {
            if (StringUtils.equals(getterName, ContainersFactory.sanitizeElementId(method.getName()))) {
                return method;
            }
        }
        return null;
    }

    private static Field findPropertyField(Class<?> clazz, String property) {
        String propertyName = ContainersFactory.sanitizeElementId(property);
        Field[] fields = FieldUtils.getAllFields(clazz);
        Field field = null;
        for (Field f : fields) {
            if (StringUtils.equals(propertyName, ContainersFactory.sanitizeElementId(f.getName()))) {
                field = f;
                break;
            }
        }
        return field != null && field.getType().equals(String.class) ? field : null;
    }
}
