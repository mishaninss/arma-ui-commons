package com.github.mishaninss.arma.html.containers.table.annotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.data.UiCommonsProperties;
import com.github.mishaninss.arma.html.elements.NoopElement;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
@Autowired
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Repeatable(IContextualTable.class)
public @interface ITable {
    String prop() default UiCommonsProperties.Application.APP_LOCALE;

    String val() default "";

    String locator() default "";

    String name() default "";

    String value() default "";

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

    Class<? extends ILocatable> context() default NoopElement.class;
}
