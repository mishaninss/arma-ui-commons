package com.github.mishaninss.arma.html.containers.table;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import com.github.mishaninss.arma.html.actions.AbstractAction;
import com.github.mishaninss.arma.html.containers.annotations.Element;
import com.github.mishaninss.arma.html.elements.interfaces.IReadable;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.interfaces.INamed;
import com.github.mishaninss.arma.html.readers.AbstractReader;
import com.github.mishaninss.arma.uidriver.annotations.ElementsDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementsDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Controller of a table column.
 */
@SuppressWarnings("unused")
@Element
public class Column<T extends IInteractiveElement> implements InitializingBean {
    @ElementsDriver
    private IElementsDriver elementsDriver;
    @Autowired
    private ApplicationContext applicationContext;

    private final T cell;
    private final String cellLocatorTemplate;

    private String name;
    private ILocatable elementForCounting;
    private int colIndex = 0;
    private int startRowIndex = 0;
    private Table table;

    public Column(T cell) {
        cellLocatorTemplate = cell.getLocator();
        this.cell = cell;
    }

    public Column(T cell, int colIndex) {
        this(cell);
        this.colIndex = colIndex;
    }

    @Override
    public void afterPropertiesSet() {
        setLocatorForCounting(makeLocatorForCounting(cellLocatorTemplate));
    }

    /**
     * Returns controller for a cell of this column with specified row index
     *
     * @param rowIndex row index of a cell
     * @return cell controller
     */
    public T getCell(int rowIndex) {
        String locator = getArgsCountInLocatorTemplate(cellLocatorTemplate) > 1 ?
                String.format(cellLocatorTemplate, startRowIndex + rowIndex, colIndex) :
                String.format(cellLocatorTemplate, startRowIndex + rowIndex);
        cell.setLocator(locator);
        if (StringUtils.isNoneBlank(name) && cell instanceof INamed) {
            INamed.setNameIfApplicable(cell, name + " [" + rowIndex + "]");
        }
        return cell;
    }

    /**
     * Determines current rows count in this column
     *
     * @return rows count
     */
    public int getRowsCount() {
        return elementsDriver.getElementsCount(elementForCounting);
    }

    /**
     * Reads value of a cell with specified row index
     *
     * @param rowIndex row index of a cell
     * @return cell value
     */
    public String readValue(int rowIndex) {
        if (!IReadable.isReadable(cell)) {
            return null;
        }

        return getCell(rowIndex).readValue();
    }

    public String readValue(int rowIndex, AbstractReader reader) {
        if (!IReadable.isReadable(cell)) {
            return null;
        }

        return getCell(rowIndex).readValue(reader);
    }

    public void performAction(int rowIndex) {
        getCell(rowIndex).performAction();
    }

    public void performAction(int rowIndex, AbstractAction action) {
        getCell(rowIndex).performAction(action);
    }

    /**
     * Reads values of all cells in this column
     *
     * @return List of values
     */
    public List<String> readValues() {
        int rowsCount = getRowsCount();
        List<String> values = new ArrayList<>(rowsCount);
        for (int rowIndex = 1; rowIndex <= rowsCount; rowIndex++) {
            values.add(readValue(rowIndex));
        }
        return values;
    }

    public List<String> readValues(AbstractReader reader) {
        int rowsCount = getRowsCount();
        List<String> values = new ArrayList<>(rowsCount);
        for (int rowIndex = 1; rowIndex <= rowsCount; rowIndex++) {
            values.add(readValue(rowIndex, reader));
        }
        return values;
    }

    /**
     * Reads values of cells with specified row indexes
     *
     * @param rowsToRead - row indexes to read
     * @return list of values
     */
    public Map<Integer, String> readValues(List<Integer> rowsToRead) {
        Map<Integer, String> values = new LinkedHashMap<>(rowsToRead.size());
        rowsToRead.forEach(rowIndex -> values.put(rowIndex, readValue(rowIndex)));
        return values;
    }

    /**
     * Performs search for specified value in this column
     *
     * @param searchValue - value to search
     * @return list of row indexes of cells with specified value
     */
    public List<Integer> findRows(String searchValue) {
        List<Integer> rows = new ArrayList<>();
        List<String> values = readValues();
        for (int i = 0; i < values.size(); i++) {
            if (searchValue.equals(values.get(i))) {
                rows.add(i + 1);
            }
        }
        return rows;
    }

    public List<Integer> findRows(String searchValue, AbstractReader reader) {
        List<Integer> rows = new ArrayList<>();
        List<String> values = readValues(reader);
        for (int i = 0; i < values.size(); i++) {
            if (searchValue.equals(values.get(i))) {
                rows.add(i + 1);
            }
        }
        return rows;
    }

    public List<Integer> fuzzyFindRows(String searchValue) {
        List<Integer> rows = new ArrayList<>();
        List<String> values = readValues();
        for (int i = 0; i < values.size(); i++) {
            if (StringUtils.normalizeSpace(searchValue).trim()
                    .equalsIgnoreCase(StringUtils.normalizeSpace(values.get(i)).trim())) {
                rows.add(i + 1);
            }
        }
        return rows;
    }

    /**
     * Performs search for specified value in this column
     *
     * @param searchValue - value to search
     * @return list of row indexes of cells with specified value
     */
    public int findRow(String searchValue) {
        return findRow(searchValue, Integer.MAX_VALUE);
    }

    public int findRow(String searchValue, int limit) {
        int rowsCount = Math.min(getRowsCount(), limit);
        for (int i = 1; i <= rowsCount; i++) {
            String value = readValue(i);
            if (searchValue.equals(value)) {
                return i;
            }
        }
        return 0;
    }

    public int findRow(String searchValue, AbstractReader reader) {
        int rowsCount = getRowsCount();
        for (int i = 1; i <= rowsCount; i++) {
            String value = readValue(i, reader);
            if (searchValue.equals(value)) {
                return i;
            }
        }
        return 0;
    }

    public int fuzzyFindRow(String searchValue) {
        int rowsCount = getRowsCount();
        for (int i = 1; i <= rowsCount; i++) {
            String value = StringUtils.normalizeSpace(readValue(i)).trim();
            if (searchValue.equalsIgnoreCase(StringUtils.normalizeSpace(value).trim())) {
                return i;
            }
        }
        return 0;
    }

    public int fuzzyFindRow(AbstractReader reader, String searchValue) {
        int rowsCount = getRowsCount();
        for (int i = 1; i <= rowsCount; i++) {
            String value = StringUtils.normalizeSpace(getCell(i).readValue(reader)).trim();
            if (searchValue.equalsIgnoreCase(StringUtils.normalizeSpace(value).trim())) {
                return i;
            }
        }
        return 0;
    }

    public int findRow(AbstractReader reader, String searchValue) {
        int rowsCount = getRowsCount();
        for (int i = 1; i <= rowsCount; i++) {
            String value = getCell(i).readValue(reader);
            if (searchValue.equals(value)) {
                return i;
            }
        }
        return 0;
    }

    public int findRow(Predicate<String> predicate) {
        return findRow(predicate, Integer.MAX_VALUE);
    }

    public int findRow(Predicate<String> predicate, int limit) {
        int rowsCount = Math.min(getRowsCount(), limit);
        for (int i = 1; i <= rowsCount; i++) {
            String value = readValue(i);
            if (predicate.test(value)) {
                return i;
            }
        }
        return 0;
    }

    List<Integer> findRows(List<Integer> rowsToSearch, String searchString) {
        List<Integer> rows = new ArrayList<>();
        Map<Integer, String> values = readValues(rowsToSearch);
        values.keySet().forEach(rowIndex ->
        {
            if (values.get(rowIndex).equals(searchString)) {
                rows.add(rowIndex);
            }
        });
        return rows;
    }

    private String makeLocatorForCounting(String locator) {
        return StringUtils.replace(locator, "%d", "*");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        INamed.setNameIfApplicable(cell, name);
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public void setStartRowIndex(int startRowIndex) {
        this.startRowIndex = startRowIndex;
    }

    public int getStartRowIndex() {
        return startRowIndex;
    }

    void setLocatorForCounting(String locatorForCounting) {
        initElementForCounting(locatorForCounting);
    }

    String getLocatorForCounting() {
        return elementForCounting.getLocator();
    }

    public void setTable(Table table) {
        this.table = table;
    }

    private void initElementForCounting(String locatorForCounting) {
        if (elementForCounting == null) {
            elementForCounting = applicationContext.getBean(cell.getClass(), cell);
        }
        elementForCounting.setLocator(locatorForCounting);
    }

    private static int getArgsCountInLocatorTemplate(String locator) {
        if (!locator.contains("%")) {
            return 0;
        }
        return StringUtils.countMatches(locator, "%");
    }
}