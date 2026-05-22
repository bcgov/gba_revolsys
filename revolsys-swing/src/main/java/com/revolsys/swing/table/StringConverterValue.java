package com.revolsys.swing.table;

import org.jeometry.common.data.type.DataTypes;

public class StringConverterValue {

  private static final long serialVersionUID = 1L;

  public String getString(final Object value) {
    if (value == null) {
      return "-";
    } else {
      return DataTypes.toString(value);
    }
  }
}
