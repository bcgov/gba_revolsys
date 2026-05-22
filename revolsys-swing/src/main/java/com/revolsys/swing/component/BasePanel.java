package com.revolsys.swing.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import com.revolsys.swing.SwingUtil;
import com.revolsys.swing.VerticalLayout;

public class BasePanel extends JPanel implements Scrollable {
  // Add to BasePanel
  public enum ScrollableSizeHint {
    FIT, PREFERRED_STRETCH, NONE
  }

  private static final long serialVersionUID = 1L;

  public static BasePanel newPanelTitled(final String title) {
    final BasePanel panel = new BasePanel();
    final javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder(title);
    panel.setBorder(border);
    return panel;
  }

  public static BasePanel newPanelTitled(final String title, final Component... components) {
    final BasePanel panel = new BasePanel(components);
    final javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder(title);
    panel.setBorder(border);
    return panel;
  }

  public static BasePanel newPanelTitled(final String title, final LayoutManager layoutManager,
    final Component... components) {
    final BasePanel panel = new BasePanel(layoutManager, components);
    final javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder(title);
    panel.setBorder(border);
    return panel;
  }

  private ScrollableSizeHint scrollableHeightHint = ScrollableSizeHint.PREFERRED_STRETCH;

  private ScrollableSizeHint scrollableWidthHint = ScrollableSizeHint.FIT;

  public BasePanel() {
    this(true);
  }

  public BasePanel(final boolean isDoubleBuffered) {
    super(new VerticalLayout(), isDoubleBuffered);
    setOpaque(false);
  }

  public BasePanel(final Component... components) {
    this();
    for (final Component component : components) {
      add(component);
    }
  }

  public BasePanel(final LayoutManager layout) {
    this(layout, true);
  }

  public BasePanel(final LayoutManager layout, final boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
    setOpaque(false);
  }

  public BasePanel(final LayoutManager layout, final Component... components) {
    this(layout);
    for (final Component component : components) {
      add(component);
    }
  }

  public BasePanel addComponents(final Component... components) {
    for (final Component component : components) {
      add(component);
    }
    return this;
  }

  public BasePanel addComponents(final LayoutManager layout, final Component... components) {
    setLayout(layout);
    return addComponents(components);
  }

  public void addWithLabel(final String label, final Component component) {
    if (component != null) {
      SwingUtil.addLabel(this, label);
      add(component);
    }
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  @Override
  public int getScrollableBlockIncrement(final Rectangle visibleRect, final int orientation,
    final int direction) {
    return orientation == SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    switch (this.scrollableHeightHint) {
      case FIT:
        return true;
      case PREFERRED_STRETCH:
        return getParent() != null && getPreferredSize().height < getParent().getHeight();
      default:
        return false;
    }
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return this.scrollableWidthHint == ScrollableSizeHint.FIT;
  }

  @Override
  public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation,
    final int direction) {
    return 16;
  }

  public void setScrollableHeightHint(final ScrollableSizeHint hint) {
    this.scrollableHeightHint = hint;
  }

  public void setScrollableWidthHint(final ScrollableSizeHint hint) {
    this.scrollableWidthHint = hint;
  }
}
