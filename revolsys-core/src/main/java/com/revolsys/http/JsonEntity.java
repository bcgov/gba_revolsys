package com.revolsys.http;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.revolsys.record.io.format.json.JsonType;

public class JsonEntity extends StringEntity {

  public JsonEntity(final JsonType json) {
    super(json.toJsonString(false), ContentType.APPLICATION_JSON);
  }

}
