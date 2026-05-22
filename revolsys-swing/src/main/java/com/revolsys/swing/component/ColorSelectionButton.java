package com.revolsys.swing.component;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;

public class ColorSelectionButton extends JButton {

  private static final long serialVersionUID = 1L;

  private Color selectedColor;

  private JColorChooser chooser;

  private JDialog dialog = null;

  public ColorSelectionButton() {
    this(Color.WHITE);
  }

  public ColorSelectionButton(final Color initialColor) {
    setSelectedColor(initialColor);
    setPreferredSize(new Dimension(24, 24));
    setOpaque(true);
    setBorderPainted(true);
    addActionListener(_ -> showDialog());
  }

  /**
   * Get the JColorChooser that is used by this JXColorSelectionButton. This
   * chooser instance is shared between all invocations of the chooser, but is unique to
   * this instance of JXColorSelectionButton.
   * @return the JColorChooser used by this JXColorSelectionButton
   */
  public JColorChooser getChooser() {
    if (this.chooser == null) {
      this.chooser = new JColorChooser();
      this.chooser.addChooserPanel(new ColorAlphaPanel());
    }
    return this.chooser;
  }

  public Color getSelectedColor() {
    return this.selectedColor;
  }

  private boolean isColorDark(final Color color) {
    // Perceived luminance formula
    return 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue() < 128;
  }

  public void setSelectedColor(final Color color) {
    final Color oldColor = this.selectedColor;
    this.selectedColor = color;
    setBackground(color);
    // Make icon text contrast against the chosen color
    setForeground(isColorDark(color) ? Color.WHITE : Color.BLACK);
    firePropertyChange("selectedColor", oldColor, color);
    repaint();
  }

  /**
   * Conditionally create and show the color chooser dialog.
   */
  private void showDialog() {
    if (this.dialog == null) {
      this.dialog = JColorChooser.createDialog(this, "Choose a color", true, getChooser(),
        // ok
        _ -> {
          final Color color = getChooser().getColor();
          if (color != null) {
            ColorSelectionButton.this.setSelectedColor(color);
          }
        },
        // cancel
        _ -> {
        });

      this.dialog.getContentPane().add(getChooser());
    }

    this.dialog.setVisible(true);

  }
}
