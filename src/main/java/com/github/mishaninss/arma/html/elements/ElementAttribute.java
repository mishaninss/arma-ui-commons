package com.github.mishaninss.arma.html.elements;

public enum ElementAttribute {
    TYPE("type"),
    VALUE("value"),
    CLASS("class"),
    DOWNLOAD("download");

    private String name;

    ElementAttribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
