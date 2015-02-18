package com.revolsys.gis.data.model;

import java.util.Map.Entry;

public class DataObjectEntry implements Entry<String, Object> {

  private final DataObject dataObject;

  private final int index;

  public DataObjectEntry(final DataObject dataObject, final int index) {
    this.dataObject = dataObject;
    this.index = index;
  }

  @Override
  public String getKey() {
    return dataObject.getMetaData().getAttributeName(index);
  }

  @Override
  public Object getValue() {
    return dataObject.getValue(index);
  }

  @Override
  public Object setValue(final Object value) {
    dataObject.setValue(index, value);
    return value;
  }
}