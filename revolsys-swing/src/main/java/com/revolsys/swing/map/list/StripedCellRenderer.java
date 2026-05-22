package com.revolsys.swing.map.list;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class StripedCellRenderer extends DefaultListCellRenderer {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private Color color1 = Color.WHITE;

  private Color color2 = Color.LIGHT_GRAY;

  public StripedCellRenderer() {
  };

  public StripedCellRenderer(final Color color1, final Color color2) {
    this.color1 = color1;
    this.color2 = color2;
  };

  @Override
  public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index,
    final boolean isSelected, final boolean cellHasFocus) {
    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    if (!isSelected) {
      setBackground(index % 2 == 0 ? this.color1 : this.color2);
    }
    return this;
  }

}
