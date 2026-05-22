package com.revolsys.swing.table.highlighter;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;

public class BorderHighlighter implements Highlighter {
  private HighlightPredicate predicate;

  private Border border = null;

  public BorderHighlighter() {
    this(null, null);
  }

  public BorderHighlighter(final HighlightPredicate predicate) {
    this(predicate, null);
  }

  public BorderHighlighter(final HighlightPredicate predicate, final Border border) {
    this.predicate = predicate;
    this.border = border;
  }

  @Override
  public Component highlight(final Component renderer, final JTable table, final int viewRow,
    final int viewColumn) {
    if (this.border == null) {
      return renderer;
    }
    if (this.predicate.isHighlighted(renderer, table, viewRow, viewColumn)) {
      if (renderer instanceof JComponent) {
        ((JComponent)renderer).setBorder(this.border);
      }
    }
    return renderer;
  }

  public void setBorder(final Border border) {
    this.border = border;
  }

  public void setHighlightPredicate(final HighlightPredicate predicate) {
    this.predicate = predicate;
  }

}
