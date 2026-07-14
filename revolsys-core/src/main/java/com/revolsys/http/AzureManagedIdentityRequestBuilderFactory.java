package com.revolsys.http;

import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import com.revolsys.net.http.apache5.ApacheHttp;
import com.revolsys.record.io.format.json.JsonObject;

public class AzureManagedIdentityRequestBuilderFactory
  extends BearerTokenRequestBuilderFactory<AzureManagedIdentityBearerToken> {
  public static final String ENDPOINT_URL = System.getenv("IDENTITY_ENDPOINT");

  public static final String IDENTITY_HEADER = System.getenv("IDENTITY_HEADER");

  private static boolean AVAILABLE = ENDPOINT_URL != null && IDENTITY_HEADER != null;

  public static boolean isAvailable() {
    return AVAILABLE;
  }

  private final String resource;

  public AzureManagedIdentityRequestBuilderFactory(final String resource) {
    this.resource = resource;
  }

  @Override
  protected AzureManagedIdentityBearerToken tokenRefresh(
    final AzureManagedIdentityBearerToken token) {
    if (isAvailable()) {
      final ClassicRequestBuilder requestBuilder = ClassicRequestBuilder//
        .get(ENDPOINT_URL)
        .addHeader("X-IDENTITY-HEADER", IDENTITY_HEADER)
        .addParameter("resource", this.resource)
        .addParameter("api-version", "2019-08-01");
      final JsonObject response = ApacheHttp.getJson(requestBuilder);
      return new AzureManagedIdentityBearerToken(response, this.resource);
    } else {
      return null;
    }
  }
}
