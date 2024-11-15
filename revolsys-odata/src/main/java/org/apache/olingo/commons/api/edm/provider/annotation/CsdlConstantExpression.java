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
package org.apache.olingo.commons.api.edm.provider.annotation;

public class CsdlConstantExpression extends CsdlExpression {

  /**
   * Type of the constant expression
   */
  public enum ConstantExpressionType {
    /**
     * Type Edm.binary
     */
    Binary,
    /**
     * Type Edm.Bool
     */
    Bool,
    /**
     * Type Edm.Date
     */
    Date,
    /**
     * Type Edm.DateTimeOffset
     */
    DateTimeOffset,
    /**
     * Type Edm.Decimal
     */
    Decimal,
    /**
     * Type Edm.Duration
     */
    Duration,
    /**
     * Type Edm.EnumMeber
     */
    EnumMember,
    /**
     * Type Edm.Float
     */
    Float,
    /**
     * Type Edm.GUID
     */
    Guid,
    /**
     * Type Integer
     */
    Int,
    /**
     * Type Edm.String
     */
    String,
    /**
     * Type Edm.TimeOfDay
     */
    TimeOfDay;

    /**
     * Creates a new type by a given string e.g. "TimeOfDay".
     * Will NOT throw an IlligalArgumentException for invalid types. If needed use the valueOf method.
     * @param value Type as string
     * @return Type type
     */
    public static ConstantExpressionType fromString(final String value) {
      ConstantExpressionType result = null;
      try {
        result = valueOf(value);
      } catch (final IllegalArgumentException e) {
        // ignore
      }
      return result;
    }
  }

  private final ConstantExpressionType type;

  private String value;

  public CsdlConstantExpression(final ConstantExpressionType type) {
    this.type = type;
  }

  public CsdlConstantExpression(final ConstantExpressionType type, final String value) {
    this.type = type;
    this.value = value;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof CsdlConstantExpression)) {
      return false;
    }
    final CsdlConstantExpression csdlConstExp = (CsdlConstantExpression)obj;

    return (this.getValue() == null ? csdlConstExp.getValue() == null
      : this.getValue().equals(csdlConstExp.getValue()))
      && (this.getType() == null ? csdlConstExp.getType() == null
        : this.getType().equals(csdlConstExp.getType()));
  }

  /**
   * Returns the type of the constant expression
   * @return type of the constant expression
   */
  public ConstantExpressionType getType() {
    return this.type;
  }

  /**
   * Value of the constant expression
   * @return value of the constant expression as String
   */
  public String getValue() {
    return this.value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.type == null ? 0 : this.type.hashCode());
    result = prime * result + (this.value == null ? 0 : this.value.hashCode());
    return result;
  }

  /**
   * Sets the value of the constant expression
   * @param value value of the constant expression
   * @return this for method chaining
   */
  public CsdlConstantExpression setValue(final String value) {
    this.value = value;
    return this;
  }
}
