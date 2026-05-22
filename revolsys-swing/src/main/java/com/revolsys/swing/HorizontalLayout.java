package com.revolsys.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * Mimics SwingX HorizontalLayout.
 * Lays out components horizontally, stretching them to fill the container height,
 * with an optional gap between components.
 */
public class HorizontalLayout implements LayoutManager {

  private final int gap;

  public HorizontalLayout() {
    this(0);
  }

  public HorizontalLayout(final int gap) {
    this.gap = gap;
  }

  @Override
  public void addLayoutComponent(final String name, final Component comp) {
    // no-op
  }

  public int getGap() {
    return this.gap;
  }

  @Override
  public void layoutContainer(final Container parent) {
    synchronized (parent.getTreeLock()) {
      final Insets insets = parent.getInsets();
      final int y = insets.top;
      final int height = parent.getHeight() - insets.top - insets.bottom;
      int x = insets.left;
      boolean firstVisible = true;

      for (final Component comp : parent.getComponents()) {
        if (comp.isVisible()) {
          if (!firstVisible) {
            x += this.gap;
          }
          final int width = comp.getPreferredSize().width;
          // Stretch component to fill full container height — mirrors
          // VerticalLayout behaviour
          comp.setBounds(x, y, width, height);
          x += width;
          firstVisible = false;
        }
      }
    }
  }

  @Override
  public Dimension minimumLayoutSize(final Container parent) {
    synchronized (parent.getTreeLock()) {
      final Insets insets = parent.getInsets();
      int width = insets.left + insets.right;
      int height = 0;
      int visibleCount = 0;

      for (final Component comp : parent.getComponents()) {
        if (comp.isVisible()) {
          final Dimension min = comp.getMinimumSize();
          width += min.width;
          height = Math.max(height, min.height);
          visibleCount++;
        }
      }

      if (visibleCount > 1) {
        width += this.gap * (visibleCount - 1);
      }

      return new Dimension(width, height + insets.top + insets.bottom);
    }
  }

  @Override
  public Dimension preferredLayoutSize(final Container parent) {
    synchronized (parent.getTreeLock()) {
      final Insets insets = parent.getInsets();
      int width = insets.left + insets.right;
      int height = 0;
      int visibleCount = 0;

      for (final Component comp : parent.getComponents()) {
        if (comp.isVisible()) {
          final Dimension pref = comp.getPreferredSize();
          width += pref.width;
          height = Math.max(height, pref.height);
          visibleCount++;
        }
      }

      if (visibleCount > 1) {
        width += this.gap * (visibleCount - 1);
      }

      return new Dimension(width, height + insets.top + insets.bottom);
    }
  }

  @Override
  public void removeLayoutComponent(final Component comp) {
    // no-op
  }
}
