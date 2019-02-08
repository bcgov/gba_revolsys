package com.revolsys.elevation.cloud.las.zip;

import com.revolsys.elevation.cloud.las.pointformat.LasPoint;
import com.revolsys.math.arithmeticcoding.ArithmeticCodingDecompressDecoder;
import com.revolsys.math.arithmeticcoding.ArithmeticCodingDecompressInteger;
import com.revolsys.math.arithmeticcoding.ArithmeticCodingDecompressModel;

public abstract class LazDecompressGpsTime11 implements LazDecompress {

  protected ArithmeticCodingDecompressDecoder decoder;

  protected ArithmeticCodingDecompressModel gpsTimeMulti;

  protected ArithmeticCodingDecompressModel gpsTime0Diff;

  protected ArithmeticCodingDecompressInteger decompressGpsTime;

  public LazDecompressGpsTime11(final ArithmeticCodingDecompressDecoder decoder) {
    this.decoder = decoder;
  }

  @Override
  public void init(final LasPoint firstPoint) {
    final ArithmeticCodingDecompressModel m = this.gpsTimeMulti;
    ArithmeticCodingDecompressDecoder r = this.decoder;
    m.reset();
    final ArithmeticCodingDecompressModel m1 = this.gpsTime0Diff;
    ArithmeticCodingDecompressDecoder r1 = this.decoder;
    m1.reset();
    this.decompressGpsTime.reset();
  }
}