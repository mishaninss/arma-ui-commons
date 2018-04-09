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

package com.github.mishaninss.utils;

import com.github.mishaninss.data.UiCommonsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergey_Mishanin on 4/3/17.
 */
@Component
public final class UrlUtils {

    @Autowired
    private Environment environment;
    @Autowired
    private UiCommonsProperties properties;

    private UrlUtils(){}

    public String getRawAppUrl(){
        String appUrl = properties.application().url;
        Pattern p = Pattern.compile("(https://|http://|www\\.)(.*:.+@)(.+)");
        Matcher m = p.matcher(appUrl);
        if (m.matches()){
            return m.group(1) + m.group(3);
        } else {
            return appUrl;
        }
    }

    public static String getRawUrl(String url){
        Pattern p = Pattern.compile("(https://|http://|www\\.)(.*:.+@)(.+)");
        Matcher m = p.matcher(url);
        if (m.matches()){
            return m.group(3);
        } else {
            p = Pattern.compile("(https://|http://|www\\.)(.+)");
            m = p.matcher(url);
            if (m.matches()){
                return m.group(2);
            } else {
                return url;
            }
        }
    }

    public String getRawAppUrlWithoutProtocol(){
        String appUrl = getRawAppUrl();
        Pattern p = Pattern.compile("(https://|http://)(.+)");
        Matcher m = p.matcher(appUrl);
        if (m.matches()){
            return m.group(2);
        } else {
            return appUrl;
        }
    }

    public String resolveUrl(String url) {
        String resolvedUrl = url;
        if (!resolvedUrl.startsWith("http")){
            if (!resolvedUrl.startsWith("/")){
                resolvedUrl = "/" + resolvedUrl;
            }
            resolvedUrl = properties.application().url + resolvedUrl;
        }
        return environment.resolvePlaceholders(resolvedUrl);
    }


}
