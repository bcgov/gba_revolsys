package com.revolsys.gis.elevation.gridded;

import java.util.Collections;
import java.util.Map;

import com.revolsys.io.IoFactory;
import com.revolsys.io.Writer;
import com.revolsys.spring.resource.Resource;

public interface GriddedElevationModelWriter extends Writer<GriddedElevationModel> {
  static boolean isWritable(final Object source) {
    return IoFactory.isAvailable(GriddedElevationModelWriterFactory.class, source);
  }

  static GriddedElevationModelWriter newGriddedElevationModelWriter(final Object target) {
    final Map<String, ? extends Object> properties = Collections.emptyMap();
    return newGriddedElevationModelWriter(target, properties);
  }

  static GriddedElevationModelWriter newGriddedElevationModelWriter(final Object target,
    final Map<String, ? extends Object> properties) {
    final GriddedElevationModelWriterFactory factory = IoFactory
      .factory(GriddedElevationModelWriterFactory.class, target);
    if (factory == null) {
      return null;
    } else {
      final Resource resource = Resource.getResource(target);
      final GriddedElevationModelWriter writer = factory.newGriddedElevationModelWriter(resource);
      writer.setProperties(properties);
      return writer;
    }
  }
}