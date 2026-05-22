package com.revolsys.swing.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import javax.swing.Icon;
import javax.swing.Timer;

public class BusyLabelPainter implements Icon {

  private static final Ellipse2D.Float[] SHAPES = {
    newShape(6, 0), newShape(10, 2), newShape(12, 6), newShape(10, 10), newShape(6, 12),
    newShape(2, 10), newShape(0, 6), newShape(2, 2)
  };

  private static final int POINT_COUNT = SHAPES.length;

  private static final int TRAIL_LENGTH = 4;

  private static Ellipse2D.Float newShape(final float x, final float y) {
    return new Ellipse2D.Float(x, y, 3, 3);
  }

  private final Color baseColor;

  private final Color highlightColor;

  private final Color[] trailColors;

  private int frame = 0;

  private Timer timer;

  private Component owner;

  public BusyLabelPainter() {
    this.baseColor = new Color(200, 200, 200);
    this.highlightColor = new Color(50, 50, 50);
    this.trailColors = new Color[TRAIL_LENGTH];
    for (int t = 0; t < TRAIL_LENGTH; t++) {
      final float terp = 1 - (float)(TRAIL_LENGTH - t) / (float)TRAIL_LENGTH;
      this.trailColors[t] = interpolate(this.baseColor, this.highlightColor, terp);
    }
  }

  private Color calcFrameColor(final int i) {
    for (int t = 0; t < TRAIL_LENGTH; t++) {
      if (i == (this.frame - t + POINT_COUNT) % POINT_COUNT) {
        return this.trailColors[t];
      }
    }
    return this.baseColor;
  }

  @Override
  public int getIconHeight() {
    return 16;
  }

  @Override
  public int getIconWidth() {
    return 16;
  }

  private Color interpolate(final Color c1, final Color c2, final float t) {
    final int r = (int)(c1.getRed() + t * (c2.getRed() - c1.getRed()));
    final int g = (int)(c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
    final int b = (int)(c1.getBlue() + t * (c2.getBlue() - c1.getBlue()));
    return new Color(r, g, b);
  }

  @Override
  public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
    final Graphics2D g2 = (Graphics2D)g.create();
    try {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.translate(x, y);
      for (int i = 0; i < SHAPES.length; i++) {
        g2.setColor(calcFrameColor(i));
        g2.fill(SHAPES[i]);
      }
    } finally {
      g2.dispose();
    }
  }

  public void start(final Component component) {
    this.owner = component;
    if (this.timer == null) {
      this.timer = new Timer(100, e -> {
        this.frame = (this.frame + 1) % POINT_COUNT;
        if (this.owner != null) {
          this.owner.repaint();
        }
      });
    }
    this.timer.start();
  }

  public void stop() {
    if (this.timer != null) {
      this.timer.stop();
    }
    this.frame = 0;
  }
}
