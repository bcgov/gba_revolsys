package com.revolsys.swing.table.highlighter;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;

public class FontHighlighter implements Highlighter {

  private final HighlightPredicate predicate;

  private final Font font;

  public FontHighlighter(final Font font) {
    this(null, font);
  }

  public FontHighlighter(final HighlightPredicate predicate, final Font font) {
    this.predicate = predicate;
    this.font = font;
  }

  @Override
  public Component highlight(final Component renderer, final JTable table, final int viewRow,
    final int viewColumn) {
    if (this.predicate != null
      && !this.predicate.isHighlighted(renderer, table, viewRow, viewColumn)) {
      return renderer;
    }
    if (this.font != null) {
      renderer.setFont(this.font);
    }
    return renderer;
  }
}
