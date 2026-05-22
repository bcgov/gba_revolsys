package com.revolsys.swing.field;

import java.awt.Color;

import javax.swing.BorderFactory;

import com.revolsys.swing.EventQueue;
import com.revolsys.swing.component.ColorSelectionButton;
import com.revolsys.swing.component.ValueField;

public class ColorChooserField extends ValueField {
  private static final long serialVersionUID = 1L;

  private final ColorSelectionButton colorButton = new ColorSelectionButton();

  public ColorChooserField(final String fieldName, final Color color) {
    super(fieldName, color);
    EventQueue.addPropertyChange(this.colorButton, "background", () -> updateFieldValue());
    setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 0));
    add(this.colorButton);
    setFieldValue(color);
  }

  @Override
  public boolean setFieldValue(final Object color) {
    if (this.colorButton != null) {
      this.colorButton.setBackground((Color)color);
    }
    return super.setFieldValue(color);
  }

  @Override
  public void updateFieldValue() {
    setFieldValue(this.colorButton.getBackground());
  }
}
