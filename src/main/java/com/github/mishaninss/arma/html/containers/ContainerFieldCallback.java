package com.github.mishaninss.arma.html.containers;

import com.github.mishaninss.arma.html.containers.annotations.Container;
import com.github.mishaninss.arma.html.containers.annotations.Nested;
import com.github.mishaninss.arma.html.interfaces.INamed;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ReflectionUtils;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ContainerFieldCallback implements ReflectionUtils.FieldCallback {

  private final Object bean;
  private final String beanName;
  private final DefaultListableBeanFactory factory;

  public ContainerFieldCallback(Object bean, ApplicationContext applicationContext,
      String beanName) {
    this.bean = bean;
    this.beanName = beanName;
    factory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext) applicationContext)
        .getBeanFactory();
  }

  @Override
  public void doWith(Field field) throws IllegalAccessException {
    var containerProps = field.getAnnotation(Container.class);
    ILocatable container = (ILocatable) FieldUtils.readField(field, bean, true);
    if (needPrototype(field, containerProps)) {
      synchronized (ContainerFieldCallback.class) {
        String containerBeanName = getContainerBeanName(containerProps, field);
        Class<?> beanClass = container.getClass();
        container = (ILocatable) getPrototype(beanClass, containerBeanName);
        if (container == null) {
          container = (ILocatable) BeanUtils.instantiateClass(beanClass);
          var bd = new GenericBeanDefinition();
          bd.setBeanClass(beanClass);
          bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
          factory.registerSingleton("proto_" + containerBeanName, container);
          factory.registerBeanDefinition(containerBeanName, bd);
          factory.autowireBean(container);
          factory.initializeBean(container, containerBeanName);
          String name = getName(field);
          if (StringUtils.isNotBlank(name) && container instanceof INamed){
            ((INamed) container).setName(name);
          }
          FieldUtils.writeField(field, bean, container, true);
        } else {
          String name = getName(field);
          if (StringUtils.isNotBlank(name) && container instanceof INamed){
            ((INamed) container).setName(name);
          }
          FieldUtils.writeField(field, bean, container, true);
          return;
        }
      }
    }

    String locator = ContainersFactory.getContainerLocator(containerProps);
    if (isNotBlank(locator)) {
      container.setLocator(locator);
    }

    if (field.isAnnotationPresent(Nested.class) && bean instanceof ILocatable) {
      ILocatable context = container.getContext();
      if (context != null) {
        context = context.getRealLocatableObjectDeque().peek();
        if (context != null) {
          context.setContext((ILocatable) bean);
        }
      } else {
        container.setContext((ILocatable) bean);
      }
    }
  }

  private boolean needPrototype(Field field, Container containerProps) {
    return isNotBlank(ContainersFactory.getContainerLocator(containerProps))
        || isNotBlank(containerProps.locator())
        || (field.isAnnotationPresent(Nested.class) && bean instanceof ILocatable);
  }

  private String getContainerBeanName(Container containerProps, Field field) {
    String containerBeanName = containerProps.value();
    return StringUtils.isNotBlank(containerBeanName) ?
        containerBeanName :
        this.beanName + " > " + field.getName();
  }

  private <T> T getPrototype(Class<T> beanClass, String beanName) {
    try {
      return factory.getBean(beanName, beanClass);
    } catch (Exception ex) {
      return null;
    }
  }

  private String getName(Field field) {
    String name = null;
    if (field.isAnnotationPresent(Container.class)) {
      Container props = field.getAnnotation(Container.class);
      name = props.value();
      if (StringUtils.isBlank(name)) {
        name = props.name();
      }
    }
    return name;
  }
}