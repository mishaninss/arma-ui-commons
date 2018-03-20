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

package com.github.mishaninss.aspects;

import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openqa.selenium.StaleElementReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("unused")
@Aspect
public class UiDriverAspects {
    private static final Logger LOGGER = LoggerFactory.getLogger("Framework");

    @Autowired
    private IElementDriver elementDriver;

    @Pointcut("call(* com.github.uidriver.interfaces.IElementDriver.* (..)) && !call(* com.github.uidriver.interfaces.IElementDriver.clearCache (..))" )
    public void pointcutIElementDriverCall() {
        //Declaration of a pointcut for call to any IElementDriver interface method
    }

    @Pointcut("execution(* com.github.uidriver.interfaces.IElementsDriver.* (..))" )
    public void pointcutIElementsDriverCall() {
        //Declaration of a pointcut for call to any IElementsDriver interface method
    }

    @Pointcut("execution(* com.github.uidriver.interfaces.IPageDriver.* (..))" )
    public void pointcutIPageDriverCall() {
        //Declaration of a pointcut for call to any IElementsDriver interface method
    }

	@Around("pointcutIElementDriverCall() || pointcutIElementsDriverCall() || pointcutIPageDriverCall()")
    public Object adviceAroundIElementDriverMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.trace(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        try {
            return joinPoint.proceed();
        } catch (StaleElementReferenceException ex){
            LOGGER.trace("StaleElementReferenceException", ex);
            elementDriver.clearCache();
            return joinPoint.proceed();
        }
    }
}
