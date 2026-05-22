package com.revolsys.swing.table.highlighter;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

public class ColorHighlighter implements SelectedHighlighter {

  private final HighlightPredicate predicate;

  private final Color background;

  private final Color foreground;

  private final Color selectedBackground;

  private final Color selectedForeground;

  public ColorHighlighter() {
    this(null);
  }

  public ColorHighlighter(final Color cellBackground, final Color cellForeground) {
    this(null, cellBackground, cellForeground);
  }

  public ColorHighlighter(final Color cellBackground, final Color cellForeground,
    final Color selectedBackground, final Color selectedForeground) {
    this(null, cellBackground, cellForeground, selectedBackground, selectedForeground);
  }

  public ColorHighlighter(final HighlightPredicate predicate) {
    this(predicate, null, null);
  }

  public ColorHighlighter(final HighlightPredicate predicate, final Color cellBackground,
    final Color cellForeground) {
    this(predicate, cellBackground, cellForeground, null, null);
  }

  public ColorHighlighter(final HighlightPredicate predicate, final Color cellBackground,
    final Color cellForeground, final Color selectedBackground, final Color selectedForeground) {
    this.background = cellBackground;
    this.foreground = cellForeground;
    this.selectedBackground = selectedBackground;
    this.selectedForeground = selectedForeground;
    this.predicate = predicate;
  }

  @Override
  public Component highlight(final Component renderer, final JTable table, final int viewRow,
    final int viewColumn) {
    // predicate == null means always apply
    if (this.predicate != null
      && !this.predicate.isHighlighted(renderer, table, viewRow, viewColumn)) {
      return renderer;
    }
    return renderer;
  }

  /**
   * Called from BaseJTable.prepareRenderer which has isSelected context.
   * Replaces doHighlight(renderer, adapter) where adapter.isSelected() was used.
   */
  @Override
  public Component highlight(final Component renderer, final JTable table, final int viewRow,
    final int viewColumn, final boolean isSelected) {
    if (this.predicate != null
      && !this.predicate.isHighlighted(renderer, table, viewRow, viewColumn)) {
      return renderer;
    }
    if (isSelected) {
      if (this.selectedBackground != null) {
        renderer.setBackground(this.selectedBackground);
      }
      if (this.selectedForeground != null) {
        renderer.setForeground(this.selectedForeground);
      }
    } else {
      if (this.background != null) {
        renderer.setBackground(this.background);
      }
      if (this.foreground != null) {
        renderer.setForeground(this.foreground);
      }
    }
    return renderer;
  }
}
