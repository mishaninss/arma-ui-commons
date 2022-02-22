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

package com.github.mishaninss.arma.uidriver;

import com.github.mishaninss.arma.data.UiCommonsProperties;
import com.github.mishaninss.arma.uidriver.annotations.BrowserDriver;
import com.github.mishaninss.arma.uidriver.annotations.ElementDriver;
import com.github.mishaninss.arma.uidriver.annotations.ElementsDriver;
import com.github.mishaninss.arma.uidriver.annotations.PageDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IBrowserDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementsDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IPageDriver;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import com.github.mishaninss.arma.config.UiCommonsConfig;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;

import java.lang.annotation.Annotation;

public class ContextBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextBuilder.class);

    private AnnotationConfigApplicationContext applicationContext;
    private boolean wasParentConfigRegistered;

    public ContextBuilder() {
        applicationContext = new AnnotationConfigApplicationContext();
    }

    public ContextBuilder baseConfig(Class<?>... configClass) {
        Preconditions.checkArgument(ArrayUtils.isNotEmpty(configClass), "Config classes was not provided");
        applicationContext.register(configClass);
        wasParentConfigRegistered = true;
        return this;
    }

    public ContextBuilder componentsLocations(String... pageObjectLocation) {
        if (ArrayUtils.isNotEmpty(pageObjectLocation)) {
            applicationContext.scan(pageObjectLocation);
        }
        return this;
    }

    public ContextBuilder reporter(Class<? extends IReporter> reporterClass) {
        registerBean(IReporter.class, reporterClass, Reporter.class, IReporter.QUALIFIER);
        return this;
    }

    public ContextBuilder elementDriver(Class<? extends IElementDriver> elementDriverClass) {
        registerBean(IElementDriver.class, elementDriverClass, ElementDriver.class, IElementsDriver.QUALIFIER);
        return this;
    }

    public ContextBuilder elementsDriver(Class<? extends IElementsDriver> elementsDriverClass) {
        registerBean(IElementsDriver.class, elementsDriverClass, ElementsDriver.class, IElementsDriver.QUALIFIER);
        return this;
    }

    public ContextBuilder pageDriver(Class<? extends IPageDriver> pageDriverClass) {
        registerBean(IPageDriver.class, pageDriverClass, PageDriver.class, IPageDriver.QUALIFIER);
        return this;
    }

    public ContextBuilder browserDriver(Class<? extends IBrowserDriver> browserDriverClass) {
        registerBean(IBrowserDriver.class, browserDriverClass, BrowserDriver.class, IBrowserDriver.QUALIFIER);
        return this;
    }

    public ContextBuilder profiles(String... profiles) {
        if (ArrayUtils.isNotEmpty(profiles)) {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            for (String profile : profiles) {
                environment.addActiveProfile(profile);
            }
        }
        return this;
    }

    public AnnotationConfigApplicationContext build() {
        if (!wasParentConfigRegistered) {
            Class<?> configClass = null;
            String baseConfig = System.getProperty(UiCommonsProperties.Framework.BASE_CONFIG);
            if (StringUtils.isNotBlank(baseConfig)) {
                try {
                    configClass = Class.forName(baseConfig);
                } catch (ClassNotFoundException e) {
                    LOGGER.warn("Couldn't load config class", e);
                }
            }
            if (configClass == null) {
                try {
                    configClass = Class.forName("com.github.mishaninss.arma.config.UiWdConfig");
                } catch (ClassNotFoundException ex) {
                    configClass = UiCommonsConfig.class;
                }
            }
            applicationContext.register(configClass);
        }
        Arma.setApplicationContext(applicationContext);
        return applicationContext;
    }

    private <T> void registerBean(Class<T> beanType, Class<? extends T> implementationClass, Class<? extends Annotation> qualifierClass, String beanName) {
        Preconditions.checkArgument(beanType != null, "Provided null bean type");
        Preconditions.checkArgument(implementationClass != null, "Provided null implementation class");
        try {
            T bean = implementationClass.newInstance();
            AnnotatedGenericBeanDefinition gbd = new AnnotatedGenericBeanDefinition(beanType);
            gbd.addQualifier(new AutowireCandidateQualifier(qualifierClass));
            gbd.setInstanceSupplier(() -> bean);
            applicationContext.registerBeanDefinition(beanName, gbd);
        } catch (Exception ex) {
            LOGGER.warn("Could not instantiate " + beanType.getCanonicalName() + " implementation class " + implementationClass.getCanonicalName(), ex);
        }
    }
}
