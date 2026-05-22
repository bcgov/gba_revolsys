package com.revolsys.swing.field;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
   * Modified BasicComboBoxEditor to track the selected object but display custom text
   */
public class ModelComboBoxEditor<T> implements ComboBoxEditor, FocusListener {
  @SuppressWarnings("serial") // Superclass is not serializable across versions
  static class BorderlessTextField extends JTextField {
    public BorderlessTextField(final String value, final int n) {
      super(value, n);
    }

    @Override
    public void setBorder(final Border b) {
      if (!(b instanceof UIResource)) {
        super.setBorder(b);
      }
    }

    // workaround for 4530952
    @Override
    public void setText(final String s) {
      if (getText().equals(s)) {
        return;
      }
      super.setText(s);
    }
  }

  /**
   * A subclass of BasicComboBoxEditor that implements UIResource.
   * BasicComboBoxEditor doesn't implement UIResource
   * directly so that applications can safely override the
   * cellRenderer property with BasicListCellRenderer subclasses.
   * <p>
   * <strong>Warning:</strong>
   * Serialized objects of this class will not be compatible with
   * future Swing releases. The current serialization support is
   * appropriate for short term storage or RMI between applications running
   * the same version of Swing.  As of 1.4, support for long term storage
   * of all JavaBeans
   * has been added to the <code>java.beans</code> package.
   * Please see {@link java.beans.XMLEncoder}.
   */
  public static class UIResource extends BasicComboBoxEditor
    implements javax.swing.plaf.UIResource {
    /**
     * Constructs a {@code UIResource}.
     */
    public UIResource() {
    }
  }

  /**
   * An instance of {@code JTextField}.
   */
  protected JTextField editor;

  private Object currentSelection;

  private final FunctionStringConverter<T> converter;

  /**
   * Constructs a new instance of {@code BasicComboBoxEditor}.
   */
  public ModelComboBoxEditor(final FunctionStringConverter<T> converter) {
    this.editor = createEditorComponent();
    this.converter = converter;
  }

  @Override
  public void addActionListener(final ActionListener l) {
    this.editor.addActionListener(l);
  }

  /**
   * Creates the internal editor component. Override this to provide
   * a custom implementation.
   *
   * @return a new editor component
   * @since 1.6
   */
  protected JTextField createEditorComponent() {
    final JTextField editor = new BorderlessTextField("", 9);
    editor.setBorder(null);
    return editor;
  }

  // This used to do something but now it doesn't. It couldn't be
  // removed because it would be an API change to do so.
  @Override
  public void focusGained(final FocusEvent e) {
  }

  // This used to do something but now it doesn't. It couldn't be
  // removed because it would be an API change to do so.
  @Override
  public void focusLost(final FocusEvent e) {
  }

  @Override
  public Component getEditorComponent() {
    return this.editor;
  }

  @Override
  public Object getItem() {
    return this.currentSelection;

  }

  @Override
  public void removeActionListener(final ActionListener l) {
    this.editor.removeActionListener(l);
  }

  @Override
  public void selectAll() {
    this.editor.selectAll();
    this.editor.requestFocus();
  }

  /**
   * Sets the item that should be edited.
   *
   * @param anObject the displayed value of the editor
   */
  @Override
  public void setItem(final Object anObject) {
    this.currentSelection = anObject;

    String text = null;

    if (anObject != null) {
      text = this.converter.getPreferredStringForItem(anObject);
    }
    if (text == null) {
      text = "";
    }
    // workaround for 4530952
    if (!text.equals(this.editor.getText())) {
      this.editor.setText(text);
    }
  }
}
