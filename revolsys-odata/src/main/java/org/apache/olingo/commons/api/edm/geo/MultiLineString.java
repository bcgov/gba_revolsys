/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.api.edm.geo;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

/**
 * Represents a MuliLineString.
 * Either Edm.GeometryMultiLineString or Edm.GeographyMultiLineString
 */
public class MultiLineString extends ComposedGeospatial<LineString> {

  /**
   * Creates a new MultiLineString
   *
   * @param dimension     Dimension of the MultiLineString
   * @param srid          SRID value
   * @param lineStrings   List of lineStrings
   */
  public MultiLineString(final Dimension dimension, final SRID srid,
    final List<LineString> lineStrings) {
    super(dimension, Type.MULTILINESTRING, srid, lineStrings);
  }

  @Override
  public EdmPrimitiveTypeKind getEdmPrimitiveTypeKind() {
    return this.dimension == Dimension.GEOGRAPHY ? EdmPrimitiveTypeKind.GeographyMultiLineString
      : EdmPrimitiveTypeKind.GeometryMultiLineString;
  }

}
