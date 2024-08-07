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
package org.apache.olingo.server.core.deserializer.batch;

import java.util.List;

import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;

public class BatchChangeSetPart extends BatchQueryOperation {
  private BatchQueryOperation request;

  public BatchChangeSetPart(final List<Line> message, final boolean isStrict)
    throws BatchDeserializerException {
    super(message, isStrict);
  }

  @Override
  public List<Line> getBody() {
    return this.request.getBody();
  }

  @Override
  public Line getHttpStatusLine() {
    return this.request.getHttpStatusLine();
  }

  public BatchQueryOperation getRequest() {
    return this.request;
  }

  @Override
  public BatchChangeSetPart parse() throws BatchDeserializerException {
    this.headers = BatchParserCommon.consumeHeaders(this.message);
    BatchParserCommon.consumeBlankLine(this.message, this.isStrict);

    this.request = new BatchQueryOperation(this.message, this.isStrict).parse();

    return this;
  }
}
