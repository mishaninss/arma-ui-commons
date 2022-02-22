package com.github.mishaninss.arma.html.containers.table;

import com.github.mishaninss.arma.html.containers.table.annotations.IColumn;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.interfaces.INamed;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.Arma;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Controller of a table.
 * Created by Sergey Mishanin
 */
@SuppressWarnings("unused")
@Component
@Primary
public class Table implements ILocatable, INamed, InitializingBean {
    private static final String EXCEPTION_UNKNOWN_COLUMN_NAME = "Неизвестное имя колонки [%s]. Досутпные именя: %s";
    private static final String EXCEPTION_UNKNOWN_COLUMN_INDEX = "Неизвестный индекс колонки [%d]. Доступные индексы: %s";
    private static final String EXCEPTION_EMPTY_COLUMNS_LIST = "Список колонок пуст";
    private static final String EXCEPTION_CANNOT_GET_ROWS_COUNT = "Невозможно получить количество строк в таблице";
    @Reporter
    private IReporter reporter;
    protected final Map<String, Column<IInteractiveElement>> namedColumns = new LinkedHashMap<>();
    protected final Map<Integer, Column<IInteractiveElement>> indexedColumns = new LinkedHashMap<>();
    private String locatorForCounting;
    private String locator;
    private ILocatable context;
    private boolean contextLookup = true;
    private String name;

    @Autowired
    protected Arma arma;

    @Override
    public void afterPropertiesSet() throws Exception {
        arma.containersFactory().initTable(this);
    }

    /**
     * Reads value of cell with specified column name and row index
     *
     * @param colName  - name of a column
     * @param rowIndex - row index
     * @return value of a cell
     */
    public String readCellValue(String colName, int rowIndex) {
        Column<?> column = findColumnByName(colName);
        return column.readValue(rowIndex);
    }

    /**
     * Reads value of cell with specified column index and row index
     *
     * @param colIndex - index of a column
     * @param rowIndex - row index
     * @return value of a cell
     */
    public String readCellValue(int colIndex, int rowIndex) {
        Column<?> column = findColumnByIndex(colIndex);
        return column.readValue(rowIndex);
    }

    /**
     * Reads all values from a row with specified index
     *
     * @param rowIndex - row index
     * @return values of a row as a Map, where key is a column name
     */
    public Map<String, String> readRowValues(int rowIndex) {
        Map<String, String> values = new HashMap<>();
        getColumns().forEach(column -> values.put(column.getName(), column.readValue(rowIndex)));
        return values;
    }

    /**
     * Reads values of specified columns from a row with specified index
     *
     * @param colNames - names of columns
     * @param rowIndex - row index
     * @return values of a row as a Map, where key is a column name
     */
    public Map<String, String> readRowValues(List<String> colNames, int rowIndex) {
        return readRowValues(colNames.toArray(new String[0]), rowIndex);
    }

    /**
     * Reads values of specified columns from a row with specified index
     *
     * @param colNames - names of columns
     * @param rowIndex - row index
     * @return values of a row as a Map, where key is a column name
     */
    public Map<String, String> readRowValues(String[] colNames, int rowIndex) {
        Map<String, String> values = new LinkedHashMap<>();

        for (String colName : colNames) {
            values.put(colName, findColumnByName(colName).readValue(rowIndex));
        }

        return values;
    }

    /**
     * Reads values of specified columns from a row with specified index
     *
     * @param colIndexes - indexes of columns
     * @param rowIndex   - row index
     * @return values of a row as a Map, where key is a column index
     */
    public Map<Integer, String> readRowValues(int[] colIndexes, int rowIndex) {
        Map<Integer, String> values = new LinkedHashMap<>();

        for (int colIndex : colIndexes) {
            values.put(colIndex, findColumnByIndex(colIndex).readValue(rowIndex));
        }

        return values;
    }

    public <T> T readRowValues(int rowIndex, T entity) {
        Map<String, String> values = new HashMap<>();
        getColumns().forEach(column -> values.put(column.getName(), column.readValue(rowIndex)));
        return mapToEntity(values, entity);
    }

    /**
     * Reads all values of a columns with specified name
     *
     * @param colName - name of a column
     * @return values of a column
     */
    public List<String> readColumnValues(String colName) {
        Column<? extends IInteractiveElement> column = findColumnByName(colName);
        return column.readValues();
    }

    /**
     * Reads all values of a columns with specified index
     *
     * @param colIndex - index of a column
     * @return values of a column
     */
    public List<String> readColumnValues(int colIndex) {
        Column<? extends IInteractiveElement> column = findColumnByIndex(colIndex);
        return column.readValues();
    }

    /**
     * Reads all values of this table by rows
     *
     * @return a List, where each element contains values of a row, represented as Map of column names and cell values
     */
    public List<Map<String, String>> readTableByRows() {
        return readValues(new ArrayList<>(namedColumns.keySet()));
    }

    /**
     * Reads all values of this table by columns
     *
     * @return a Map of column names and column values, represented as a List of cell values for each row in a column
     */
    public Map<String, List<String>> readTableByColumns() {
        Map<String, List<String>> values = new HashMap<>();
        getColumns().forEach(column ->
        {
            List<String> colValues = column.readValues();
            values.put(column.getName(), colValues);
        });

        return values;
    }

    /**
     * Reads all values of columns with specified names
     *
     * @param colNames - names of columns
     * @return a List, where each element contains values of a row, represented as Map of column names and cell values
     */
    public List<Map<String, String>> readValues(List<String> colNames) {
        return readValues(colNames.toArray(new String[0]));
    }

    /**
     * Reads all values of columns with specified names
     *
     * @param colNames - names of columns
     * @return a List, where each element contains values of a row, represented as Map of column names and cell values
     */
    public List<Map<String, String>> readValues(String... colNames) {
        List<Map<String, String>> values = new ArrayList<>();

        Column<? extends IInteractiveElement> column = getColumns().get(0);
        int rowsCount = column.getRowsCount();

        for (int rowIndex = 1; rowIndex <= rowsCount; rowIndex++) {
            values.add(readRowValues(colNames, rowIndex));
        }
        return values;
    }

    /**
     * Reads all values of columns with specified names
     *
     * @param colIndexes - names of columns
     * @return a List, where each element contains values of a row, represented as Map of column indexes and cell values
     */
    public List<Map<Integer, String>> readValues(int... colIndexes) {
        List<Map<Integer, String>> values = new ArrayList<>();

        Column<? extends IInteractiveElement> column = getColumns().get(0);
        int rowsCount = column.getRowsCount();

        for (int rowIndex = 1; rowIndex <= rowsCount; rowIndex++) {
            values.add(readRowValues(colIndexes, rowIndex));
        }
        return values;
    }

    /**
     * Reads values of specified rows of specified columns
     *
     * @param colNames   - names of columns
     * @param rowIndexes - indexes of rows
     * @return a List, where each element contains values of a row, represented as Map of column names and cell values
     */
    public List<Map<String, String>> readValues(String[] colNames, int[] rowIndexes) {
        List<Map<String, String>> values = new ArrayList<>();

        for (int rowIndex : rowIndexes) {
            values.add(readRowValues(colNames, rowIndex));
        }
        return values;
    }

    public int findRow(String colName, String searchString) {
        return findColumnByName(colName).findRow(searchString);
    }

    public int findRowIndex(Map<String, String> searchCriteria) {
        return findRowIndex(searchCriteria, Integer.MAX_VALUE);
    }

    public int findRowIndex(Map<String, String> searchCriteria, int limit) {
        int rowsCount = Math.min(getRowsCount(), limit);
        boolean found;
        for (int index = 1; index <= rowsCount; index++) {
            found = true;
            for (Map.Entry<String, String> entry : searchCriteria.entrySet()) {
                String value = readCellValue(entry.getKey(), index);
                if (!StringUtils.equals(value, entry.getValue())) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return index;
            }
        }
        return 0;
    }

    public int findRow(String columnName, Predicate<String> searchCriteria) {
        return findRow(Map.of(columnName, searchCriteria));
    }

    public int findRow(Map<String, Predicate<String>> searchCriteria) {
        return findRow(searchCriteria, Integer.MAX_VALUE);
    }

    public int findRow(Map<String, Predicate<String>> searchCriteria, int limit) {
        int rowsCount = Math.min(getRowsCount(), limit);
        boolean found;
        for (int index = 1; index <= rowsCount; index++) {
            found = true;
            for (Map.Entry<String, Predicate<String>> entry : searchCriteria.entrySet()) {
                String value = readCellValue(entry.getKey(), index);
                if (entry.getValue() != null && !entry.getValue().evaluate(value)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return index;
            }
        }
        return 0;
    }

    public List<Integer> findRows(Map<String, String> searchCriteria) {
        Set<String> colNames = searchCriteria.keySet();
        List<Integer> previousColumnResult = new ArrayList<>();
        for (String colName : colNames) {
            List<Integer> nextColumnResult;
            if (!previousColumnResult.isEmpty()) {
                nextColumnResult = findColumnByName(colName).findRows(previousColumnResult, searchCriteria.get(colName));
                nextColumnResult.retainAll(previousColumnResult);
            } else {
                nextColumnResult = findColumnByName(colName).findRows(searchCriteria.get(colName));
            }
            if (nextColumnResult.isEmpty()) {
                return nextColumnResult;
            }
            previousColumnResult = nextColumnResult;
        }
        return previousColumnResult;
    }

    public List<Integer> findRows(String colName, String searchString) {
        Column<? extends IInteractiveElement> column = findColumnByName(colName);
        return column.findRows(searchString);
    }

    private List<Column<IInteractiveElement>> getColumns() {
        if (!namedColumns.isEmpty()) {
            return new ArrayList<>(namedColumns.values());
        }
        List<Column<IInteractiveElement>> columns = new ArrayList<>();
        getColumnFields().forEach(field -> {
            try {
                columns.add((Column<IInteractiveElement>) FieldUtils.readField(field, this, true));
            } catch (IllegalAccessException ex) {
                reporter.ignoredException(ex);
            }
        });
        return columns;
    }

    private Column<IInteractiveElement> findColumnByIndex(int colIndex) {
        Column<IInteractiveElement> column = indexedColumns.get(colIndex);
        if (column == null || column.getColIndex() != colIndex) {
            indexedColumns.remove(colIndex);

            Optional<Column<IInteractiveElement>> opt = getColumns().stream().filter(col -> col.getColIndex() == colIndex).findFirst();
            if (opt.isPresent()) {
                column = opt.get();
                indexedColumns.put(colIndex, column);
            }
        }
        if (column == null) {
            String message = String.format(EXCEPTION_UNKNOWN_COLUMN_INDEX, colIndex, indexedColumns.keySet());
            throw new IllegalArgumentException(message);
        }
        return column;
    }

    public Column<IInteractiveElement> findColumnByName(String colName) {
        Column<IInteractiveElement> column = namedColumns.get(colName);
        if (column == null) {
            column = namedColumns.get(StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(colName))));
        }
        if (column == null || column.getName() == null || !column.getName().equals(colName)) {
            namedColumns.remove(colName);

            Optional<Column<IInteractiveElement>> opt = getColumns().stream().filter(col -> col.getName().equalsIgnoreCase(colName)).findFirst();
            if (opt.isPresent()) {
                column = opt.get();
                namedColumns.put(colName, column);
            }
        }
        if (column == null) {
            String message = String.format(EXCEPTION_UNKNOWN_COLUMN_NAME, colName, namedColumns.keySet());
            throw new IllegalArgumentException(message);
        }
        return column;
    }

    public List<Integer> findRows(Object entity) {
        Field[] fields = FieldUtils.getAllFields(entity.getClass());
        Map<String, String> searchCriteria = new HashMap<>();
        for (Field field : fields) {
            String colName = StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(field.getName()), " "));
            if (namedColumns.containsKey(colName)) {
                try {
                    Object value = FieldUtils.readField(entity, field.getName(), true);
                    if (value != null) {
                        searchCriteria.put(colName, String.valueOf(value));
                    }
                } catch (IllegalAccessException ex) {
                    reporter.ignoredException(ex);
                }
            }
        }
        return findRows(searchCriteria);
    }

    public String getLocatorForCounting() {
        return locatorForCounting;
    }

    public void setLocatorForCounting(String locatorForCounting) {
        this.locatorForCounting = locatorForCounting;
    }

    private List<Field> getColumnFields() {
        return Arrays.asList(FieldUtils.getFieldsWithAnnotation(this.getClass(), IColumn.class));
    }

    private <T> T mapToEntity(Map<String, String> data, T entity) {
        data.keySet().forEach(colName ->
        {
            String fieldName = StringUtils.remove(StringUtils.uncapitalize(colName), " ");
            try {
                Field filed = FieldUtils.getField(entity.getClass(), fieldName, true);
                if (filed != null) {
                    FieldUtils.writeField(entity, fieldName, data.get(colName), true);
                }
            } catch (IllegalAccessException ex) {
                reporter.ignoredException(ex);
            }
        });

        return entity;
    }

    public IInteractiveElement getColumnCell(String columnName, int index) {
        return findColumnByName(columnName).getCell(index);
    }

    public int getRowsCount() {
        if (MapUtils.isNotEmpty(namedColumns)) {
            return namedColumns.values().iterator().next().getRowsCount();
        }
        if (MapUtils.isNotEmpty(indexedColumns)) {
            return indexedColumns.values().iterator().next().getRowsCount();
        } else {
            throw new RuntimeException(EXCEPTION_CANNOT_GET_ROWS_COUNT + ". " + EXCEPTION_EMPTY_COLUMNS_LIST);
        }
    }

    public void setNamedColumns(Map<String, Column<IInteractiveElement>> columns) {
        namedColumns.clear();
        namedColumns.putAll(columns);
    }

    public void setIndexedColumns(Map<Integer, Column<IInteractiveElement>> columns) {
        indexedColumns.clear();
        indexedColumns.putAll(columns);
    }

    @Override
    public String getLocator() {
        return locator;
    }

    @Override
    public void setLocator(String locator) {
        this.locator = locator;
    }

    @Override
    public ILocatable getContext() {
        return context;
    }

    @Override
    public void setContext(ILocatable context) {
        this.context = context;
    }

    @Override
    public void setContextLookup(boolean contextLookup) {
        this.contextLookup = contextLookup;
    }

    @Override
    public boolean useContextLookup() {
        return contextLookup;
    }

    @Override
    public INamed setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isDisplayed() {
        return isDisplayed(false);
    }

    public boolean isDisplayed(boolean shouldWait) {
        if (StringUtils.isNotBlank(locator)) {
            return arma.element(this).isDisplayed(shouldWait);
        } else {
            return getRowsCount() > 0;
        }
    }
}