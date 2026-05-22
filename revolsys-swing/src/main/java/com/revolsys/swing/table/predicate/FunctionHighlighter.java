package com.revolsys.swing.table.predicate;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import org.apache.commons.lang3.function.TriFunction;
import org.jeometry.common.logging.Logs;

import com.revolsys.swing.table.highlighter.ColorHighlighter;
import com.revolsys.swing.table.highlighter.HighlightPredicate;
import com.revolsys.swing.table.highlighter.Highlighter;
import com.revolsys.swing.table.highlighter.OutsideBorderHighlighter;

public class FunctionHighlighter implements HighlightPredicate {

  public static Highlighter color(final TriFunction<JTable, Integer, Integer, Boolean> function,
    final Color cellBackground, final Color cellForeground, final Color selectedBackground,
    final Color selectedForeground) {
    final HighlightPredicate predicate = new FunctionHighlighter(function);
    return new ColorHighlighter(predicate, cellBackground, cellForeground, selectedBackground,
      selectedForeground);
  }

  public static Highlighter outsideBorder(final JTable table,
    final TriFunction<JTable, Integer, Integer, Boolean> function, final Color color,
    final int thickness) {

    final HighlightPredicate predicate = new FunctionHighlighter(function);
    return new OutsideBorderHighlighter(predicate, table, color, thickness, true, false);
  }

  private final TriFunction<JTable, Integer, Integer, Boolean> function;

  public FunctionHighlighter(final TriFunction<JTable, Integer, Integer, Boolean> function) {
    this.function = function;
  }

  @Override
  public boolean isHighlighted(final Component renderer, final JTable table, final int viewRow,
    final int viewColumn) {

    try {
      return this.function.apply(table, viewRow, viewColumn);
    } catch (final Throwable e) {
      Logs.debug(this, "Error in highlighter", e);
      return false;
    }
  }
}
