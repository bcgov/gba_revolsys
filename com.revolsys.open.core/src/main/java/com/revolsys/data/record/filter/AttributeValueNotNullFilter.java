package com.revolsys.data.record.filter;

import com.revolsys.data.record.Record;
import com.revolsys.data.record.Records;
import com.revolsys.filter.Filter;

/**
 * Filter records by the the attribute not having a null value.
 *
 * @author Paul Austin
 */
public class AttributeValueNotNullFilter implements Filter<Record> {

  /** The property name, or path to match. */
  private String attributeName;

  public AttributeValueNotNullFilter() {
  }

  public AttributeValueNotNullFilter(final String attributeName) {
    this.attributeName = attributeName;
  }

  /**
   * Match the property on the data object with the required value.
   *
   * @param object The object.
   * @return True if the object matched the filter, false otherwise.
   */
  @Override
  public boolean accept(final Record object) {
    final Object propertyValue = Records.getAttributeByPath(object, this.attributeName);
    return propertyValue != null;
  }

  public String getAttributeName() {
    return this.attributeName;
  }

  public void setAttributeName(final String attributeName) {
    this.attributeName = attributeName;
  }

  /**
   * @return the name
   */
  @Override
  public String toString() {
    return this.attributeName + " != null ";
  }
}