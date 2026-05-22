package com.revolsys.swing.table.lambda;

public interface LambdaStringValue<R> {

  default String getString(final Object value) {
    if (value == null) {
      return null;
    } else {
      return toString((R)value);
    }
  }

  String toString(R value);

}
