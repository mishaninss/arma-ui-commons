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

package com.github.mishaninss.uidriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Locator types
 */
public final class LocatorType {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocatorType.class);

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String XPATH = "xpath";
    public static final String LINK = "link";
    public static final String PARTIAL_LINK = "partialLink";
    public static final String TAG = "tag";
    public static final String CSS = "css";
    public static final String CLASS = "class";

    /** Hidden constructor */
    private LocatorType() {}

    public static String buildXpath(String locator){
        try {
            XPathFactory.newInstance().newXPath().compile(locator);
        } catch (XPathExpressionException ex){
            LOGGER.warn("Invalid XPath locator provided: {}", locator);
        }
        return buildLocator(locator, XPATH);
    }

    public static String buildCss(String locator){
        return buildLocator(locator, CSS);
    }

    public static String buildId(String locator){
        return buildLocator(locator, ID);
    }

    public static String buildName(String locator){
        return buildLocator(locator, NAME);
    }

    public static String buildClass(String locator){
        return buildLocator(locator, CLASS);
    }

    public static String buildTag(String locator){
        return buildLocator(locator, TAG);
    }

    public static String buildLink(String locator){
        return buildLocator(locator, LINK);
    }

    public static String buildPartialLink(String locator){
        return buildLocator(locator, PARTIAL_LINK);
    }

    private static String buildLocator(final String locator, final String type){
        if (locator == null){
            return locator;
        }
        if (!locator.trim().startsWith(type)){
            return type + "=" + locator.trim();
        } else {
            return locator.trim();
        }
    }

}
