package com.revolsys.swing.table.highlighter;

import java.awt.Component;

import javax.swing.JTable;

public interface SelectedHighlighter extends Highlighter {

  Component highlight(final Component renderer, JTable table, final int modelRow,
    final int modelColumn, final boolean isSelected);
}
