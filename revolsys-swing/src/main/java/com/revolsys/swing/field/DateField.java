package com.revolsys.swing.field;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.jdatepicker.ComponentIconDefaults;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.UtilDateModel;
import org.jeometry.common.data.type.DataType;
import org.jeometry.common.data.type.DataTypes;

public class DateField extends JPanel implements Field, PropertyChangeListener {

  private static final long serialVersionUID = 1L;

  private final FieldSupport fieldSupport;

  private final JDatePicker datePicker;

  private final UtilDateModel dateModel;

  public DateField() {
    this("fieldValue");
  }

  public DateField(final String fieldName) {
    this(fieldName, null);
  }

  public DateField(final String fieldName, final Object fieldValue) {
    super(new BorderLayout());

    ComponentIconDefaults.getInstance().setPopupButtonIcon(new Icon() {
      @Override
      public int getIconHeight() {
        return 10;
      }

      @Override
      public int getIconWidth() {
        return 10;
      }

      @Override
      public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        final Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c.getForeground());
        // Draw a simple downward triangle
        final int[] xPoints = {
          x, x + 8, x + 4
        };
        final int[] yPoints = {
          y + 2, y + 2, y + 8
        };
        g2.fillPolygon(xPoints, yPoints, 3);
        g2.dispose();
      }
    });

    this.dateModel = new UtilDateModel();
    this.datePicker = new JDatePicker(this.dateModel);
    setBackground(Color.WHITE);

    final JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setBorder(UIManager.getBorder("TextField.border"));
    wrapper.setBackground(Color.WHITE);

    // Remove individual borders
    ((JFormattedTextField)this.datePicker.getComponent(0))
      .setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
    ((JButton)this.datePicker.getComponent(1)).setBorder(UIManager.getBorder("Button.border"));

    this.datePicker.setBorder(null);
    wrapper.add(this.datePicker, BorderLayout.CENTER);

    add(wrapper, BorderLayout.CENTER);

    // add(this.datePicker, BorderLayout.CENTER);

    this.fieldSupport = new FieldSupport(this, fieldName, fieldValue, true);

    // Listen for date changes via PropertyChangeListener on the model
    this.dateModel.addPropertyChangeListener(evt -> {
      if ("value".equals(evt.getPropertyName())) {
        setFieldValue(evt.getNewValue());
      }
    });

    // Set initial value
    if (fieldValue != null) {
      setFieldValue(fieldValue);
    }
  }

  // ---------------------------------------------------------------------------
  // JXDatePicker API replacements
  // ---------------------------------------------------------------------------

  public void addActionListener(final ActionListener actionListener) {
    this.datePicker.addActionListener(actionListener);
  }

  @Override
  public DateField clone() {
    return new DateField(getFieldName(), getFieldValue());
  }

  @Override
  public void firePropertyChange(final String propertyName, final Object oldValue,
    final Object newValue) {
    super.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * Replaces JXDatePicker.getDate().
   */
  public Date getDate() {
    return this.dateModel.getValue();
  }

  /**
   * Replaces JXDatePicker.getEditor() — returns the text field from the picker.
   */
  public JTextField getEditor() {
    return (JTextField)this.datePicker.getComponent(0);
  }

  // ---------------------------------------------------------------------------
  // PropertyChangeListener
  // ---------------------------------------------------------------------------

  @Override
  public Color getFieldSelectedTextColor() {
    return getEditor().getSelectedTextColor();
  }

  // ---------------------------------------------------------------------------
  // Field interface
  // ---------------------------------------------------------------------------

  @Override
  public FieldSupport getFieldSupport() {
    return this.fieldSupport;
  }

  @Override
  public void propertyChange(final PropertyChangeEvent evt) {
    final String propertyName = evt.getPropertyName();
    if (propertyName.equals("date")) {
      setFieldValue(evt.getNewValue());
    }
  }

  public void removeActionListener(final ActionListener actionListener) {
    this.datePicker.removeActionListener(actionListener);
  }

  /**
   * Replaces JXDatePicker.setDate(Date).
   */
  public void setDate(final Date date) {
    if (!DataType.equal(getDate(), date)) {
      if (date == null) {
        this.dateModel.setValue(null);
      } else {
        final java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        this.dateModel.setDate(cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH),
          cal.get(java.util.Calendar.DAY_OF_MONTH));
        this.dateModel.setSelected(true);
      }
    }
  }

  @Override
  public void setFieldBackgroundColor(Color color) {
    if (color == null) {
      color = Field.DEFAULT_BACKGROUND;
    }
    getEditor().setBackground(color);
  }

  @Override
  public void setFieldForegroundColor(Color color) {
    if (color == null) {
      color = Field.DEFAULT_FOREGROUND;
    }
    getEditor().setForeground(color);
  }

  @Override
  public void setFieldSelectedTextColor(Color color) {
    if (color == null) {
      color = Field.DEFAULT_SELECTED_FOREGROUND;
    }
    getEditor().setSelectedTextColor(color);
  }

  @Override
  public void setFieldToolTip(final String toolTip) {
    getEditor().setToolTipText(toolTip);
  }

  @Override
  public boolean setFieldValue(final Object value) {
    final Date date = DataTypes.DATE_TIME.toObject(value);
    if (!DataType.equal(getDate(), date)) {
      setDate(date);
    }
    return this.fieldSupport.setValue(date);
  }

  @Override
  public void setToolTipText(final String text) {
    final FieldSupport fieldSupport = getFieldSupport();
    if (fieldSupport == null || fieldSupport.setOriginalTooltipText(text)) {
      super.setToolTipText(text);
    }
  }

  @Override
  public String toString() {
    return getFieldName() + "=" + getFieldValue();
  }

  @Override
  public void updateFieldValue() {
    // TODO setFieldValue(getText());
  }
}
