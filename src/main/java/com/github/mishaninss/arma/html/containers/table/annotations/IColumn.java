package com.github.mishaninss.arma.html.containers.table.annotations;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.data.UiCommonsProperties;

import java.lang.annotation.*;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Repeatable(IContextualColumn.class)
public @interface IColumn {
    String prop() default UiCommonsProperties.Application.APP_LOCALE;

    String val() default "";

    String locator() default "";

    /**
     * If an element constructor requires multiple locators to be passed, use this parameter to specify a list of locators
     */
    String[] locators() default {};

    String name() default "";

    int index() default 0;

    int startIndex() default 0;

    /** Specifies XPath locator of an element */
    String byXpath() default "";

    /** Specifies CSS locator of an element */
    String byCss() default "";

    /** Specifies Name locator of an element */
    String byName() default "";

    /** Specifies Id locator of an element */
    String byId() default "";

    /** Specifies className locator of an element */
    String byClass() default "";

    /** Specifies tagName locator of an element */
    String byTag() default "";

    /** Specifies linkText locator of an element */
    String byLink() default "";

    /** Specifies partialLinkText locator of an element */
    String byPatrialLink() default "";

    String byArg() default "";
}