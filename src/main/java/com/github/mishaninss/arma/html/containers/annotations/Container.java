package com.github.mishaninss.arma.html.containers.annotations;

import com.github.mishaninss.arma.html.elements.NoopElement;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Component
@Autowired
@Primary
@Repeatable(ContextualContainer.class)
public @interface Container {

  String prop() default Element.PROFILE_PROPERTY;

  String val() default "";

  String locator() default "";

  String locators() default "";

  String name() default "";

  String value() default "";

  /**
   * Specifies XPath locator of an element
   */
  String byXpath() default "";

  /**
   * Specifies CSS locator of an element
   */
  String byCss() default "";

  /**
   * Specifies Name locator of an element
   */
  String byName() default "";

  /**
   * Specifies Id locator of an element
   */
  String byId() default "";

  /**
   * Specifies className locator of an element
   */
  String byClass() default "";

  /**
   * Specifies tagName locator of an element
   */
  String byTag() default "";

  /**
   * Specifies linkText locator of an element
   */
  String byLink() default "";

  /**
   * Specifies partialLinkText locator of an element
   */
  String byPatrialLink() default "";

  String byArg() default "";

  Class<? extends ILocatable> context() default NoopElement.class;
}
