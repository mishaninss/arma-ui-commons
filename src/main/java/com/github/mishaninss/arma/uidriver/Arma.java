package com.github.mishaninss.arma.uidriver;

import com.github.mishaninss.arma.data.UiCommonsProperties;
import com.github.mishaninss.arma.html.composites.IndexedElementBuilder;
import com.github.mishaninss.arma.html.containers.ArmaContainer;
import com.github.mishaninss.arma.html.containers.ContainersFactory;
import com.github.mishaninss.arma.html.containers.table.Table;
import com.github.mishaninss.arma.html.elements.ElementBuilder;
import com.github.mishaninss.arma.uidriver.annotations.AlertHandler;
import com.github.mishaninss.arma.uidriver.annotations.BrowserDriver;
import com.github.mishaninss.arma.uidriver.annotations.ElementDriver;
import com.github.mishaninss.arma.uidriver.annotations.ElementsDriver;
import com.github.mishaninss.arma.uidriver.annotations.PageDriver;
import com.github.mishaninss.arma.uidriver.annotations.SelectElementDriver;
import com.github.mishaninss.arma.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IActionsChain;
import com.github.mishaninss.arma.uidriver.interfaces.IAlertHandler;
import com.github.mishaninss.arma.uidriver.interfaces.IBrowserDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementActionsChain;
import com.github.mishaninss.arma.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementWaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementsDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import com.github.mishaninss.arma.uidriver.interfaces.IPageDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ISelectElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IThisElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.data.DataObject;
import com.github.mishaninss.arma.exceptions.FrameworkConfigurationException;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.annotations.*;
import com.github.mishaninss.arma.uidriver.interfaces.*;

import static java.lang.String.format;

@Component
public class Arma implements InitializingBean, DisposableBean {
    private static final ThreadLocal<Arma> INSTANCES = new ThreadLocal<>();
    private static AnnotationConfigApplicationContext staticContext;
    private static ContextBuilder contextBuilder;

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private Environment environment;
    @ElementDriver
    private IElementDriver elementDriver;
    @ElementsDriver
    private IElementsDriver elementsDriver;
    @SelectElementDriver
    private ISelectElementDriver selectElementDriver;
    @PageDriver
    private IPageDriver pageDriver;
    @WaitingDriver
    private IWaitingDriver waitingDriver;
    @BrowserDriver
    private IBrowserDriver browserDriver;
    @Reporter
    private IReporter reporter;
    @AlertHandler
    private IAlertHandler alertHandler;
    @Autowired
    private ElementBuilder elementBuilder;
    @Autowired
    private IndexedElementBuilder indexedElementBuilder;
    @Autowired
    private UiCommonsProperties properties;
    @Autowired
    private ContainersFactory containersFactory;

    private ArmaContainer currentPage;

    public Arma() {
    }

    @Override
    public void afterPropertiesSet() {
        INSTANCES.set(this);
    }

    @Override
    public void destroy() {
        INSTANCES.remove();
    }

    public static Arma get() {
        Arma arma = INSTANCES.get();
        if (arma == null) {
            return up(null);
        } else {
            return arma;
        }
    }

    public static Arma get(String browserName) {
        Arma arma = INSTANCES.get();
        if (arma == null) {
            return up(browserName);
        } else {
            return arma;
        }
    }

    public static void kill() {
        Arma arma = INSTANCES.get();
        if (arma != null) {
            arma.close();
        }
    }

    public void close() {
        ((ConfigurableApplicationContext) applicationContext).close();
    }

    public ContainersFactory containersFactory() {
        return containersFactory;
    }

    public IElementDriver element() {
        return elementDriver;
    }

    public ISelectElementDriver selectElement() {
        return selectElementDriver;
    }

    public IElementsDriver elements() {
        return elementsDriver;
    }

    public void setCurrentPage(ArmaContainer currentPage) {
        this.currentPage = currentPage;
    }

    public void setCurrentPage(Class<? extends ArmaContainer> currentPageClass) {
        this.currentPage = page(currentPageClass);
    }

    public void setCurrentPage(String currentPageName) {
        this.currentPage = page(currentPageName);
    }

    public ArmaContainer currentPage() {
        return currentPage;
    }

    public IPageDriver page() {
        return pageDriver;
    }

    public <T extends ArmaContainer> T page(Class<T> pageClass) {
        return applicationContext.getBean(pageClass);
    }

    public ArmaContainer page(String pageQualifier) {
        Preconditions.checkArgument(StringUtils.isNotBlank(pageQualifier), "Идентификатор страницы не может быть пустым");
        try {
            return (ArmaContainer) applicationContext.getBean(DataObject.sanitizeElementId(pageQualifier));
        } catch (Exception ex) {
            try {
                return (ArmaContainer) applicationContext.getBean(StringUtils.normalizeSpace(pageQualifier));
            } catch (Exception e) {
                throw new FrameworkConfigurationException(format("Не удалось получить контроллер страницы [%s]", pageQualifier), e);
            }
        }
    }

    public Table table(String pageQualifier) {
        Preconditions.checkArgument(StringUtils.isNotBlank(pageQualifier), "Идентификатор таблицы не может быть пустым");
        try {
            return (Table) applicationContext.getBean(StringUtils.normalizeSpace(pageQualifier));
        } catch (Exception ex) {
            throw new FrameworkConfigurationException(format("Не удалось получить контроллер таблицы [%s]", pageQualifier), ex);
        }
    }

    public <T extends ArmaContainer> T open(Class<T> pageClass) {
        T page = page(pageClass);
        page.goToUrl();
        return page;
    }

    public <T extends ArmaContainer> T open(String url, Class<T> pageClass) {
        pageDriver.goToUrl(url);
        return applicationContext.getBean(pageClass);
    }

    public ApplicationContext applicationContext() {
        return applicationContext;
    }

    public Environment env() {
        return environment;
    }

    public IBrowserDriver browser() {
        return browserDriver;
    }

    public IAlertHandler alert() {
        return alertHandler;
    }

    public IWaitingDriver waiting() {
        return waitingDriver;
    }

    public IElementWaitingDriver waiting(ILocatable element) {
        return applicationContext.getBean(IElementWaitingDriver.class, element);
    }

    public IActionsChain actionsChain() {
        return applicationContext.getBean(IActionsChain.class);
    }

    public IElementActionsChain actionsChain(ILocatable element) {
        return applicationContext.getBean(IElementActionsChain.class, element);
    }

    public IThisElementDriver element(ILocatable element) {
        return applicationContext.getBean(IThisElementDriver.class, element);
    }

    public IReporter reporter() {
        return reporter;
    }

    public ElementBuilder elementBy() {
        return applicationContext.getBean(ElementBuilder.class);
    }

    public ElementBuilder elementBy(boolean withListeners) {
        return elementBy().withListeners(withListeners);
    }

    public ElementBuilder elementBy(ILocatable context) {
        return elementBy().withContext(context);
    }

    public IndexedElementBuilder elementsBy() {
        return applicationContext.getBean(IndexedElementBuilder.class);
    }

    public IndexedElementBuilder elementsBy(boolean withListeners) {
        return elementsBy().withListeners(withListeners);
    }

    public IndexedElementBuilder elementsBy(ILocatable context) {
        return elementsBy().withContext(context);
    }

    public UiCommonsProperties config() {
        return properties;
    }

    public static Arma chrome() {
        return get("chrome");
    }

    public static ContextBuilder using() {
        if (contextBuilder == null) {
            contextBuilder = new ContextBuilder();
        }
        return contextBuilder;
    }

    private static Arma up(String browserName) {
        if (staticContext == null) {
            using().profiles(browserName).build();
        } else {
            staticContext.getEnvironment().addActiveProfile(browserName);
        }
        staticContext.refresh();
        return staticContext.getBean(Arma.class);
    }

    static void setApplicationContext(AnnotationConfigApplicationContext context) {
        staticContext = context;
    }
}