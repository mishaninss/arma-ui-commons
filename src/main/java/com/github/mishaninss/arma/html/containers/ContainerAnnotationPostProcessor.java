package com.github.mishaninss.arma.html.containers;

import com.github.mishaninss.arma.html.containers.annotations.Container;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class ContainerAnnotationPostProcessor implements BeanPostProcessor {

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    configureFieldInjection(bean, beanName);
    if (applicationContext.containsBean("proto_" + beanName)) {
      ArmaContainer proto = applicationContext.getBean("proto_" + beanName, ArmaContainer.class);
      if (proto.getContext() != null) {
        ((ArmaContainer) bean).setContext(proto.getContext());
      }
      if (StringUtils.isNotBlank(proto.getLocator())) {
        ((ArmaContainer) bean).setLocator(proto.getLocator());
      }
    }
    return bean;
  }

  private void configureFieldInjection(Object bean, String beanName) {
    Class<?> managedBeanClass = bean.getClass();
    ReflectionUtils.FieldCallback fieldCallback = new ContainerFieldCallback(bean,
        applicationContext, beanName);
    ReflectionUtils.doWithFields(managedBeanClass, fieldCallback,
        field -> field.isAnnotationPresent(Container.class)
            && ILocatable.class.isAssignableFrom(field.getType())
    );
  }
}