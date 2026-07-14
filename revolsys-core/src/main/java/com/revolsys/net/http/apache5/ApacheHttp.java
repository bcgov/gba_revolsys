package com.revolsys.net.http.apache5;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.jeometry.common.exception.Exceptions;
import org.jeometry.common.exception.WrappedException;

import com.revolsys.io.FileUtil;
import com.revolsys.record.io.format.json.JsonObject;
import com.revolsys.record.io.format.json.JsonParser;

public class ApacheHttp {

  public static final ContentType XML = ContentType.create("application/xml",
    StandardCharsets.UTF_8);

  private static TrustStrategy defaultTrustStrategy = TrustSelfSignedStrategy.INSTANCE;

  public static void execute(final ClassicHttpRequest request,
    final Consumer<ClassicHttpResponse> action) {
    try (
      final CloseableHttpClient httpClient = newClient()) {
      getResponse(httpClient, request, (r) -> {
        action.accept(r);
        return null;
      });
    } catch (final ApacheHttpException e) {
      throw e;
    } catch (final WrappedException e) {
      throw e;
    } catch (final Exception e) {
      throw Exceptions.wrap(request.getRequestUri(), e);
    }
  }

  public static <V> V execute(final ClassicHttpRequest request,
    final Function<ClassicHttpResponse, V> action) {
    try (
      final CloseableHttpClient httpClient = newClient()) {
      return getResponse(httpClient, request, (r) -> {
        return action.apply(r);
      });
    } catch (final ApacheHttpException e) {
      throw e;
    } catch (final Exception e) {
      throw Exceptions.wrap(request.getRequestUri(), e);
    }
  }

  public static void execute(final ClassicRequestBuilder requestBuilder,
    final Consumer<ClassicHttpResponse> action) {
    final ClassicHttpRequest request = requestBuilder.build();
    execute(request, action);
  }

  public static <V> V execute(final ClassicRequestBuilder requestBuilder,
    final Function<ClassicHttpResponse, V> action) {
    final ClassicHttpRequest request = requestBuilder.build();
    return execute(request, action);
  }

  public static InputStream getInputStream(final ClassicHttpRequest request) {
    final CloseableHttpClient httpClient = newClient();
    try {
      final ClassicHttpResponse response = httpClient.executeOpen(null, request, null);
      final HttpEntity entity = response.getEntity();
      return new ApacheEntityInputStream(httpClient, entity);
    } catch (final ApacheHttpException e) {
      FileUtil.closeSilent(httpClient);
      throw e;
    } catch (final Exception e) {
      FileUtil.closeSilent(httpClient);
      throw Exceptions.wrap(request.getRequestUri().toString(), e);
    }
  }

  public static InputStream getInputStream(final ClassicRequestBuilder requestBuilder) {
    final ClassicHttpRequest request = requestBuilder.build();
    return getInputStream(request);
  }

  public static JsonObject getJson(final ClassicHttpResponse response) {
    final HttpEntity entity = response.getEntity();
    try (
      InputStream in = entity.getContent()) {
      return JsonParser.read(in);
    } catch (final Exception e) {
      throw Exceptions.wrap(e);
    }
  }

  public static JsonObject getJson(final ClassicRequestBuilder requestBuilder) {
    final Function<ClassicHttpResponse, JsonObject> function = ApacheHttp::getJson;
    return execute(requestBuilder, function);
  }

  public static <T> T getResponse(final CloseableHttpClient httpClient,
    final ClassicHttpRequest request, final HttpClientResponseHandler<T> handler) {
    try {
      return httpClient.execute(request, response -> {
        final int statusCode = response.getCode();
        if (statusCode >= 200 && statusCode <= 299) {
          return handler.handleResponse(response);
        } else {
          throw ApacheHttpException.create(request, response);
        }
      });
    } catch (final ApacheHttpException e) {
      throw e;
    } catch (final Exception e) {
      throw Exceptions.wrap(request.getRequestUri(), e);
    }
  }

  public static <T> T getResponse(final CloseableHttpClient httpClient,
    final ClassicRequestBuilder requestBuilder, final HttpClientResponseHandler<T> handler) {
    final ClassicHttpRequest request = requestBuilder.build();
    return getResponse(httpClient, request, handler);
  }

  public static String getString(final ClassicHttpResponse response) {
    final HttpEntity entity = response.getEntity();
    try (
      InputStream in = entity.getContent()) {
      return FileUtil.getString(in);
    } catch (final Exception e) {
      throw Exceptions.wrap(e);
    }
  }

  public static String getString(final ClassicRequestBuilder requestBuilder) {
    final Function<ClassicHttpResponse, String> function = ApacheHttp::getString;
    return execute(requestBuilder, function);
  }

  public static CloseableHttpClient newClient() {
    try {
      final SSLContext sslContext = SSLContextBuilder.create()
        .loadTrustMaterial(defaultTrustStrategy)
        .build();

      @SuppressWarnings("unused")
      final DefaultClientTlsStrategy tlsStrategy = new DefaultClientTlsStrategy(sslContext,
        (hostname, session) -> true);

      final PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder
        .create()
        .setTlsSocketStrategy(tlsStrategy)
        .build();

      return HttpClientBuilder//
        .create()
        .setConnectionManager(connectionManager)
        .build();
    } catch (final Exception e) {
      throw Exceptions.wrap(e);
    }
  }

  public static void setDefaultTrustStrategy(final TrustStrategy defaultTrustStrategy) {
    if (defaultTrustStrategy == null) {
      ApacheHttp.defaultTrustStrategy = TrustSelfSignedStrategy.INSTANCE;
    } else {
      ApacheHttp.defaultTrustStrategy = defaultTrustStrategy;
    }
  }

  public static ClassicRequestBuilder setJsonBody(final ClassicRequestBuilder requestBuilder,
    final JsonObject body) {
    final String jsonString = body.toJsonString();
    final StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
    requestBuilder.setEntity(entity);
    return requestBuilder;
  }

}
