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

package com.github.mishaninss.data;

import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

/**
 * Provides an Application Context properties
 * @author Sergey Mishanin
 */
@Component
public class UiCommonsProperties{

    @Autowired
    @Qualifier("uiCommonsDriverProps")
    private Driver driverProps;
    @Autowired
    @Qualifier("uiCommonsApplicationProps")
    private Application applicationProps;
    @Autowired
    @Qualifier("uiCommonsFrameworkProps")
    private Framework frameworkProps;

    private static final ThreadLocal<UiCommonsProperties> INSTANCES = new ThreadLocal<>();

    private UiCommonsProperties(){}

    @PostConstruct
    private void init(){
        INSTANCES.set(this);
    }

    @PreDestroy
    private void destroy(){
        INSTANCES.remove();
    }

    public static UiCommonsProperties get(){
        return INSTANCES.get();
    }

    public Driver driver() {
        return driverProps;
    }

    public Application application() {
        return applicationProps;
    }

    public Framework framework() {
        return frameworkProps;
    }

    /**
     * Contains a list of available environment property names
     */
    @Component("uiCommonsDriverProps")
    public static class Driver {
        public static final String TIMEOUTS_ELEMENT = "arma.driver.timeouts.element.presence";
        public static final String TIMEOUTS_PAGE_LOAD = "arma.driver.timeouts.page.load";
        public static final String TIMEOUTS_DRIVER_OPERATION = "arma.driver.timeouts.drivers.operation";
        public static final String BROWSER_LOGS_LEVEL = "arma.driver.browser.logs.level";

        @Autowired
        private Environment environment;
        @Reporter
        private IReporter reporter;

        @Value("${" + TIMEOUTS_ELEMENT + ":20000}")
        public int timeoutsElement;
        @Value("${" + TIMEOUTS_PAGE_LOAD + ":60000}")
        public int timeoutsPageLoad;
        @Value("${" + TIMEOUTS_DRIVER_OPERATION + ":60000}")
        public int timeoutsDriverOperation;

        public Level browserLogsLevel;

        public boolean areConsoleLogsEnabled(){
            return !Objects.equals(browserLogsLevel, Level.OFF);
        }

        @PostConstruct
        private void init(){
            String logsLevel = environment.getProperty(BROWSER_LOGS_LEVEL, "ALL");
            switch (logsLevel.toUpperCase()){
                case "ALL": browserLogsLevel = Level.ALL; break;
                case "INFO": browserLogsLevel = Level.INFO; break;
                case "WARNING": browserLogsLevel = Level.WARNING; break;
                case "CONFIG": browserLogsLevel = Level.CONFIG; break;
                case "FINE": browserLogsLevel = Level.FINE; break;
                case "FINER": browserLogsLevel = Level.FINER; break;
                case "FINEST": browserLogsLevel = Level.FINEST; break;
                case "SEVERE": browserLogsLevel = Level.SEVERE; break;
                case "OFF": browserLogsLevel = Level.OFF; break;
                default: reporter.warn("Unknown browser logs level: " + logsLevel);
            }
        }
    }

    @Component("uiCommonsFrameworkProps")
    public static class Framework{
        @Autowired
        private ApplicationContext applicationContext;

        public static final String DEFAULT_EVENT_HANDLERS = "arma.default.event.handlers";
        public static final String ARE_DEFAULT_LISTENERS_ENABLED = "arma.enable.default.listeners";
        public static final String BASE_CONFIG = "arma.base.config";
        public static final String FORCE_CLOSE = "arma.force.close";
        public static final String DEBUG_MODE = "arma.framework.debug.mode";
        public static final String SCREENSHOTS_DIR = "arma.framework.screenshots.dir";
        public static final String STACKTRACE_WHITE_LIST_PROPERTY = "arma.framework.stacktrace.whitelist";

        @Value("#{'${" + DEFAULT_EVENT_HANDLERS + ":}'.split(',')}")
        public Set<String> defaultEventHandlers;

        @Value("${" + ARE_DEFAULT_LISTENERS_ENABLED + ":true}")
        public boolean areDefaultListenersEnabled;

        @Value("${" + FORCE_CLOSE + ":false}")
        public boolean forceClose;

        @Value("${" + DEBUG_MODE + ":false}")
        public boolean debugMode;

        @Value("${" + SCREENSHOTS_DIR + ":./target}")
        public String screenshotsDir;

        public String[] stackTraceWhiteList;

        public Framework addPackageToStacktraceWhiteList(String packageName){
            stackTraceWhiteList = ArrayUtils.add(stackTraceWhiteList, packageName);
            return this;
        }

        @PostConstruct
        private void init(){
            String whiteListValue = applicationContext.getEnvironment().getProperty(STACKTRACE_WHITE_LIST_PROPERTY, "");
            Set<String> whiteList = Sets.newHashSet(StringUtils.split(whiteListValue,","));
            whiteList.removeIf(StringUtils::isBlank);
            if (!whiteList.isEmpty()){
                stackTraceWhiteList = whiteList.toArray(new String[0]);
            } else {
                stackTraceWhiteList = new String[0];
            }
        }

        public Framework enableForcedClosing(){
            forceClose = true;
            ((AnnotationConfigApplicationContext) applicationContext).registerShutdownHook();
            return this;
        }

        public Framework enableDefaultEventHandlers(){
            areDefaultListenersEnabled = true;
            return this;
        }

        public Framework dibableDefaultEventHandlers(){
            areDefaultListenersEnabled = false;
            return this;
        }
    }

    /**
     * Contains a list of available application property names
     */
    @Component("uiCommonsApplicationProps")
    public static class Application {
        public static final String APP_LOCALE = "arma.app.locale";
        public static final String APP_URL = "arma.app.url";
        public static final String APP_ENV = "arma.app.env";

        @Value("${" + APP_LOCALE + ":}")
        public String locale;
        @Value("${" + APP_URL + ":}")
        public String url;
        @Value("${" + APP_ENV + ":}")
        public String env;
    }
}