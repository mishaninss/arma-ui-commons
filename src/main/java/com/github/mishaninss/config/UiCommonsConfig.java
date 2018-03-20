package com.github.mishaninss.config;

import com.github.mishaninss.aspects.InteractiveElementAspects;
import com.github.mishaninss.aspects.UiDriverAspects;
import org.aspectj.lang.Aspects;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonsConfig.class)
public class UiCommonsConfig {

    @Bean(autowire = Autowire.BY_TYPE)
    public InteractiveElementAspects interactiveElementAspects() {
        return Aspects.aspectOf(InteractiveElementAspects.class);
    }

    @Bean(autowire = Autowire.BY_TYPE)
    public UiDriverAspects uiDriverAspects() {
        return Aspects.aspectOf(UiDriverAspects.class);
    }

}
