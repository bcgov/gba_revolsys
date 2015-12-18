package com.revolsys.geometry.test.old.perf.operation.distance;

import java.util.List;

import com.revolsys.geometry.densify.Densifier;
import com.revolsys.geometry.model.BoundingBox;
import com.revolsys.geometry.model.Geometry;
import com.revolsys.geometry.model.GeometryFactory;
import com.revolsys.geometry.model.Point;
import com.revolsys.geometry.model.Polygon;
import com.revolsys.geometry.model.impl.PointDouble;
import com.revolsys.geometry.operation.distance.IndexedFacetDistance;
import com.revolsys.geometry.test.old.algorithm.InteriorPointTest;
import com.revolsys.geometry.util.Stopwatch;
import com.revolsys.geometry.wkb.WKTFileReader;
import com.revolsys.geometry.wkb.WKTReader;

/**
 * Tests performance of {@link IndexedFacetDistance} versus standard
 * using a grid of points to a target set of lines
 *
 * @author Martin Davis
 *
 */
public class TestPerfDistanceLinesPoints {
  static final double EXTENT = 1000;

  static GeometryFactory geomFact = GeometryFactory.floating3();

  static final int MAX_ITER = 1;

  static final int NUM_PTS_SIDE = 100;

  static final int NUM_TARGET_ITEMS = 4000;

  static final boolean USE_INDEXED_DIST = true;

  public static void main(final String[] args) {
    final TestPerfDistanceLinesPoints test = new TestPerfDistanceLinesPoints();
    try {
      test.test();
    } catch (final Exception ex) {
      ex.printStackTrace();
    }
  }

  boolean verbose = true;

  public TestPerfDistanceLinesPoints() {
  }

  void computeDistance(final Geometry[] pts, final Geometry geom) {
    IndexedFacetDistance bbd = null;
    if (USE_INDEXED_DIST) {
      bbd = new IndexedFacetDistance(geom);
    }
    for (final Geometry pt : pts) {
      if (USE_INDEXED_DIST) {
        final double dist = bbd.getDistance(pt);
        // double dist = bbd.getDistanceWithin(pts[i].getCoordinate(), 100000);
      } else {
        final double dist = geom.distance(pt);
      }
    }
  }

  Geometry loadData(final String file) throws Exception {
    final List geoms = InteriorPointTest.getTestGeometries(file);
    return geomFact.buildGeometry(geoms);
  }

  List loadWKT(final String filename) throws Exception {
    final WKTReader rdr = new WKTReader();
    final WKTFileReader fileRdr = new WKTFileReader(filename, rdr);
    return fileRdr.read();
  }

  Geometry newDiagonalCircles(final double extent, final int nSegs) {
    final Polygon[] circles = new Polygon[nSegs];
    final double inc = extent / nSegs;
    for (int i = 0; i < nSegs; i++) {
      final double ord = i * inc;
      final Point p = new PointDouble(ord, ord, Geometry.NULL_ORDINATE);
      final Geometry pt = geomFact.point(p);
      circles[i] = (Polygon)pt.buffer(inc / 2);
    }
    return geomFact.multiPolygon(circles);

  }

  Geometry newDiagonalLine(final double extent, final int nSegs) {
    final Point[] pts = new Point[nSegs + 1];
    pts[0] = new PointDouble((double)0, 0, Geometry.NULL_ORDINATE);
    final double inc = extent / nSegs;
    for (int i = 1; i <= nSegs; i++) {
      final double ord = i * inc;
      pts[i] = new PointDouble(ord, ord, Geometry.NULL_ORDINATE);
    }
    return geomFact.lineString(pts);
  }

  Geometry newLine(final double extent, final int nSegs) {
    final Point[] pts = new Point[] {
      new PointDouble((double)0, 0, Geometry.NULL_ORDINATE),
      new PointDouble((double)0, extent, Geometry.NULL_ORDINATE),
      new PointDouble(extent, extent, Geometry.NULL_ORDINATE),
      new PointDouble(extent, 0, Geometry.NULL_ORDINATE)

    };
    final Geometry outline = geomFact.lineString(pts);
    final double inc = extent / nSegs;
    return Densifier.densify(outline, inc);

  }

  Geometry[] newPoints(final BoundingBox extent, final int nPtsSide) {
    final Geometry[] pts = new Geometry[nPtsSide * nPtsSide];
    int index = 0;
    final double xinc = extent.getWidth() / nPtsSide;
    final double yinc = extent.getHeight() / nPtsSide;
    for (int i = 0; i < nPtsSide; i++) {
      for (int j = 0; j < nPtsSide; j++) {
        pts[index++] = geomFact.point(new PointDouble(extent.getMinX() + i * xinc,
          extent.getMinY() + j * yinc, Geometry.NULL_ORDINATE));
      }
    }
    return pts;
  }

  public void test() throws Exception {

    // test(200);
    // if (true) return;

    // test(5000);
    // test(8001);

    // test(50);
    test(100);
    test(200);
    test(500);
    test(1000);
    // test(5000);
    // test(10000);
    // test(50000);
    // test(100000);
  }

  public void test(final Geometry[] pts, final Geometry target) {
    if (this.verbose) {
      // System.out.println("Query points = " + pts.length
      // + " Target points = " + target.getVertexCount());
      // if (! verbose) System.out.print(num + ", ");
    }

    final Stopwatch sw = new Stopwatch();
    final double dist = 0.0;
    for (int i = 0; i < MAX_ITER; i++) {
      computeDistance(pts, target);
    }
    if (!this.verbose) {
      // System.out.println(sw.getTimeString());
    }
    if (this.verbose) {
      final String name = USE_INDEXED_DIST ? "IndexedFacetDistance" : "Distance";
      // System.out.println(name + " - Run time: " + sw.getTimeString());
      // System.out.println(" (Distance = " + dist + ")\n");
      // System.out.println();
    }
  }

  public void test(final int num) throws Exception {
    final Geometry target = newDiagonalCircles(EXTENT, NUM_TARGET_ITEMS);
    final Geometry[] pts = newPoints(target.getBoundingBox(), num);

    test(pts, target);
  }

}