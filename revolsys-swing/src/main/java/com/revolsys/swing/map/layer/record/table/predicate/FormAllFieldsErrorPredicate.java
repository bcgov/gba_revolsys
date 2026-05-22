package com.revolsys.swing.map.layer.record.table.predicate;

import java.awt.Color;
import java.awt.Component;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.jeometry.common.awt.WebColors;

import com.revolsys.swing.field.BaseJTable;
import com.revolsys.swing.map.form.LayerRecordForm;
import com.revolsys.swing.map.layer.record.table.model.LayerRecordTableModel;
import com.revolsys.swing.table.highlighter.ColorHighlighter;
import com.revolsys.swing.table.highlighter.HighlightPredicate;

public class FormAllFieldsErrorPredicate implements HighlightPredicate {

  public static void add(final LayerRecordForm form, final BaseJTable table) {
    final LayerRecordTableModel model = table.getTableModel();
    final FormAllFieldsErrorPredicate predicate = new FormAllFieldsErrorPredicate(form, model);
    addErrorHighlighters(table, predicate);
  }

  public static void addErrorHighlighters(final BaseJTable table,
    final HighlightPredicate predicate) {

    table.addHighlighter(new ColorHighlighter(
      HighlightPredicate.and(predicate, HighlightPredicate.EVEN),
      WebColors.newAlpha(WebColors.LightCoral, 127), WebColors.Black, WebColors.Red, Color.WHITE));

    table.addHighlighter(
      new ColorHighlighter(HighlightPredicate.and(predicate, HighlightPredicate.ODD),
        WebColors.LightCoral, WebColors.Black, WebColors.DarkRed, WebColors.White));
  }

  private final Reference<LayerRecordForm> form;

  private final LayerRecordTableModel model;

  public FormAllFieldsErrorPredicate(final LayerRecordForm form,
    final LayerRecordTableModel model) {
    this.form = new WeakReference<>(form);
    this.model = model;
  }

  @Override
  public boolean isHighlighted(final Component renderer, final JTable table, final int viewRow,
    final int viewColumn) {
    try {
      final int rowIndex = table.convertRowIndexToModel(viewRow);
      final String fieldName = this.model.getColumnFieldName(rowIndex);
      if (fieldName != null) {
        final LayerRecordForm form = this.form.get();
        if (!form.isFieldValid(fieldName)) {
          final JComponent jcomponent = (JComponent)renderer;
          form.setFieldInvalidToolTip(fieldName, jcomponent);
          return true;
        }
      }
    } catch (final IndexOutOfBoundsException e) {
    }
    return false;

  }
}
