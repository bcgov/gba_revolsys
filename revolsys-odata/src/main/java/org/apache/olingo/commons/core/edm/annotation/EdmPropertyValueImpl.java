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
package org.apache.olingo.commons.core.edm.annotation;

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmPropertyValue;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPropertyValue;
import org.apache.olingo.commons.core.edm.AbstractEdmAnnotatable;
import org.apache.olingo.commons.core.edm.Edm;

public class EdmPropertyValueImpl extends AbstractEdmAnnotatable implements EdmPropertyValue {

  private EdmExpression value;

  private final CsdlPropertyValue csdlExp;

  public EdmPropertyValueImpl(final Edm edm, final CsdlPropertyValue csdlExp) {
    super(edm, csdlExp);
    this.csdlExp = csdlExp;
  }

  @Override
  public String getProperty() {
    if (this.csdlExp.getProperty() == null) {
      throw new EdmException("PropertyValue expressions require a referenced property value.");
    }
    return this.csdlExp.getProperty();
  }

  @Override
  public EdmExpression getValue() {
    if (this.value == null) {
      if (this.csdlExp.getValue() == null) {
        throw new EdmException("PropertyValue expressions require an expression value.");
      }
      this.value = AbstractEdmExpression.getExpression(this.edm, this.csdlExp.getValue());
    }
    return this.value;
  }
}
