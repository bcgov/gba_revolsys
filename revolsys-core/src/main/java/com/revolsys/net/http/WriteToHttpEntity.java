package com.revolsys.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.AbstractHttpEntity;

public class WriteToHttpEntity extends AbstractHttpEntity {
  public static interface WriteTo {
    void writeTo(OutputStream out) throws IOException;
  }

  private long contentLength = -1;

  private final WriteTo action;

  public WriteToHttpEntity(final WriteTo action) {
    this.action = action;
  }

  @Override
  public InputStream getContent() throws IOException, UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getContentLength() {
    return this.contentLength;
  }

  @Override
  public boolean isRepeatable() {
    return false;
  }

  @Override
  public boolean isStreaming() {
    return false;
  }

  public WriteToHttpEntity setContentLength(final long contentLength) {
    this.contentLength = contentLength;
    return this;
  }

  @Override
  public void writeTo(final OutputStream outStream) throws IOException {
    this.action.writeTo(outStream);
  }

}
