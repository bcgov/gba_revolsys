package com.revolsys.swing.table.highlighter;

import java.awt.Component;

import javax.swing.JTable;

public interface Highlighter {

  Component highlight(Component renderer, JTable table, int modelRow, int modelColumn);

}
