/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package com.revolsys.jts.triangulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.revolsys.jts.geom.Geometry;
import com.revolsys.jts.geom.GeometryCollection;
import com.revolsys.jts.geom.GeometryFactory;
import com.revolsys.jts.geom.LineString;
import com.revolsys.jts.geom.MultiLineString;
import com.revolsys.jts.geom.Point;
import com.revolsys.jts.geom.Polygon;
import com.revolsys.jts.geom.impl.BoundingBoxDoubleGf;
import com.revolsys.jts.triangulate.quadedge.QuadEdgeSubdivision;
import com.revolsys.jts.triangulate.quadedge.Vertex;

/**
 * A utility class which creates Conforming Delaunay Trianglulations
 * from collections of points and linear constraints, and extract the resulting
 * triangulation edges or triangles as geometries.
 *
 * @author Martin Davis
 *
 */
public class ConformingDelaunayTriangulationBuilder {
  private static List createConstraintSegments(final Geometry geom) {
    final List lines = geom.getGeometryComponents(LineString.class);
    final List constraintSegs = new ArrayList();
    for (final Iterator i = lines.iterator(); i.hasNext();) {
      final LineString line = (LineString)i.next();
      createConstraintSegments(line, constraintSegs);
    }
    return constraintSegs;
  }

  private static void createConstraintSegments(final LineString line,
    final List<Segment> constraintSegs) {
    for (final com.revolsys.jts.geom.segment.Segment segment : line.segments()) {
      constraintSegs.add(new Segment(segment.getPoint(0).clonePoint(),
        segment.getPoint(1).clonePoint()));
    }
  }

  private Collection siteCoords;

  private Geometry constraintLines;

  private double tolerance = 0.0;

  private QuadEdgeSubdivision subdiv = null;

  private final Map constraintVertexMap = new TreeMap();

  public ConformingDelaunayTriangulationBuilder() {
  }

  private void create() {
    if (this.subdiv != null) {
      return;
    }

    final BoundingBoxDoubleGf siteEnv = DelaunayTriangulationBuilder.envelope(this.siteCoords);

    List segments = new ArrayList();
    if (this.constraintLines != null) {
      siteEnv.expandToInclude(this.constraintLines.getBoundingBox());
      createVertices(this.constraintLines);
      segments = createConstraintSegments(this.constraintLines);
    }
    final List sites = createSiteVertices(this.siteCoords);

    final ConformingDelaunayTriangulator cdt = new ConformingDelaunayTriangulator(
      sites, this.tolerance);

    cdt.setConstraints(segments, new ArrayList(this.constraintVertexMap.values()));

    cdt.formInitialDelaunay();
    cdt.enforceConstraints();
    this.subdiv = cdt.getSubdivision();
  }

  private List<ConstraintVertex> createSiteVertices(
    final Collection<Point> coords) {
    final List<ConstraintVertex> verts = new ArrayList<>();
    for (final Point coord : coords) {
      if (this.constraintVertexMap.containsKey(coord)) {
        continue;
      }
      verts.add(new ConstraintVertex(coord));
    }
    return verts;
  }

  private void createVertices(final Geometry geom) {
    for (final Point coordinate : geom.vertices()) {
      final Vertex v = new ConstraintVertex(coordinate);
      this.constraintVertexMap.put(v.getCoordinate(), v);
    }
  }

  /**
   * Gets the edges of the computed triangulation as a {@link MultiLineString}.
   *
   * @param geomFact the geometry factory to use to create the output
   * @return the edges of the triangulation
   */
  public Geometry getEdges(final GeometryFactory geomFact) {
    create();
    return this.subdiv.getEdges(geomFact);
  }

  /**
   * Gets the QuadEdgeSubdivision which models the computed triangulation.
   *
   * @return the subdivision containing the triangulation
   */
  public QuadEdgeSubdivision getSubdivision() {
    create();
    return this.subdiv;
  }

  /**
   * Gets the faces of the computed triangulation as a {@link GeometryCollection}
   * of {@link Polygon}.
   *
   * @param geomFact the geometry factory to use to create the output
   * @return the faces of the triangulation
   */
  public Geometry getTriangles(final GeometryFactory geomFact) {
    create();
    return this.subdiv.getTriangles(geomFact);
  }

  /**
   * Sets the linear constraints to be conformed to.
   * All linear components in the input will be used as constraints.
   * The constraint vertices do not have to be disjoint from
   * the site vertices.
   * The constraints must not contain duplicate segments (up to orientation).
   *
   * @param constraintLines the lines to constraint to
   */
  public void setConstraints(final Geometry constraintLines) {
    this.constraintLines = constraintLines;
  }

  /**
   * Sets the sites (point or vertices) which will be triangulated.
   * All vertices of the given geometry will be used as sites.
   * The site vertices do not have to contain the constraint
   * vertices as well; any site vertices which are
   * identical to a constraint vertex will be removed
   * from the site vertex set.
   *
   * @param geom the geometry from which the sites will be extracted.
   */
  public void setSites(final Geometry geom) {
    this.siteCoords = DelaunayTriangulationBuilder.extractUniqueCoordinates(geom);
  }

  /**
   * Sets the snapping tolerance which will be used
   * to improved the robustness of the triangulation computation.
   * A tolerance of 0.0 specifies that no snapping will take place.
   *
   * @param tolerance the tolerance distance to use
   */
  public void setTolerance(final double tolerance) {
    this.tolerance = tolerance;
  }

}