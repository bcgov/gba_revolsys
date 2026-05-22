package com.revolsys.swing.table.highlighter;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;

public class OutsideBorderHighlighter implements Highlighter {

  private final Border bottomBorder;

  private final boolean compound;

  private final boolean inner;

  private final Border middleBorder;

  private final Border topBorder;

  private final HighlightPredicate predicate;

  private final JTable table;

  public OutsideBorderHighlighter(final HighlightPredicate predicate, final JTable table,
    final Color color, final int thickness, final boolean compound, final boolean inner) {
    this.predicate = predicate;
    this.table = table;
    this.topBorder = BorderFactory.createMatteBorder(thickness, thickness, 0, thickness, color);
    this.middleBorder = BorderFactory.createMatteBorder(0, thickness, 0, thickness, color);
    this.bottomBorder = BorderFactory.createMatteBorder(0, thickness, thickness, thickness, color);
    this.compound = compound;
    this.inner = inner;
  }

  @Override
  public Component highlight(final Component renderer, final JTable table, final int viewRow,
    final int viewColumn) {
    if (!(renderer instanceof JComponent)) {
      return renderer;
    }
    if (this.predicate != null
      && !this.predicate.isHighlighted(renderer, table, viewRow, viewColumn)) {
      return renderer;
    }

    final int rowCount = this.table.getRowCount();
    Border border;
    if (viewRow == 0) {
      border = this.topBorder;
    } else if (viewRow == rowCount - 1) {
      border = this.bottomBorder;
    } else {
      border = this.middleBorder;
    }

    final JComponent component = (JComponent)renderer;
    final Border componentBorder = component.getBorder();
    if (this.compound && componentBorder != null) {
      if (this.inner) {
        border = BorderFactory.createCompoundBorder(componentBorder, border);
      } else {
        border = BorderFactory.createCompoundBorder(border, componentBorder);
      }
    }
    component.setBorder(border);
    return renderer;
  }

}
