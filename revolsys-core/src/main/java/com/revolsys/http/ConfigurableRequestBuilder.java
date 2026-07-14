package com.revolsys.http;

import org.apache.hc.client5.http.classic.methods.HttpUriRequest;

public class ConfigurableRequestBuilder extends ApacheHttpRequestBuilder {

  public ConfigurableRequestBuilder(final ConfigurableRequestBuilderFactory factory) {
    super(factory);
  }

  @Override
  public HttpUriRequest build() {
    getFactory().preBuild(this);
    return super.build();
  }

  @Override
  public ConfigurableRequestBuilderFactory getFactory() {
    return (ConfigurableRequestBuilderFactory)super.getFactory();
  }

}
