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

package com.github.mishaninss.arma.html.elements;

import com.github.mishaninss.arma.html.containers.annotations.Element;
import com.github.mishaninss.arma.html.elements.interfaces.IEditable;
import com.github.mishaninss.arma.html.elements.interfaces.IReadable;
import com.github.mishaninss.arma.html.elements.interfaces.ISelect;
import com.github.mishaninss.arma.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.listeners.ElementEvent;
import com.github.mishaninss.arma.html.listeners.FiresEvent;
import com.github.mishaninss.arma.uidriver.annotations.SelectElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ISelectElementDriver;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;

/**
 * Controller for the standard HTML Select control.
 *
 * @author Sergey Mishanin
 */
@Element
@Primary
public class Select extends ArmaElement implements IEditable, IReadable, ISelect {

  @SelectElementDriver
  protected ISelectElementDriver selectElementDriver;
  private static final String ITEMS_SEPARATOR = "::";
  private Boolean multiple;

  public Select() {
  }

  /**
   * Creates new instance of the HtmlSelect class
   *
   * @param locator - the locator of this Select control.
   */
  public Select(final String locator) {
    super(locator);
  }

  public Select(String locator, IInteractiveContainer context) {
    super(locator, context);
  }

  public Select(IInteractiveElement element) {
    super(element);
  }

  /**
   * Clears all current selection and selects the option in this control by visible text.
   *
   * @param value - the option to select.
   */
  @Override
  @FiresEvent(ElementEvent.CHANGE_VALUE)
  public void changeValue(Object value) {
    deselectAll();
    selectByVisibleText(value.toString());
  }

  /**
   * Performs actions to select an option in this control by visible text.
   *
   * @param text - the visible text of the option to select.
   */
  @FiresEvent(ElementEvent.CHANGE_VALUE)
  public void selectByVisibleText(String text) {
    selectElementDriver.selectByVisibleText(this, text);
  }

  /**
   * Performs actions to select an option in this control by index.
   *
   * @param index - the index of the option to select.
   */
  @FiresEvent(ElementEvent.CHANGE_VALUE)
  public void selectByIndex(int index) {
    selectElementDriver.selectByIndex(this, index);
  }

  /**
   * Performs actions to select an option in this control by value.
   *
   * @param value - the value of the option to select.
   */
  @FiresEvent(ElementEvent.CHANGE_VALUE)
  public void selectByValue(String value) {
    selectElementDriver.selectByValue(this, value);
  }

  @FiresEvent(ElementEvent.CHANGE_VALUE)
  public void selectByTextContains(String value) {
    this.perform().click();
    this.elementBy().xpath(".//option[contains(text(),'%s')]", value).perform().click();
  }

  @FiresEvent(ElementEvent.CHANGE_VALUE)
  public void selectByAttribute(String attribute, String value) {
    this.perform().click();
    this.elementBy().css("option[%s='%s']", attribute, value).perform().click();
  }

  @Override
  public String readValue() {
    String[] selectedOptions = selectElementDriver.getAllSelectedOptions(this);
    return StringUtils.join(StringUtils.stripAll(selectedOptions), ITEMS_SEPARATOR);
  }

  public List<String> readValues() {
    String[] selectedOptions = selectElementDriver.getOptions(this);
    return Arrays.asList(selectedOptions);
  }

  /**
   * Deselects all selected options. Does nothing if this element doesn't support multiple
   * selection.
   *
   * @return this element
   */
  public void deselectAll() {
    if (multiple == null) {
      String multi = arma.element().getAttributeOfElement(this, "multiple");
      multiple = multi != null;
    }
    if (multiple) {
      selectElementDriver.deselectAll(this);
    }
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @FiresEvent(ElementEvent.READ_VALUE)
  public List<String> getOptions() {
    return Arrays.asList(selectElementDriver.getOptions(this));
  }

}