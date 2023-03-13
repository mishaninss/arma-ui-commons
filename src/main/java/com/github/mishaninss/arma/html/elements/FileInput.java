package com.github.mishaninss.arma.html.elements;

import com.github.mishaninss.arma.html.elements.interfaces.IEditable;
import com.github.mishaninss.arma.html.elements.interfaces.IReadable;
import com.github.mishaninss.arma.html.listeners.ElementEvent;
import com.github.mishaninss.arma.html.listeners.FiresEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Primary;
import com.github.mishaninss.arma.html.containers.annotations.Element;
import com.github.mishaninss.arma.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.readers.AttributeReader;
import com.github.mishaninss.arma.utils.FileUtils;

import java.io.File;

@Element
@Primary
public class FileInput extends ArmaElement implements IEditable, IReadable, InitializingBean {

    public FileInput() {
    }

    public FileInput(String locator) {
        super(locator);
    }

    public FileInput(String locator, IInteractiveContainer context) {
        super(locator, context);
    }

    public FileInput(IInteractiveElement element) {
        super(element);
    }

    @Override
    public void afterPropertiesSet() {
        reader = arma.applicationContext().getBean(AttributeReader.class, AttributeReader.VALUE);
    }

    @Override
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public void changeValue(Object value) {
        if (value instanceof File) {
            changeValue((File) value);
        } else {
            changeValue(value.toString());
        }
    }

    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public void changeValue(String pathToFile) {
        File file = FileUtils.getFile(pathToFile);
        changeValue(file);
    }

    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public void changeValue(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("Файл [%s] не существует", file.getAbsolutePath()));
        }
        arma.element().setInputFile(this, file.getAbsolutePath());
    }

    @FiresEvent(ElementEvent.READ_VALUE)
    public List<String> getAcceptedTypes(){
      String accept = read().attribute("accept");
      if(StringUtils.isNotBlank(accept)){
        return Arrays.asList(accept.split(","));
      } else {
        return new ArrayList<>();
      }
    }

}
