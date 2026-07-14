package com.revolsys.raster.commonsimaging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.imaging.AbstractImageParser;
import org.apache.commons.imaging.bytesource.ByteSource;
import org.jeometry.common.exception.Exceptions;

import com.revolsys.io.AbstractIoFactory;
import com.revolsys.raster.BufferedImageReadFactory;
import com.revolsys.raster.GeoreferencedImage;
import com.revolsys.raster.GeoreferencedImageReadFactory;
import com.revolsys.spring.resource.Resource;

public class CommonsImagingImageReadFactory extends AbstractIoFactory
  implements GeoreferencedImageReadFactory, BufferedImageReadFactory {

  private final AbstractImageParser<?> imageParser;

  public final String worldFileExtension;

  public CommonsImagingImageReadFactory(final AbstractImageParser<?> imageParser) {
    this(imageParser, imageParser.getName(), null, null);
  }

  public CommonsImagingImageReadFactory(final AbstractImageParser<?> imageParser, final String name,
    final String mimeType, final String worldFileExtension) {
    super(name);
    this.imageParser = imageParser;
    this.worldFileExtension = worldFileExtension;
    final String defaultExtension = imageParser.getDefaultExtension().substring(1);
    if (mimeType == null) {
      addFileExtension(defaultExtension);
    } else {
      addMediaTypeAndFileExtension(mimeType, defaultExtension);
    }
  }

  @Override
  public boolean isReadFromZipFileSupported() {
    return true;
  }

  @Override
  public BufferedImage readBufferedImage(final Resource resource) {
    try {
      ByteSource byteSource;
      if (resource.isFile()) {
        final File file = resource.getFile();
        byteSource = ByteSource.file(file);
      } else {
        final InputStream in = resource.getInputStream();
        final String filename = resource.getFilename();
        byteSource = ByteSource.inputStream(in, filename);
      }
      return this.imageParser.getBufferedImage(byteSource, null);
    } catch (final IOException e) {
      throw Exceptions.wrap("Unable to open: " + resource, e);
    }
  }

  @Override
  public GeoreferencedImage readGeoreferencedImage(final Resource resource) {
    return new CommonsImagingGeoreferencedImage(this, resource, this.worldFileExtension);
  }

}
