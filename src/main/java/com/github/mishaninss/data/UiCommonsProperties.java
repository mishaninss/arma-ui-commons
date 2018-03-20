package com.github.mishaninss.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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

        @Value("${" + TIMEOUTS_ELEMENT + ":20000}")
        public int timeoutsElement;
        @Value("${" + TIMEOUTS_PAGE_LOAD + ":60000}")
        public int timeoutsPageLoad;
        @Value("${" + TIMEOUTS_DRIVER_OPERATION + ":60000}")
        public int timeoutsDriverOperation;
    }

    @Component("uiCommonsFrameworkProps")
    public static class Framework{
        public static final String DEBUG_MODE = "arma.framework.debug.mode";

        @Value("${" + DEBUG_MODE + ":false}")
        public boolean debugMode;
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