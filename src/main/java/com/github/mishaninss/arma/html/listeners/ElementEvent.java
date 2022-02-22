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

package com.github.mishaninss.arma.html.listeners;

public enum ElementEvent {
    CHANGE_VALUE("изменить значение элемента"),
    READ_VALUE("прочитать значение элемента"),
    ACTION("выполнить действие с элементом"),
    IS_DISPLAYED("проверить отображение элемента");

    private String text;

    ElementEvent(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }
}
