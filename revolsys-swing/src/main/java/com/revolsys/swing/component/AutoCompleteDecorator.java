package com.revolsys.swing.component;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import com.revolsys.swing.field.ComboBox;
import com.revolsys.swing.field.FunctionStringConverter;

public class AutoCompleteDecorator {

  public static void decorate(final ComboBox<?> comboBox,
    final FunctionStringConverter<?> stringConverter) {

    final JTextField editor = (JTextField)comboBox.getEditor().getEditorComponent();
    editor.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(final KeyEvent e) {
        final int keyCode = e.getKeyCode();
        // Ignore navigation/control keys
        if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_ESCAPE
          || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT
          || keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_BACK_SPACE
          || keyCode == KeyEvent.VK_DELETE) {
          return;
        }
        final String typed = editor.getText();
        if (typed.isEmpty()) {
          comboBox.hidePopup();
          return;
        }
        // Find first match
        for (int i = 0; i < comboBox.getModel().getSize(); i++) {
          final Object item = comboBox.getModel().getElementAt(i);
          final String str = stringConverter.getPreferredStringForItem(item);
          if (str != null && str.toLowerCase().startsWith(typed.toLowerCase())) {
            comboBox.setSelectedIndex(i);
            editor.setText(typed);
            // Select the auto-completed suffix
            editor.setSelectionStart(typed.length());
            editor.setSelectionEnd(str.length());
            comboBox.showPopup();
            return;
          }
        }
        comboBox.hidePopup();
      }
    });
    comboBox.setEditable(true);

  }
}
