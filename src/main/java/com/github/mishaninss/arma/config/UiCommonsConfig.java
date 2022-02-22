package com.github.mishaninss.arma.config;

import com.github.mishaninss.arma.aspects.InteractiveElementAspects;
import com.github.mishaninss.arma.html.containers.DefaultEventHandlersProviderImpl;
import com.github.mishaninss.arma.html.containers.interfaces.IDefaultEventHandlersProvider;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import(CommonsConfig.class)
public class UiCommonsConfig {

  @Bean
  public InteractiveElementAspects interactiveElementAspects() {
    return Aspects.aspectOf(InteractiveElementAspects.class);
  }

  @Bean
  @Profile({"!ios & !android"})
  public IDefaultEventHandlersProvider defaultEventHandlersProvider() {
    return new DefaultEventHandlersProviderImpl();
  }

}
