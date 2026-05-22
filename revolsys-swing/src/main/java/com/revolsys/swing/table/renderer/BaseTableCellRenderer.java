package com.revolsys.swing.table.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.jeometry.common.data.type.DataTypes;

public class BaseTableCellRenderer extends DefaultTableCellRenderer {

  private static final long serialVersionUID = 1L;

  public BaseTableCellRenderer() {
    super();
  }

  @Override
  public Component getTableCellRendererComponent(final JTable table, final Object value,
    final boolean isSelected, final boolean hasFocus, final int row, final int columnIndex) {
    final String text = DataTypes.toString(value);
    final Component component = super.getTableCellRendererComponent(table, text, isSelected,
      hasFocus, row, columnIndex);
    if (Number.class.isAssignableFrom(table.getModel().getColumnClass(columnIndex))) {
      if (component instanceof JLabel) {
        final JLabel label = (JLabel)component;
        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
        label.setHorizontalTextPosition(SwingConstants.RIGHT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
      }
    }
    return component;
  }
}
