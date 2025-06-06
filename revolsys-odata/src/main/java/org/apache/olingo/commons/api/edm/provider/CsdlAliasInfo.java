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
package org.apache.olingo.commons.api.edm.provider;

/**
 * Represents an alias info CSDL item
 */
public class CsdlAliasInfo {

  private String namespace;

  private String alias;

  /**
   * Returns the alias of item
   * @return Alias alias
   */
  public String getAlias() {
    return this.alias;
  }

  /**
   * Returns the namespace of the alias
   * @return namespace of the alias
   */
  public String getNamespace() {
    return this.namespace;
  }

  /**
   * Sets the alias of the item
   * @param alias Alias
   * @return this instance
   */
  public CsdlAliasInfo setAlias(final String alias) {
    this.alias = alias;
    return this;
  }

  /**
   * Sets the namespace of the alias
   * @param namespace the namespace of the alias
   * @return this instance
   */
  public CsdlAliasInfo setNamespace(final String namespace) {
    this.namespace = namespace;
    return this;
  }

}
