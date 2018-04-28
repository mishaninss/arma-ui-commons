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

package com.github.mishaninss.config;

import com.github.mishaninss.aspects.InteractiveElementAspects;
import com.github.mishaninss.html.containers.DefaultEventHandlersProviderImpl;
import com.github.mishaninss.html.containers.annotations.DefaultEventHandlersProvider;
import com.github.mishaninss.html.containers.interfaces.IDefaultEventHandlersProvider;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonsConfig.class)
public class UiCommonsConfig {

    @Bean
    public InteractiveElementAspects interactiveElementAspects() {
        return Aspects.aspectOf(InteractiveElementAspects.class);
    }

    @Bean(IDefaultEventHandlersProvider.QUALIFIER) @DefaultEventHandlersProvider
    public IDefaultEventHandlersProvider defaultEventHandlersProvider(){
        return new DefaultEventHandlersProviderImpl();
    }

}
