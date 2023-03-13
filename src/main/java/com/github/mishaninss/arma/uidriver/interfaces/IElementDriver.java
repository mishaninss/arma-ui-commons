package com.github.mishaninss.arma.uidriver.interfaces;

import com.github.mishaninss.arma.utils.Dimension;
import org.springframework.lang.NonNull;

/**
 * @author Sergey Mishanin
 */
public interface IElementDriver {

  String QUALIFIER = "IElementDriver";

  /**
   * Can be used for text inputs to clear the current value
   *
   * @param element - locator of the element
   */
  IElementDriver clearElement(@NonNull ILocatable element);

  /**
   * Simulates left click on the element
   *
   * @param element - locator of the element
   */
  IElementDriver clickOnElement(@NonNull ILocatable element);

  /**
   * Simulates left click on the element without waiting for element is clickable
   *
   * @param element - locator of the element
   */
  IElementDriver simpleClickOnElement(@NonNull ILocatable element);

  /**
   * Simulates right click on the element
   *
   * @param element - locator of the element
   */
  IElementDriver contextClickOnElement(@NonNull ILocatable element);

  /**
   * Simulates left click with a pressed key (eg. CTRL, SHIFT, ALT)
   *
   * @param element - locator of the element
   * @param key     - pressed key
   */
  IElementDriver clickOnElementWithKeyPressed(@NonNull ILocatable element, CharSequence key);

  /**
   * Get the value of a the given attribute of the element.
   *
   * @param element   - locator of the element
   * @param attribute - name of the attribute
   * @return the value of a the given attribute
   */
  String getAttributeOfElement(@NonNull ILocatable element, String attribute);

  /**
   * Get the visible inner text of this element, including sub-elements, without any leading or
   * trailing whitespace.
   *
   * @param element - locator of the element
   * @return The visible inner text of this element.
   */
  String getTextFromElement(@NonNull ILocatable element);

  /**
   * Get the full inner text of this element, including hidden text and text from sub-elements,
   * without any leading or trailing whitespace.
   *
   * @param element - locator of the element
   * @return The full inner text of this element.
   */
  String getFullTextFromElement(@NonNull ILocatable element);

  /**
   * Checks if element with specified locator is displayed on the page or not.
   *
   * @param element        - locator of the element.
   * @param waitForElement - true if you want to wait for an element existence; false otherwise.
   * @return true if element exists on the page and displayed; false otherwise.
   */
  boolean isElementDisplayed(@NonNull ILocatable element, boolean waitForElement);

  /**
   * Checks if the element is displayed on the page or not.
   *
   * @param element - locator of the element.
   * @return true if the element exists on the page and displayed; false otherwise.
   */
  boolean isElementDisplayed(@NonNull ILocatable element);

  /**
   * Checks if the element is enabled or not.
   *
   * @param element - locator of the element.
   * @return true if the element is enabled; false otherwise.
   */
  boolean isElementEnabled(@NonNull ILocatable element);

  /**
   * Checks if the element is selected or not.
   *
   * @param element - locator of the element.
   * @return true if the element is selected; false otherwise.
   */
  boolean isElementSelected(@NonNull ILocatable element);

  /**
   * Simulates typing into an element
   *
   * @param element    - locator of the element
   * @param keysToSend - keys to send
   */
  IElementDriver sendKeysToElement(@NonNull ILocatable element, CharSequence... keysToSend);

  /**
   * Performs scrolling to make the element visible on screen
   *
   * @param element - locator of the element
   */
  IElementDriver scrollToElement(@NonNull ILocatable element);

  byte[] takeElementScreenshot(@NonNull ILocatable element);

  void clearCache();

  void highlightElement(@NonNull ILocatable element);

  void unhighlightElement(@NonNull ILocatable element);

  String getTagName(@NonNull ILocatable element);

  IPoint getLocation(@NonNull ILocatable element);

  IElementDriver hoverElement(@NonNull ILocatable element);

  IElementDriver clickWithDelayElement(@NonNull ILocatable element);

  void addElementDebugInfo(@NonNull ILocatable element, final String info, final String tooltip);

  void removeElementDebugInfo();

  Object executeJsOnElement(@NonNull String javaScript, @NonNull ILocatable element);

  IElementDriver setValueToElement(@NonNull ILocatable element, String value);

  IElementDriver setAttributeOfElement(@NonNull ILocatable element, @NonNull String attribute,
      String value);

  void removeAttributeOfElement(ILocatable element, String attribute);

  boolean hasAttribute(ILocatable element, String attribute);

  String getCssValue(@NonNull ILocatable element, String cssValue);

  void scrollIntoView(@NonNull ILocatable element, boolean allignToTop);

  void scrollIntoViewIfNeeded(@NonNull ILocatable element, boolean allignToTop);

  Dimension getSize(ILocatable element);

  void setInputFile(@NonNull ILocatable element, String path);

  void clickOutsideOfElement(ILocatable locatable);
}