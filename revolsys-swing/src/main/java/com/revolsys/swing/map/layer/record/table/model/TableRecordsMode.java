package com.revolsys.swing.map.layer.record.table.model;

import java.awt.Color;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.ListSelectionModel;

import com.revolsys.record.query.Query;
import com.revolsys.swing.map.layer.record.LayerRecord;

public interface TableRecordsMode {
  default void activate() {
  }

  default void deactivate() {
  }

  void exportRecords(final Query query, final Object target, boolean tableColumnsOnly);

  void forEachRecord(Query query, final Consumer<? super LayerRecord> action);

  Color getBorderColor();

  Icon getIcon();

  String getKey();

  /**
   * The maximum number of records that can be loaded into the table
   * at once. For paged mode, its going to be the pages size * the number of
   * pages in the cache, for others its the record count.  Default
   * is the record count.
   * @return
   */
  default int getMaximumVisibleRecords() {
    return getRecordCount();
  }

  LayerRecord getRecord(int index);

  int getRecordCount();

  ListSelectionModel getSelectionModel();

  String getTitle();

  default boolean isFilterByBoundingBoxSupported() {
    return false;
  }

  default boolean isSortable() {
    return true;
  }

  void refresh();
}
