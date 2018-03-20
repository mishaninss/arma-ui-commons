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

package com.github.mishaninss.html.containers.interfaces;

import com.github.mishaninss.uidriver.Arma;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Sergey_Mishanin on 4/17/17.
 */
public interface IHaveUrl {
    void setUrl(String url);

    String getUrl();

    default String getUrl(Object... args){
        return String.format(getUrl(), args);
    }

    default void goToUrl(){
        String url = getUrl();
        if (StringUtils.isBlank(url)){
            throw new IllegalArgumentException("URL was not specified for this container");
        }
        Arma.get().page().goToUrl(url);
    }

    default void goToUrl(Object... args){
        String url = getUrl();
        if (StringUtils.isBlank(url)){
            throw new IllegalArgumentException("URL was not specified for this container");
        }
        url = String.format(url, args);
        Arma.get().page().goToUrl(url);
    }

    static String getUrlIfApplicable(Object object){
        if (object instanceof IHaveUrl){
            return ((IHaveUrl) object).getUrl();
        } else {
            return null;
        }
    }

    static void setUrlIfApplicable(Object object, String url){
        if (object instanceof IHaveUrl){
            ((IHaveUrl) object).setUrl(url);
        }
    }
}
