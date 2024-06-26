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
package org.apache.olingo.server.core.uri.parser.search;

import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;

public class SearchTokenizerException extends UriParserSyntaxException {

  public enum MessageKeys implements MessageKey {
    /** parameter: character, TOKEN */
    FORBIDDEN_CHARACTER,
    /** parameter: TOKEN */
    NOT_EXPECTED_TOKEN,
    /** parameter: TOKEN */
    NOT_FINISHED_QUERY,
    /** parameter: TOKEN */
    INVALID_TOKEN_STATE,
    /** parameter: TOKEN */
    ALREADY_FINISHED;

    @Override
    public String getKey() {
      return name();
    }
  }

  private static final long serialVersionUID = -8295456415309640166L;

  public SearchTokenizerException(final String developmentMessage, final MessageKey messageKey,
    final String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  public SearchTokenizerException(final String developmentMessage, final Throwable cause,
    final MessageKey messageKey, final String... parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }
}
