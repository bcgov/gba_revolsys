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

import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmOperationImport;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlOperationImport;

public abstract class AbstractEdmOperationImport extends AbstractEdmNamed
  implements EdmOperationImport {

  protected final EdmEntityContainer container;

  private final Target entitySet;

  private EdmEntitySet returnedEntitySet;

  public AbstractEdmOperationImport(final Edm edm, final EdmEntityContainer container,
    final CsdlOperationImport operationImport) {
    super(edm, operationImport.getName(), operationImport);
    this.container = container;
    if (operationImport.getEntitySet() != null) {
      this.entitySet = new Target(operationImport.getEntitySet(), container);
    } else {
      this.entitySet = null;
    }
  }

  @Override
  public EdmEntityContainer getEntityContainer() {
    return this.container;
  }

  @Override
  public FullQualifiedName getFullQualifiedName() {
    return new FullQualifiedName(this.container.getNamespace(), getName());
  }

  @Override
  public EdmEntitySet getReturnedEntitySet() {
    if (this.entitySet != null && this.returnedEntitySet == null) {
      final EdmEntityContainer entityContainer = this.edm
        .getEntityContainer(this.entitySet.getEntityContainer());
      if (entityContainer == null) {
        throw new EdmException(
          "Can´t find entity container with name: " + this.entitySet.getEntityContainer());
      }
      this.returnedEntitySet = entityContainer.getEntitySet(this.entitySet.getTargetName());
      if (this.returnedEntitySet == null) {
        throw new EdmException(
          "Can´t find entity set with name: " + this.entitySet.getTargetName());
      }
    }
    return this.returnedEntitySet;
  }
}
