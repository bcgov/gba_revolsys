package com.revolsys.swing.table.highlighter;

import java.awt.Component;

import javax.swing.JTable;

/**
 * Functional interface replacing SwingX HighlightPredicate.
 * Tests whether a cell at (viewRow, viewColumn) should be highlighted.
 */
@FunctionalInterface
public interface HighlightPredicate {
  HighlightPredicate ODD = (r, t, row, col) -> row % 2 != 0;

  HighlightPredicate EVEN = (r, t, row, col) -> row % 2 == 0;

  static HighlightPredicate and(final HighlightPredicate... predicates) {
    return (renderer, table, viewRow, viewColumn) -> {
      for (final HighlightPredicate p : predicates) {
        if (!p.isHighlighted(renderer, table, viewRow, viewColumn)) {
          return false;
        }
      }
      return true;
    };
  }

  boolean isHighlighted(final Component renderer, JTable table, int modelRow, int modelColumn);
}
