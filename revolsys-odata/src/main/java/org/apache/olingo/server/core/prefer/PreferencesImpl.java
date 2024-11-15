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
package org.apache.olingo.server.core.prefer;

import java.net.URI;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.format.PreferenceName;
import org.apache.olingo.server.api.prefer.Preferences;

/**
 * Provides access methods to the preferences set in the Prefer HTTP request
 * header as described in <a href="https://www.ietf.org/rfc/rfc7240.txt">RFC 7240</a>.
 * Preferences defined in the OData standard can be accessed with named methods.
 */
public class PreferencesImpl implements Preferences {

  // parameter name for odata.callback
  private static final String URL = "url";

  private final Map<String, Preference> preferences;

  public PreferencesImpl(final Collection<String> preferHeaders) {
    this.preferences = PreferParser.parse(preferHeaders);
  }

  @Override
  public URI getCallback() {
    if (this.preferences.containsKey(PreferenceName.CALLBACK.getName())
      && this.preferences.get(PreferenceName.CALLBACK.getName()).getParameters() != null
      && this.preferences.get(PreferenceName.CALLBACK.getName()).getParameters().get(URL) != null) {
      try {
        return URI
          .create(this.preferences.get(PreferenceName.CALLBACK.getName()).getParameters().get(URL));
      } catch (final IllegalArgumentException e) {
        return null;
      }
    }
    return null;
  }

  @Override
  public Integer getMaxPageSize() {
    return getNonNegativeIntegerPreference(PreferenceName.MAX_PAGE_SIZE.getName());
  }

  private Integer getNonNegativeIntegerPreference(final String name) {
    if (this.preferences.containsKey(name) && this.preferences.get(name).getValue() != null) {
      try {
        final Integer result = Integer.valueOf(this.preferences.get(name).getValue());
        return result < 0 ? null : result;
      } catch (final NumberFormatException e) {
        return null;
      }
    }
    return null;
  }

  @Override
  public Preference getPreference(final String name) {
    return this.preferences.get(name.toLowerCase(Locale.ROOT));
  }

  @Override
  public Return getReturn() {
    if (this.preferences.containsKey(PreferenceName.RETURN.getName())) {
      final String value = this.preferences.get(PreferenceName.RETURN.getName()).getValue();
      if (Return.REPRESENTATION.toString().toLowerCase(Locale.ROOT).equals(value)) {
        return Return.REPRESENTATION;
      } else if (Return.MINIMAL.toString().toLowerCase(Locale.ROOT).equals(value)) {
        return Return.MINIMAL;
      }
    }
    return null;
  }

  @Override
  public Integer getWait() {
    return getNonNegativeIntegerPreference(PreferenceName.WAIT.getName());
  }

  @Override
  public boolean hasAllowEntityReferences() {
    return this.preferences.containsKey(PreferenceName.ALLOW_ENTITY_REFERENCES.getName());
  }

  @Override
  public boolean hasContinueOnError() {
    return this.preferences.containsKey(PreferenceName.CONTINUE_ON_ERROR.getName());
  }

  @Override
  public boolean hasRespondAsync() {
    return this.preferences.containsKey(PreferenceName.RESPOND_ASYNC.getName());
  }

  @Override
  public boolean hasTrackChanges() {
    return this.preferences.containsKey(PreferenceName.TRACK_CHANGES.getName())
      || this.preferences.containsKey(PreferenceName.TRACK_CHANGES_PREF.getName());
  }
}
