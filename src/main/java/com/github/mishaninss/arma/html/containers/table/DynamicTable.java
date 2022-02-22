package com.github.mishaninss.arma.html.containers.table;

import com.github.mishaninss.arma.data.DataObject;
import com.github.mishaninss.arma.html.containers.ContainersFactory;
import com.github.mishaninss.arma.html.elements.ArmaElement;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller of a table. Created by Sergey Mishanin
 */
@SuppressWarnings("unused")
@Component
public class DynamicTable extends Table {

  @Autowired
  private ContainersFactory containersFactory;

  private Column<? extends IInteractiveElement> keyColumn;

  public void setKeyColumn(
      Column<? extends IInteractiveElement> keyColumn) {
    this.keyColumn = keyColumn;
  }

  @Override
  public List<String> readColumnValues(String colName) {
    return readColumnValues(findColumnByName(colName));
  }

  public List<String> readColumnValues(Column<? extends IInteractiveElement> column) {
    List<String> values = new ArrayList<>();
    if (keyColumn.getRowsCount() == 0) {
      return values;
    }
    scrollToTop();
    Set<String> keys = new HashSet<>();
    List<String> nextKeys;
    List<String> nextValues;
    do {
      int nextCount = keyColumn.getRowsCount();
      nextKeys = new ArrayList<>();
      nextValues = new ArrayList<>();
      for (var i = 1; i <= nextCount; i++) {
        nextKeys.add(((ArmaElement) keyColumn.getCell(i)).raw(IInteractiveElement::readValue));
        nextValues.add(((ArmaElement) column.getCell(i)).raw(IInteractiveElement::readValue));
      }
      for (var i = 0; i < nextKeys.size(); i++) {
        if (!keys.contains(nextKeys.get(i))) {
          values.add(nextValues.get(i));
        }
      }
      arma.element().scrollIntoView(keyColumn.getCell(nextCount), true);
      arma.waiting().waitForPageUpdate();
    } while (keys.addAll(nextKeys));
    return values;
  }

  @Override
  public IInteractiveElement getColumnCell(String columnName, int index) {
    return getColumnCell(findColumnByName(columnName), index);
  }

  public IInteractiveElement getColumnCell(Column<? extends IInteractiveElement> column,
      int index) {
    List<String> values = new ArrayList<>();
    if (keyColumn.getRowsCount() == 0) {
      return null;
    }
    scrollToTop();
    Set<String> keys = new HashSet<>();
    List<String> nextKeys = new ArrayList<>();
    var currentIndex = 0;
    do {
      nextKeys.clear();
      int nextCount = keyColumn.getRowsCount();
      for (var i = 1; i <= nextCount; i++) {
        String nextKey = ((ArmaElement) keyColumn.getCell(i)).raw(IInteractiveElement::readValue);
        if (!keys.contains(nextKey)) {
          currentIndex++;
          if (currentIndex == index) {
            IInteractiveElement element = column.getCell(i);
            if (keys.isEmpty()){
              return element;
            }
            arma.element().scrollToElement(element);
            nextCount = keyColumn.getRowsCount();
            for (var j = 1; j <= nextCount; j++) {
              String nextKey2 = ((ArmaElement) keyColumn.getCell(j)).raw(IInteractiveElement::readValue);
              if (StringUtils.equals(nextKey, nextKey2)) {
                return column.getCell(j);
              }
            }
          }
          nextKeys.add(nextKey);
        }
      }
      arma.element().scrollIntoView(column.getCell(nextCount), true);
      arma.waiting().waitForPageUpdate();
    } while (keys.addAll(nextKeys));
    return null;
  }

  public List<Map<String,String>> readValues(int limit){
    List<Map<String,String>> values = new ArrayList<>();
    scrollToTop();
    Set<String> keys = new HashSet<>();
    boolean added;
    do {
      added = false;
      int nextCount = keyColumn.getRowsCount();
      for (var i = 1; i <= nextCount; i++) {
        String nextKey = ((ArmaElement) keyColumn.getCell(i)).raw(IInteractiveElement::readValue);
        if (!keys.contains(nextKey)){
          int index = i;
          added = true;
          Map<String, String> row = new LinkedHashMap<>();
          namedColumns.forEach((name, column) -> {
            row.put(name, ((ArmaElement) column.getCell(index)).raw(IInteractiveElement::readValue));
          });
          values.add(row);
          keys.add(nextKey);
        }
      }
      arma.element().scrollIntoView(keyColumn.getCell(nextCount), true);
      arma.waiting().waitForPageUpdate();
    } while (added && keys.size() < limit);
    return values;
  }

  public List<Map<String,String>> readValues(int limit, Function<ArmaElement, String> reader){
    List<Map<String,String>> values = new ArrayList<>();
    scrollToTop();
    Set<String> keys = new HashSet<>();
    boolean added;
    do {
      added = false;
      int nextCount = keyColumn.getRowsCount();
      for (var i = 1; i <= nextCount; i++) {
        String nextKey = ((ArmaElement) keyColumn.getCell(i)).raw(IInteractiveElement::readValue);
        if (!keys.contains(nextKey)){
          int index = i;
          added = true;
          Map<String, String> row = new LinkedHashMap<>();
          namedColumns.forEach((name, column) -> {
            row.put(name, reader.apply((ArmaElement) column.getCell(index)));
          });
          values.add(row);
          keys.add(nextKey);
        }
      }
      arma.element().scrollIntoView(keyColumn.getCell(nextCount), true);
      arma.waiting().waitForPageUpdate();
    } while (added && keys.size() < limit);
    return values;
  }

  @Override
  public int findRow(String colName, String searchString) {
    return findRow(findColumnByName(colName), searchString);
  }

  public int findRow(Column<? extends IInteractiveElement> column, String searchString) {
    List<String> values = new ArrayList<>();
    if (keyColumn.getRowsCount() == 0) {
      return 0;
    }
    scrollToTop();
    Set<String> keys = new HashSet<>();
    List<String> nextKeys = new ArrayList<>();
    var index = 0;
    do {
      nextKeys.clear();
      int nextCount = keyColumn.getRowsCount();
      for (var i = 1; i <= nextCount; i++) {
        String nextKey = ((ArmaElement) keyColumn.getCell(i)).raw(IInteractiveElement::readValue);
        if (!keys.contains(nextKey)) {
          index++;
          String nextValue = ((ArmaElement) column.getCell(i)).raw(IInteractiveElement::readValue);
          if (StringUtils.equals(searchString, nextValue)) {
            return index;
          }
          nextKeys.add(nextKey);
        }
      }
      arma.element().scrollIntoView(keyColumn.getCell(nextCount), true);
      arma.waiting().waitForPageUpdate();
    } while (keys.addAll(nextKeys));
    return 0;
  }

  @Override
  public int getRowsCount() {
    if (keyColumn.getRowsCount() == 0) {
      return 0;
    }
    scrollToTop();
    Set<String> names = new HashSet<>();
    List<String> nextNames;
    do {
      int nextCount = keyColumn.getRowsCount();
      nextNames = new ArrayList<>();
      for (int i = 1; i <= nextCount; i++) {
        nextNames.add(((ArmaElement) keyColumn.getCell(i)).raw(IInteractiveElement::readValue));
      }
      arma.element().scrollIntoView(keyColumn.getCell(nextCount), true);
      arma.waiting().waitForPageUpdate();
    } while (names.addAll(nextNames));
    return names.size();
  }

  public void scrollToTop() {
    if (keyColumn.getRowsCount() == 0) {
      return;
    }
    String prevKey;
    String nextKey = keyColumn.readValue(1);
    do {
      prevKey = nextKey;
      arma.element().scrollIntoView(keyColumn.getCell(1), false);
      arma.waiting().waitForPageUpdate();
      nextKey = keyColumn.readValue(1);
    } while (!StringUtils.equals(prevKey, nextKey));
  }

  public void addColumn(String name, Column<? extends IInteractiveElement> column){
    namedColumns.put(DataObject.sanitizeElementId(name), (Column<IInteractiveElement>) column);
  }

  public void resetColumns(){
    namedColumns.clear();
    indexedColumns.clear();
    try {
      containersFactory.initTable(this);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}