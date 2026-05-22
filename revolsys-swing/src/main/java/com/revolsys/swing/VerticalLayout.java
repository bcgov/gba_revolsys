package com.revolsys.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * Replaces org.jdesktop.swingx.VerticalLayout.
 * Lays out components vertically, stretching them to fill the container width,
 * with an optional gap between components.
 */
public class VerticalLayout implements LayoutManager {

  private final int gap;

  public VerticalLayout() {
    this(0);
  }

  public VerticalLayout(final int gap) {
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
      final int x = insets.left;
      final int width = parent.getWidth() - insets.left - insets.right;
      int y = insets.top;
      boolean firstVisible = true;

      for (final Component comp : parent.getComponents()) {
        if (comp.isVisible()) {
          if (!firstVisible) {
            y += this.gap;
          }
          final int height = comp.getPreferredSize().height;
          // Stretch component to fill full container width — matches SwingX
          // behaviour
          comp.setBounds(x, y, width, height);
          y += height;
          firstVisible = false;
        }
      }
    }
  }

  @Override
  public Dimension minimumLayoutSize(final Container parent) {
    synchronized (parent.getTreeLock()) {
      final Insets insets = parent.getInsets();
      int width = 0;
      int height = insets.top + insets.bottom;
      int visibleCount = 0;

      for (final Component comp : parent.getComponents()) {
        if (comp.isVisible()) {
          final Dimension min = comp.getMinimumSize();
          width = Math.max(width, min.width);
          height += min.height;
          visibleCount++;
        }
      }

      if (visibleCount > 1) {
        height += this.gap * (visibleCount - 1);
      }

      return new Dimension(width + insets.left + insets.right, height);
    }
  }

  @Override
  public Dimension preferredLayoutSize(final Container parent) {
    synchronized (parent.getTreeLock()) {
      final Insets insets = parent.getInsets();
      int width = 0;
      int height = insets.top + insets.bottom;
      int visibleCount = 0;

      for (final Component comp : parent.getComponents()) {
        if (comp.isVisible()) {
          final Dimension pref = comp.getPreferredSize();
          width = Math.max(width, pref.width);
          height += pref.height;
          visibleCount++;
        }
      }

      if (visibleCount > 1) {
        height += this.gap * (visibleCount - 1);
      }

      return new Dimension(width + insets.left + insets.right, height);
    }
  }

  @Override
  public void removeLayoutComponent(final Component comp) {
    // no-op
  }
}
