package com.revolsys.swing.table;

import java.text.Collator;
import java.util.Comparator;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jeometry.common.compare.NumericComparator;

public class BaseRowSorter extends TableRowSorter<TableModel> {

  public BaseRowSorter() {
  }

  public BaseRowSorter(final TableModel model) {
    super(model);
  }

  @Override
  public Comparator<?> getComparator(final int column) {
    final Class<?> columnClass = getModel().getColumnClass(column);
    final Comparator<?> comparator = super.getComparator(column);
    if (comparator != null) {
      return comparator;
    }
    if (columnClass == String.class) {
      return Collator.getInstance();
    } else if (Number.class.isAssignableFrom(columnClass)) {
      return new NumericComparator<>();
    } else if (Comparable.class.isAssignableFrom(columnClass)) {
      return Comparator.naturalOrder();
    } else {
      return Collator.getInstance();
    }
  }
}
