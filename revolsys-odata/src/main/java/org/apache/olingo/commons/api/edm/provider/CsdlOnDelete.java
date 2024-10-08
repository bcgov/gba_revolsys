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

import java.util.ArrayList;
import java.util.List;

/**
 * The type Csdl on delete.
 */
public class CsdlOnDelete implements CsdlAbstractEdmItem, CsdlAnnotatable {

  private CsdlOnDeleteAction action = CsdlOnDeleteAction.None;

  private List<CsdlAnnotation> annotations = new ArrayList<>();

  /**
   * Gets action.
   *
   * @return the action
   */
  public CsdlOnDeleteAction getAction() {
    return this.action;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return this.annotations;
  }

  /**
   * Sets action.
   *
   * @param action the action
   * @return the action
   */
  public CsdlOnDelete setAction(final CsdlOnDeleteAction action) {
    this.action = action;
    return this;
  }

  /**
   * Sets annotations.
   *
   * @param annotations the annotations
   * @return the annotations
   */
  public CsdlOnDelete setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
}
