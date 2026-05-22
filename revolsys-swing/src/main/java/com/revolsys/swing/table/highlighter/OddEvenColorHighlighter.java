package com.revolsys.swing.table.highlighter;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import org.jeometry.common.awt.WebColors;

public class OddEvenColorHighlighter implements SelectedHighlighter {

  private final Color backgroundSelected;

  private final Color background;

  private final Color backgroundSelectedOdd;

  private final Color backgroundOdd;

  private Color foreground = WebColors.Black;

  private Color foregroundSelected = WebColors.White;

  private final HighlightPredicate predicate;

  public OddEvenColorHighlighter(final HighlightPredicate predicate, final Color background,
    final Color backgroundSelected) {
    this.predicate = predicate;
    this.background = background;
    this.backgroundOdd = new Color(Math.max((int)(background.getRed() * 0.9), 0),
      Math.max((int)(background.getGreen() * 0.9), 0),
      Math.max((int)(background.getBlue() * 0.9), 0));
    this.backgroundSelected = backgroundSelected;
    this.backgroundSelectedOdd = new Color(Math.max((int)(backgroundSelected.getRed() * 0.9), 0),
      Math.max((int)(backgroundSelected.getGreen() * 0.9), 0),
      Math.max((int)(backgroundSelected.getBlue() * 0.9), 0));
  }

  public HighlightPredicate getHighlightPredicate() {
    return this.predicate;
  }

  @Override
  public Component highlight(final Component renderer, final JTable table, final int viewRow,
    final int viewColumn) {
    return renderer; // isSelected not available — use overload below
  }

  @Override
  public Component highlight(final Component renderer, final JTable table, final int viewRow,
    final int viewColumn, final boolean isSelected) {

    if (this.predicate != null
      && !this.predicate.isHighlighted(renderer, table, viewRow, viewColumn)) {
      return renderer;
    }

    final boolean even = viewRow % 2 == 0;
    if (isSelected) {
      renderer.setBackground(even ? this.backgroundSelected : this.backgroundSelectedOdd);
      renderer.setForeground(this.foregroundSelected);
    } else {
      renderer.setBackground(even ? this.background : this.backgroundOdd);
      renderer.setForeground(this.foreground);
    }
    return renderer;
  }

  public OddEvenColorHighlighter setForeground(final Color foreground) {
    this.foreground = foreground;
    return this;
  }

  public OddEvenColorHighlighter setForegroundSelected(final Color foregroundSelected) {
    this.foregroundSelected = foregroundSelected;
    return this;
  }

}
