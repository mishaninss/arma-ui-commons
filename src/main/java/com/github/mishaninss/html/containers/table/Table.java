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

package com.github.mishaninss.html.containers.table;

import com.github.mishaninss.html.containers.table.annotations.IColumn;
import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Controller of a table.
 * Created by Sergey Mishanin
 */
@SuppressWarnings("unused")
@Component
public class Table implements ILocatable, INamed{
    @Reporter
    private IReporter reporter;

    private Map<String, Column<? extends IInteractiveElement>> namedColumns = new LinkedHashMap<>();
    private Map<Integer, Column<? extends IInteractiveElement>> indexedColumns = new LinkedHashMap<>();

    private String locatorForCounting;
    private String locator;
    private ILocatable context;
    private boolean contextLookup = true;
    private String name;

    private static final String EXCEPTION_UNKNOWN_COLUMN_NAME = "Unknown column name [%s]. Available names: %s";
    private static final String EXCEPTION_UNKNOWN_COLUMN_INDEX = "Unknown column index [%d]. Available indexes: %s";

    /**
     * Reads value of cell with specified column name and row index
     * @param colName - name of a column
     * @param rowIndex - row index
     * @return value of a cell
     */
    public String readCellValue(String colName, int rowIndex){
        Column column = findColumnByName(colName);
        return column.readValue(rowIndex);
    }

    /**
     * Reads value of cell with specified column index and row index
     * @param colIndex - index of a column
     * @param rowIndex - row index
     * @return value of a cell
     */
    public String readCellValue(int colIndex, int rowIndex){
        Column column = findColumnByIndex(colIndex);
        return column.readValue(rowIndex);
    }

    /**
     * Reads all values from a row with specified index
     * @param rowIndex - row index
     * @return values of a row as a Map, where key is a column name
     */
    public Map<String, String> readRowValues(int rowIndex){
        Map<String, String> values = new HashMap<>();
        getColumns().forEach(column -> values.put(column.getName(), column.readValue(rowIndex)));
        return values;
    }

    /**
     * Reads values of specified columns from a row with specified index
     * @param colNames - names of columns
     * @param rowIndex - row index
     * @return values of a row as a Map, where key is a column name
     */
    public Map<String, String> readRowValues(List<String> colNames, int rowIndex){
        return readRowValues(colNames.toArray(new String[0]), rowIndex);
    }

    /**
     * Reads values of specified columns from a row with specified index
     * @param colNames - names of columns
     * @param rowIndex - row index
     * @return values of a row as a Map, where key is a column name
     */
    public Map<String, String> readRowValues(String[] colNames, int rowIndex){
        Map<String, String> values = new LinkedHashMap<>();

        for (String colName: colNames){
            values.put(colName, findColumnByName(colName).readValue(rowIndex));
        }

        return values;
    }

    /**
     * Reads values of specified columns from a row with specified index
     * @param colIndexes - indexes of columns
     * @param rowIndex - row index
     * @return values of a row as a Map, where key is a column index
     */
    public Map<Integer, String> readRowValues(int[] colIndexes, int rowIndex){
        Map<Integer, String> values = new LinkedHashMap<>();

        for (int colIndex: colIndexes){
            values.put(colIndex, findColumnByIndex(colIndex).readValue(rowIndex));
        }

        return values;
    }

    public <T> T readRowValues(int rowIndex, T entity){
        Map<String, String> values = new HashMap<>();
        getColumns().forEach(column -> values.put(column.getName(), column.readValue(rowIndex)));
        return mapToEntity(values, entity);
    }

    /**
     * Reads all values of a columns with specified name
     * @param colName - name of a column
     * @return values of a column
     */
    public List<String> readColumnValues(String colName){
        Column<? extends IInteractiveElement> column = findColumnByName(colName);
        return column.readValues();
    }

    /**
     * Reads all values of a columns with specified index
     * @param colIndex - index of a column
     * @return values of a column
     */
    public List<String> readColumnValues(int colIndex){
        Column<? extends IInteractiveElement> column = findColumnByIndex(colIndex);
        return column.readValues();
    }

    /**
     * Reads all values of this table by rows
     * @return a List, where each element contains values of a row, represented as Map of column names and cell values
     */
    public List<Map<String, String>> readTableByRows(){
        return readValues(new ArrayList<>(namedColumns.keySet()));
    }

    /**
     * Reads all values of this table by columns
     * @return a Map of column names and column values, represented as a List of cell values for each row in a column
     */
    public Map<String, List<String>> readTableByColumns(){
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
     * @param colNames - names of columns
     * @return a List, where each element contains values of a row, represented as Map of column names and cell values
     */
    public List<Map<String, String>> readValues(List<String> colNames){
        return readValues(colNames.toArray(new String[0]));
    }

    /**
     * Reads all values of columns with specified names
     * @param colNames - names of columns
     * @return a List, where each element contains values of a row, represented as Map of column names and cell values
     */
    public List<Map<String, String>> readValues(String... colNames){
        List<Map<String,String>> values = new ArrayList<>();

        Column<? extends IInteractiveElement> column = getColumns().get(0);
        int rowsCount = column.getRowsCount();

        for (int rowIndex=1; rowIndex<= rowsCount; rowIndex++){
            values.add(readRowValues(colNames, rowIndex));
        }
        return values;
    }

    /**
     * Reads all values of columns with specified names
     * @param colIndexes - names of columns
     * @return a List, where each element contains values of a row, represented as Map of column indexes and cell values
     */
    public List<Map<Integer, String>> readValues(int... colIndexes){
        List<Map<Integer,String>> values = new ArrayList<>();

        Column<? extends IInteractiveElement> column = getColumns().get(0);
        int rowsCount = column.getRowsCount();

        for (int rowIndex=1; rowIndex<= rowsCount; rowIndex++){
            values.add(readRowValues(colIndexes, rowIndex));
        }
        return values;
    }

    /**
     * Reads values of specified rows of specified columns
     * @param colNames - names of columns
     * @param rowIndexes - indexes of rows
     @return a List, where each element contains values of a row, represented as Map of column names and cell values
     */
    public List<Map<String, String>> readValues(String[] colNames, int[] rowIndexes){
        List<Map<String,String>> values = new ArrayList<>();

        for (int rowIndex: rowIndexes){
            values.add(readRowValues(colNames, rowIndex));
        }
        return values;
    }

    public List<Integer> findRows(Map<String, String> searchCriteria){
        Set<String> colNames = searchCriteria.keySet();
        List<Integer> previousColumnResult = new ArrayList<>();
        for (String colName: colNames){
            List<Integer> nextColumnResult;
            if (!previousColumnResult.isEmpty()) {
                nextColumnResult = findColumnByName(colName).findRows(previousColumnResult, searchCriteria.get(colName));
                nextColumnResult.retainAll(previousColumnResult);
            } else {
                nextColumnResult = findColumnByName(colName).findRows(searchCriteria.get(colName));
            }
            if (nextColumnResult.isEmpty()){
                return nextColumnResult;
            }
            previousColumnResult = nextColumnResult;
        }
        return previousColumnResult;
    }

    public List<Integer> findRows(String colName, String searchString){
        Column<? extends IInteractiveElement> column = findColumnByName(colName);
        return column.findRows(searchString);
    }

    private List<Column<? extends IInteractiveElement>> getColumns(){
        if (!namedColumns.isEmpty()){
            return new ArrayList<>(namedColumns.values());
        }
        List<Column<? extends IInteractiveElement>> columns = new ArrayList<>();
        getColumnFields().forEach(field -> {
            try {
                columns.add((Column<IInteractiveElement>) FieldUtils.readField(field, this, true));
            } catch (IllegalAccessException ex) {
                reporter.ignoredException(ex);
            }
        });
        return columns;
    }

    private Column<? extends IInteractiveElement> findColumnByIndex(int colIndex){
        Column<? extends IInteractiveElement> column = indexedColumns.get(colIndex);
        if (column == null || column.getColIndex() != colIndex) {
            indexedColumns.remove(colIndex);

            Optional<Column<? extends IInteractiveElement>> opt = getColumns().stream().filter(col -> col.getColIndex() == colIndex).findFirst();
            if (opt.isPresent()) {
                column = opt.get();
                indexedColumns.put(colIndex, column);
            }
        }
        if (column == null){
            String message = String.format(EXCEPTION_UNKNOWN_COLUMN_INDEX, colIndex, indexedColumns.keySet());
            throw new IllegalArgumentException(message);
        }
        return column;
    }

    private Column<? extends IInteractiveElement> findColumnByName(String colName){
        Column<? extends IInteractiveElement> column = namedColumns.get(colName);
        if (column == null){
            column = namedColumns.get(StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(colName))));
        }
        if (column == null || column.getName() == null || !column.getName().equals(colName)) {
            namedColumns.remove(colName);

            Optional<Column<? extends IInteractiveElement>> opt = getColumns().stream().filter(col -> col.getName().equalsIgnoreCase(colName)).findFirst();
            if (opt.isPresent()) {
                column = opt.get();
                namedColumns.put(colName, column);
            }
        }
        if (column == null){
            String message = String.format(EXCEPTION_UNKNOWN_COLUMN_NAME, colName, namedColumns.keySet());
            throw new IllegalArgumentException(message);
        }
        return column;
    }

    public List<Integer> findRows(Object entity){
        Field[] fields = FieldUtils.getAllFields(entity.getClass());
        Map<String, String> searchCriteria = new HashMap<>();
        for (Field field: fields){
            String colName = StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(field.getName()), " "));
            if (namedColumns.keySet().contains(colName)){
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

    public void setLocatorForCounting(String locatorForCounting) {
        this.locatorForCounting = locatorForCounting;
    }

    public String getLocatorForCounting() {
        return locatorForCounting;
    }

    private List<Field> getColumnFields(){
        return Arrays.asList(FieldUtils.getFieldsWithAnnotation(this.getClass(), IColumn.class));
    }

    private <T> T mapToEntity(Map<String, String> data, T entity){
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

    public IInteractiveElement getColumnCell(String columnName, int index){
        return findColumnByName(columnName).getCell(index);
    }

    public int getRowsCount(){
        if (MapUtils.isNotEmpty(namedColumns)){
            return namedColumns.values().iterator().next().getRowsCount();
        }
        if (MapUtils.isNotEmpty(indexedColumns)){
            return indexedColumns.values().iterator().next().getRowsCount();
        } else {
            throw new RuntimeException("Cannot get rows count: columns collection is empty");
        }
    }

    public void setNamedColumns(Map<String, Column<? extends IInteractiveElement>> columns){
        namedColumns.clear();
        namedColumns.putAll(columns);
    }

    public void setIndexedColumns(Map<Integer, Column<? extends IInteractiveElement>> columns){
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
}