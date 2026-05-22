package com.revolsys.swing.table.lambda;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class LambdaCellRenderer<V> extends DefaultTableCellRenderer {

  private static final long serialVersionUID = 1L;

  private final LambdaStringValue<V> renderFunction;

  public LambdaCellRenderer(final LambdaStringValue<V> renderFunction) {
    super();
    this.renderFunction = renderFunction;
  }

  public LambdaCellRenderer(final LambdaStringValue<V> renderFunction, final int alignment) {
    super();
    setHorizontalAlignment(alignment);
    this.renderFunction = renderFunction;
  }

  public LambdaStringValue<V> getRenderFunction() {
    return this.renderFunction;
  }

  @Override
  public Component getTableCellRendererComponent(final JTable table, final Object value,
    final boolean isSelected, final boolean hasFocus, final int row, final int column) {
    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void setValue(final Object value) {
    if (this.renderFunction != null && value != null) {
      setText(this.renderFunction.getString(value));
    } else {
      setText(value == null ? "" : value.toString());
    }
  }
}
