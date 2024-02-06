/*
 * Copyright 2004-2005 Revolution Systems Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.revolsys.ui.html.fields;

import com.revolsys.util.Property;
import com.revolsys.util.UrlUtil;

public class EmailAddressField extends TextField {

  public EmailAddressField(final String name, final boolean required) {
    this(name, null, required);
  }

  public EmailAddressField(final String name, final String defaultValue, final boolean required) {
    super(name, 50, 150, defaultValue, required);
    setCssClass("email");
    setType("email");
  }

  @Override
  public void setTextValue(final String value) {
    super.setTextValue(value);
    if (Property.hasValue(value)) {
      if (!UrlUtil.isValidEmail(getInputValue())) {
        throw new IllegalArgumentException("Enter a valid email address");
      }
    }
  }
}