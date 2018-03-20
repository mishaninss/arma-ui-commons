package com.github.mishaninss.data;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DataObjectUtils {

    public static Map<String, Object> readDataFromObject(Iterable<String> desiredProperties, Object object){
        Map<String, Object> data = new HashMap<>();
        desiredProperties.forEach(property -> {
            Object value = readProperty(object, property);
            if (value != null){
                data.put(property, value);
            }
        });
        return data;
    }

    public static <T> T putDataToObject(Map<String, String> data, T object){
        data.forEach((property, value) -> setProperty(object, property, value));
        return object;
    }

    public static <T> T putDataToObject(Map<String, String> data, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T object = clazz.newInstance();
        return putDataToObject(data, object);
    }

    private static <T> void setProperty(T object, String property, String value){
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
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private static Object readProperty(Object object, String property){
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
        } catch (Exception ex){
            return null;
        }
        return null;
    }

    private static Method findSetter(Class<?> clazz, String property){
        String setterName = "set" + StringUtils.capitalize(property);
        return MethodUtils.getMatchingAccessibleMethod(clazz, setterName, String.class);
    }

    private static Method findGetter(Class<?> clazz, String property){
        String getterName = "get" + StringUtils.capitalize(property);
        Method getter = MethodUtils.getMatchingAccessibleMethod(clazz, getterName);
        return  getter;
    }

    private static Field findPropertyField(Class<?> clazz, String property){
        Field field = FieldUtils.getField(clazz, property, true);
        return field != null && field.getType().equals(String.class) ? field : null;
    }
}
