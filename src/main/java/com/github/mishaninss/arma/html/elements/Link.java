package com.github.mishaninss.arma.html.elements;

import com.github.mishaninss.arma.html.elements.interfaces.IReadable;
import com.github.mishaninss.arma.html.listeners.ElementEvent;
import com.github.mishaninss.arma.html.listeners.FiresEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import com.github.mishaninss.arma.exceptions.InteractionException;
import com.github.mishaninss.arma.html.containers.annotations.Element;
import com.github.mishaninss.arma.html.interfaces.IInteractiveContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.readers.AttributeReader;
import com.github.mishaninss.arma.uidriver.interfaces.IDownloadsManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Element
@Primary
public class Link extends ArmaElement implements IReadable {

    @Autowired
    private IDownloadsManager downloadsManager;

    public Link() {
    }

    public Link(String locator) {
        super(locator);
    }

    public Link(String locator, IInteractiveContainer context) {
        super(locator, context);
    }

    public Link(IInteractiveElement element) {
        super(element);
    }

    @FiresEvent(ElementEvent.READ_VALUE)
    public String getHref() {
        return read().attribute(AttributeReader.HREF);
    }

    @FiresEvent(value = ElementEvent.ACTION, message = "сохранить файл по ссылке")
    public File download() {
        boolean modified = false;
        List<String> currentDownloads = downloadsManager.getDownloadedFileNames();
        try {
            if (!arma.element().hasAttribute(this, ElementAttribute.DOWNLOAD.getName())) {
                arma.element().setAttributeOfElement(this, ElementAttribute.DOWNLOAD.getName(), "");
                modified = true;
            }
            arma.element().clickOnElement(this);
            String fileName = arma.waiting().waitForCondition(() -> {
                List<String> newDownloads = downloadsManager.getDownloadedFileNames();
                newDownloads.removeAll(currentDownloads);
                return newDownloads.stream().anyMatch(fName -> !StringUtils.endsWith(fName, ".crdownload")) ? newDownloads.get(0) : null;
            }, "Ожидание скачивания файла");
            return StringUtils.isNotBlank(fileName) ? downloadsManager.getDownloadedFile(fileName) : null;
        } catch (IOException e) {
            throw new InteractionException("Ошибка доступа к скачанному файлу", e);
        } finally {
            if (modified) {
                arma.element(this).removeAttribute(ElementAttribute.DOWNLOAD.getName());
            }
        }
    }

}
