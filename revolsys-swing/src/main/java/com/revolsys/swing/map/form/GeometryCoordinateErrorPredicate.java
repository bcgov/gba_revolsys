package com.revolsys.swing.map.form;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;

import org.jeometry.common.awt.WebColors;

import com.revolsys.swing.field.BaseJTable;
import com.revolsys.swing.table.geometry.GeometryCoordinatesTableModel;
import com.revolsys.swing.table.highlighter.ColorHighlighter;
import com.revolsys.swing.table.highlighter.HighlightPredicate;
import com.revolsys.swing.table.highlighter.Highlighter;

public class GeometryCoordinateErrorPredicate implements Highlighter, HighlightPredicate {

  private static final Border ERROR_BORDER = BorderFactory.createLineBorder(WebColors.Red, 2);

  private static final ColorHighlighter ERROR_HIGHLIGHTER = new ColorHighlighter(
    WebColors.newAlpha(WebColors.Red, 127), Color.BLACK, Color.RED, Color.YELLOW);

  public static void add(final BaseJTable table) {
    final GeometryCoordinatesTableModel model = (GeometryCoordinatesTableModel)table.getModel();
    final GeometryCoordinateErrorPredicate predicate = new GeometryCoordinateErrorPredicate(model);
    table.addHighlighter(predicate);
  }

  public static String toString(final int[] vertexIndex) {
    return Arrays.toString(vertexIndex);
  }

  private final GeometryCoordinatesTableModel model;

  public GeometryCoordinateErrorPredicate(final GeometryCoordinatesTableModel model) {
    this.model = model;
  }

  @Override
  public Component highlight(final Component renderer, final JTable table, final int viewRow,
    final int viewColumn) {
    ERROR_HIGHLIGHTER.highlight(renderer, table, viewRow, viewColumn);

    if (this.isHighlighted(renderer, table, viewRow, viewColumn)) {
      if (renderer instanceof JComponent) {
        ((JComponent)renderer).setBorder(ERROR_BORDER);
      }
    }
    return renderer;
  }

  @Override
  public boolean isHighlighted(final Component renderer, final JTable table, final int viewRow,
    final int viewColumn) {
    final int rowIndex = table.convertRowIndexToModel(viewRow);
    final int columnIndex = table.convertColumnIndexToModel(viewColumn);
    final int axisIndex = columnIndex - this.model.getNumIndexItems();
    if (axisIndex >= 0 && axisIndex <= 1) {
      final JComponent component = (JComponent)renderer;
      final double value = this.model.getCoordinate(rowIndex, columnIndex);
      if (Double.isNaN(value) || Double.isInfinite(value)) {
        component.setToolTipText("Coordinate value " + value + " is invalid");
        return true;
      }
    }
    return false;
  }
}
