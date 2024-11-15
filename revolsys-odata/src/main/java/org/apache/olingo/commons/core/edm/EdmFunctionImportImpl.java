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
package org.apache.olingo.commons.core.edm;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;

public class EdmFunctionImportImpl extends AbstractEdmOperationImport implements EdmFunctionImport {

  private final CsdlFunctionImport functionImport;

  public EdmFunctionImportImpl(final Edm edm, final EdmEntityContainer container,
    final CsdlFunctionImport functionImport) {
    super(edm, container, functionImport);
    this.functionImport = functionImport;
  }

  @Override
  public FullQualifiedName getFunctionFqn() {
    return this.functionImport.getFunctionFQN();
  }

  @Override
  public String getTitle() {
    return this.functionImport.getTitle();
  }

  @Override
  public EdmFunction getUnboundFunction(final List<String> parameterNames) {
    return this.edm.getUnboundFunction(getFunctionFqn(), parameterNames);
  }

  @Override
  public List<EdmFunction> getUnboundFunctions() {
    return this.edm.getUnboundFunctions(getFunctionFqn());
  }

  @Override
  public boolean isIncludeInServiceDocument() {
    return this.functionImport.isIncludeInServiceDocument();
  }
}
