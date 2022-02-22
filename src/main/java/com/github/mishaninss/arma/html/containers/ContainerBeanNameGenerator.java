package com.github.mishaninss.arma.html.containers;

import com.github.mishaninss.arma.html.containers.annotations.Container;
import com.github.mishaninss.arma.html.containers.annotations.ContextualContainer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.lang.NonNull;


public class ContainerBeanNameGenerator extends AnnotationBeanNameGenerator {

  @Override
  @NonNull
  public String generateBeanName(BeanDefinition definition,
      @NonNull BeanDefinitionRegistry registry) {
    try {
      Class<?> clazz = Class.forName(definition.getBeanClassName());
      if (clazz.isAnnotationPresent(ContextualContainer.class) || clazz
          .isAnnotationPresent(Container.class)) {
        return ContainersFactory.getContainerBeanId(clazz);
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    return super.generateBeanName(definition, registry);
  }
}