package com.revolsys.swing.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class CollapsiblePane extends JPanel {

  private static final long serialVersionUID = 1L;

  private final JPanel contentPanel;

  private final JButton toggleButton;

  private boolean collapsed = false;

  public CollapsiblePane() {
    this(null, new BorderLayout());
  }

  public CollapsiblePane(final String title) {
    this(title, new BorderLayout());
  }

  public CollapsiblePane(final String title, final LayoutManager contentLayout) {
    super(new BorderLayout());

    this.contentPanel = new JPanel(contentLayout);
    this.contentPanel.setOpaque(false);

    final String label = title != null ? title : "";
    this.toggleButton = new JButton("▼  " + label);
    this.toggleButton.putClientProperty("JButton.buttonType", "borderless");
    this.toggleButton.setHorizontalAlignment(JButton.LEFT);
    this.toggleButton.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
    this.toggleButton.addActionListener(e -> setCollapsed(!this.collapsed));

    setOpaque(false);
    add(this.toggleButton, BorderLayout.NORTH);
    add(this.contentPanel, BorderLayout.CENTER);
  }

  @Override
  public Component add(final Component comp) {
    if (this.contentPanel == null) {
      return super.add(comp);
    }
    return this.contentPanel.add(comp);
  }

  @Override
  public Component add(final String name, final Component comp) {
    if (this.contentPanel == null) {
      return super.add(name, comp);
    }
    return this.contentPanel.add(name, comp);
  }

  public JPanel getContentPanel() {
    return this.contentPanel;
  }

  // Delegate content methods to contentPanel

  public boolean isCollapsed() {
    return this.collapsed;
  }

  @Override
  public void remove(final Component comp) {
    this.contentPanel.remove(comp);
  }

  public void setAnimated(final boolean animated) {
    // no-op — retained for API compatibility if needed
  }

  public void setCollapsed(final boolean collapse) {
    this.collapsed = collapse;
    this.contentPanel.setVisible(!collapse);
    final String title = this.toggleButton.getText().substring(2).trim();
    this.toggleButton.setText((collapse ? "▶" : "▼") + "  " + title);
    revalidate();
    repaint();
  }

  @Override
  public void setLayout(final LayoutManager mgr) {
    if (this.contentPanel == null) {
      super.setLayout(mgr);
    } else {
      this.contentPanel.setLayout(mgr);
    }
  }
}
