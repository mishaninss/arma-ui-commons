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

import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.interfaces.IElementWaitingDriver;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openqa.selenium.TimeoutException;

@SuppressWarnings("unused")
@Aspect
public class IElementWaitingDriverAspects {
    @Reporter
    private IReporter reporter;

    @Pointcut("execution(* com.github.mishaninss.uidriver.interfaces.IElementWaitingDriver.* (..)) " +
            "&& !execution(* com.github.mishaninss.uidriver.interfaces.IElementWaitingDriver.quietly (..)) " +
            "&& !execution(* com.github.mishaninss.uidriver.interfaces.IElementWaitingDriver.isQuietly (..)) ")
    public void pointcutIElementWaitingDriverExecution() {
        //Declaration of a pointcut for call to any IElementDriver interface method
    }

    @Around("pointcutIElementWaitingDriverExecution()")
    public Object adviceAroundIElementDriverMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        IElementWaitingDriver elementWaitingDriver = (IElementWaitingDriver) joinPoint.getTarget();
        if (elementWaitingDriver.isQuietly()){
            try {
                return joinPoint.proceed();
            } catch (TimeoutException ex){
                reporter.debug("", ex);
                return null;
            }
        } else {
            return joinPoint.proceed();
        }
    }
}
