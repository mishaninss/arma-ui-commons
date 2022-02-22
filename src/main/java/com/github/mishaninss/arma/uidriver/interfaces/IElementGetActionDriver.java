package com.github.mishaninss.arma.uidriver.interfaces;

import com.github.mishaninss.arma.utils.Dimension;

public interface IElementGetActionDriver extends ILocatableWrapper {

  Dimension size();

  IPoint location();

  byte[] screenshot();
}
