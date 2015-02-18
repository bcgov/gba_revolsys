package com.revolsys.gis.data.model;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.revolsys.comparator.NumericComparator;
import com.revolsys.converter.string.StringConverterRegistry;
import com.revolsys.gis.data.model.types.DataType;
import com.revolsys.gis.data.model.types.DataTypes;
import com.revolsys.io.AbstractObjectWithProperties;
import com.revolsys.io.map.InvokeMethodMapObjectFactory;
import com.revolsys.io.map.MapObjectFactory;
import com.revolsys.io.map.MapSerializer;
import com.revolsys.io.map.MapSerializerUtil;
import com.revolsys.util.CaseConverter;
import com.revolsys.util.CollectionUtil;
import com.revolsys.util.MathUtil;

/**
 * The Attribute class defines the name, type and other properties about each
 * attribute on a {@link DataObject} in the {@link DataObjectMetaData}.
 * 
 * @author Paul Austin
 * @see DataObject
 * @see DataObjectMetaData
 */
public class Attribute extends AbstractObjectWithProperties implements
  Cloneable, MapSerializer {

  public static final MapObjectFactory FACTORY = new InvokeMethodMapObjectFactory(
    "field", "Data Record Field", Attribute.class, "create");

  public static Attribute create(final Map<String, Object> properties) {
    return new Attribute(properties);
  }

  private final Map<Object, Object> allowedValues = new LinkedHashMap<Object, Object>();

  private Object defaultValue;

  /** The description of the attribute. */
  private String description;

  private int index;

  /** The maximum length of an attribute value. */
  private int length;

  /** The name of the attribute. */
  private String name;

  /** The flag indicating if a value is required for the attribute. */
  private boolean required;

  /** The maximum number of decimal places. */
  private int scale;

  /** The data type of the attribute value. */
  private DataType type;

  private Reference<DataObjectMetaData> metaData;

  private String title;

  private Object minValue;

  private Object maxValue;

  public Attribute() {
  }

  public Attribute(final Attribute attribute) {
    this.name = attribute.getName();
    this.title = attribute.getTitle();
    this.description = attribute.getDescription();
    this.type = attribute.getType();
    this.required = attribute.isRequired();
    this.length = attribute.getLength();
    this.scale = attribute.getScale();
    this.minValue = attribute.getMinValue();
    this.maxValue = attribute.getMaxValue();
    final Map<String, Object> properties = attribute.getProperties();
    setProperties(properties);
  }

  public Attribute(final int index) {
    this.index = index;
  }

  public Attribute(final Map<String, Object> properties) {
    this.name = CollectionUtil.getString(properties, "name");
    this.title = CollectionUtil.getString(properties, "title");
    if (!StringUtils.hasText(title)) {
      this.title = CaseConverter.toCapitalizedWords(name);
    }
    this.description = CollectionUtil.getString(properties, "description");
    this.type = DataTypes.getType(CollectionUtil.getString(properties,
      "dataType"));
    this.required = CollectionUtil.getBool(properties, "required");
    this.length = CollectionUtil.getInteger(properties, "length", 0);
    this.scale = CollectionUtil.getInteger(properties, "scale", 0);
    this.minValue = properties.get("minValue");
    if (minValue == null) {
      this.minValue = MathUtil.getMinValue(getTypeClass());
    } else {
      this.minValue = StringConverterRegistry.toString(type, minValue);
    }
    if (maxValue == null) {
      this.maxValue = MathUtil.getMaxValue(getTypeClass());
    } else {
      this.maxValue = StringConverterRegistry.toString(type, maxValue);
    }
  }

  /**
   * Construct a new attribute.
   * 
   * @param name The name of the attribute.
   * @param type The data type of the attribute value.
   * @param required The flag indicating if a value is required for the
   *          attribute.
   */
  public Attribute(final String name, final DataType type,
    final boolean required) {
    this(name, type, 0, 0, required, null, null);
  }

  /**
   * Construct a new attribute.
   * 
   * @param name The name of the attribute.
   * @param type The data type of the attribute value.
   * @param length The maximum length of an attribute value, 0 for no maximum.
   * @param required The flag indicating if a value is required for the
   *          attribute.
   * @param properties The meta data properties about the attribute.
   */
  public Attribute(final String name, final DataType type,
    final boolean required, final Map<String, Object> properties) {
    this(name, type, 0, 0, required, properties);
  }

  public Attribute(final String name, final DataType dataType,
    final boolean required, final String description) {
    this(name, dataType, 0, 0, required, description, null);
  }

  /**
   * Construct a new attribute.
   * 
   * @param name The name of the attribute.
   * @param type The data type of the attribute value.
   * @param length The maximum length of an attribute value, 0 for no maximum.
   * @param required The flag indicating if a value is required for the
   *          attribute.
   */
  public Attribute(final String name, final DataType type, final int length,
    final boolean required) {
    this(name, type, length, 0, required, null, null);
  }

  /**
   * Construct a new attribute.
   * 
   * @param name The name of the attribute.
   * @param type The data type of the attribute value.
   * @param length The maximum length of an attribute value, 0 for no maximum.
   * @param scale The maximum number of decimal places.
   * @param required The flag indicating if a value is required for the
   *          attribute.
   * @param properties The meta data properties about the attribute.
   */
  public Attribute(final String name, final DataType type, final int length,
    final boolean required, final Map<String, Object> properties) {
    this(name, type, length, 0, required, properties);
  }

  public Attribute(final String name, final DataType type, final int length,
    final boolean required, final String description) {
    this(name, type, length, 0, required, description, null);
  }

  /**
   * Construct a new attribute.
   * 
   * @param name The name of the attribute.
   * @param type The data type of the attribute value.
   * @param required The flag indicating if a value is required for the
   *          attribute.
   * @param properties The meta data properties about the attribute.
   */
  public Attribute(final String name, final DataType type,
    final Integer length, final Integer scale, final Boolean required) {
    this(name, type, length, scale, required, "");
  }

  /**
   * Construct a new attribute.
   * 
   * @param name The name of the attribute.
   * @param type The data type of the attribute value.
   * @param length The maximum length of an attribute value, 0 for no maximum.
   * @param scale The maximum number of decimal places.
   * @param required The flag indicating if a value is required for the
   *          attribute.
   * @param properties The meta data properties about the attribute.
   */
  public Attribute(final String name, final DataType type,
    final Integer length, final Integer scale, final Boolean required,
    final Map<String, Object> properties) {
    this(name, type, length, scale, required, null, properties);

  }

  /**
   * Construct a new attribute.
   * 
   * @param name The name of the attribute.
   * @param type The data type of the attribute value.
   * @param length The maximum length of an attribute value, 0 for no maximum.
   * @param scale The maximum number of decimal places.
   * @param required The flag indicating if a value is required for the
   *          attribute.
   * @param properties The meta data properties about the attribute.
   */
  public Attribute(final String name, final DataType type,
    final Integer length, final Integer scale, final Boolean required,
    final String description) {
    this.name = name;
    this.title = CaseConverter.toCapitalizedWords(name);
    this.description = description;
    this.type = type;
    if (required != null) {
      this.required = required;
    }
    if (length != null) {
      this.length = length;
    }
    if (scale != null) {
      this.scale = scale;
    }
    this.description = description;
    this.minValue = MathUtil.getMinValue(getTypeClass());
    this.maxValue = MathUtil.getMaxValue(getTypeClass());
  }

  /**
   * Construct a new attribute.
   * 
   * @param name The name of the attribute.
   * @param type The data type of the attribute value.
   * @param length The maximum length of an attribute value, 0 for no maximum.
   * @param scale The maximum number of decimal places.
   * @param required The flag indicating if a value is required for the
   *          attribute.
   * @param properties The meta data properties about the attribute.
   */
  public Attribute(final String name, final DataType type,
    final Integer length, final Integer scale, final Boolean required,
    final String description, final Map<String, Object> properties) {
    this.name = name;
    this.title = CaseConverter.toCapitalizedWords(name);
    this.type = type;
    if (required != null) {
      this.required = required;
    }
    if (length != null) {
      this.length = length;
    }
    if (scale != null) {
      this.scale = scale;
    }
    this.description = description;
    final Class<?> typeClass = getTypeClass();
    this.minValue = MathUtil.getMinValue(typeClass);
    this.maxValue = MathUtil.getMaxValue(typeClass);
    setProperties(properties);
  }

  public void addAllowedValue(final Object value, final Object text) {
    this.allowedValues.put(value, text);
  }

  public void appendType(final StringBuffer string) {
    string.append(type);
    if (length > 0) {
      string.append('(');
      string.append(length);
      if (scale > 0) {
        string.append(',');
        string.append(scale);
      }
      string.append(')');
    }
  }

  @Override
  public Attribute clone() {
    return new Attribute(this);
  }

  @Override
  public boolean equals(final Object object) {
    if (object instanceof Attribute) {
      final Attribute attribute = (Attribute)object;
      return (name.equals(attribute.getName()));
    } else {
      return false;
    }
  }

  public Map<Object, Object> getAllowedValues() {
    return allowedValues;
  }

  @SuppressWarnings("unchecked")
  public <T> T getDefaultValue() {
    return (T)defaultValue;
  }

  public String getDescription() {
    return description;
  }

  public int getIndex() {
    return index;
  }

  /**
   * Get the maximum length of the attribute value. The length 0 should be used
   * if there is no maximum.
   * 
   * @return The maximum length of an attribute value.
   */
  public int getLength() {
    return length;
  }

  public int getMaxStringLength() {
    int length = this.length;
    if (scale > 0) {
      length += 1;
      length += scale;
    }
    if (Number.class.isAssignableFrom(type.getJavaClass())) {
      length += 1;
    } else if (DataTypes.DATE.equals(type)) {
      return 10;
    }
    return length;
  }

  @SuppressWarnings("unchecked")
  public <V> V getMaxValue() {
    return (V)maxValue;
  }

  public DataObjectMetaData getMetaData() {
    if (metaData == null) {
      return null;
    } else {
      return metaData.get();
    }
  }

  @SuppressWarnings("unchecked")
  public <V> V getMinValue() {
    return (V)minValue;
  }

  /**
   * Get the name of the attribute.
   * 
   * @return The name of the attribute.
   */
  public String getName() {
    return name;
  }

  /**
   * Get the maximum number of decimal places of the attribute value.
   * 
   * @return The maximum number of decimal places.
   */
  public int getScale() {
    return scale;
  }

  public String getSimpleType() {
    final StringBuffer string = new StringBuffer();
    String typeName;
    if (Number.class.isAssignableFrom(getTypeClass())) {
      typeName = "NUMBER";
    } else if (CharSequence.class.isAssignableFrom(getTypeClass())) {
      typeName = "CHARACTER";
    } else {
      typeName = type.getName().toUpperCase();
    }
    string.append(typeName);
    if (length > 0) {
      string.append('(');
      string.append(length);
      if (scale > 0) {
        string.append(',');
        string.append(scale);
      }
      string.append(')');
    }
    return string.toString();
  }

  public String getTitle() {
    return title;
  }

  /**
   * Get the data type of the attribute value.
   * 
   * @return The data type of the attribute value.
   */
  public DataType getType() {
    return type;
  }

  /**
   * Get the data type class of the attribute value.
   * 
   * @return The data type of the attribute value.
   */
  public Class<?> getTypeClass() {
    if (type == null) {
      return Object.class;
    } else {
      return type.getJavaClass();
    }
  }

  /**
   * Get the data type of the attribute value.
   * 
   * @return The data type of the attribute value.
   */
  public String getTypeDescription() {
    final StringBuffer typeDescription = new StringBuffer();
    appendType(typeDescription);
    return typeDescription.toString();
  }

  /**
   * Return the hash code of the attribute.
   * 
   * @return The hash code.
   */
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /**
   * Get the flag indicating if a value is required for the attribute.
   * 
   * @return True if a value is required, false otherwise.
   */
  public boolean isRequired() {
    return required;
  }

  public void setAllowedValues(final Collection<?> allowedValues) {
    for (final Object allowedValue : allowedValues) {
      this.allowedValues.put(allowedValue, allowedValue);
    }
  }

  public void setAllowedValues(final Map<?, ?> allowedValues) {
    this.allowedValues.putAll(allowedValues);
  }

  public void setDefaultValue(final Object defaultValue) {
    this.defaultValue = defaultValue;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  void setIndex(final int index) {
    this.index = index;
  }

  public void setLength(final int length) {
    this.length = length;
  }

  public void setMaxValue(final Object maxValue) {
    this.maxValue = maxValue;
  }

  protected void setMetaData(final DataObjectMetaData metaData) {
    this.metaData = new WeakReference<DataObjectMetaData>(metaData);
  }

  public void setMinValue(final Object minValue) {
    this.minValue = minValue;
  }

  public void setRequired(final boolean required) {
    this.required = required;
  }

  public void setScale(final int scale) {
    this.scale = scale;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public void setType(final DataType type) {
    this.type = type;
  }

  @Override
  public Map<String, Object> toMap() {
    final Map<String, Object> map = new LinkedHashMap<String, Object>();
    map.put("type", "field");
    map.put("name", getName());
    map.put("title", getTitle());
    MapSerializerUtil.add(map, "description", getDescription(), "");
    map.put("dataType", getType().getName());
    map.put("length", getLength());
    map.put("scale", getScale());
    map.put("required", isRequired());
    MapSerializerUtil.add(map, "minValue", getMinValue(), null);
    MapSerializerUtil.add(map, "maxValue", getMaxValue(), null);
    MapSerializerUtil.add(map, "defaultValue", getDefaultValue(), null);
    MapSerializerUtil.add(map, "allowedValues", getAllowedValues(),
      Collections.emptyMap());
    return map;
  }

  @Override
  public String toString() {
    final StringBuffer string = new StringBuffer(name);
    string.append(':');
    appendType(string);
    return string.toString();
  }

  public void validate(Object value) {
    final String fieldName = getName();

    if (isRequired()) {
      if (value == null || value instanceof String
        && !StringUtils.hasText((String)value)) {
        throw new IllegalArgumentException(fieldName + " is required");
      }
    }
    final DataType fieldType = getType();
    if (value != null) {
      final Class<?> fieldClass = fieldType.getJavaClass();
      final Class<? extends Object> valueClass = value.getClass();
      if (!fieldClass.isAssignableFrom(valueClass)) {
        try {
          value = StringConverterRegistry.toObject(fieldType, value);
        } catch (final Throwable t) {
          throw new IllegalArgumentException(fieldName + "=" + value
            + " is not a valid " + fieldType);
        }
        if (value == null) {
          throw new IllegalArgumentException(fieldName + "=" + value
            + " is not a valid " + fieldType);
        }
      }
      if (value != null) {
        final int maxLength = getLength();
        if (value instanceof Number) {
          final Number number = (Number)value;
          final BigDecimal bigNumber = new BigDecimal(number.toString());
          final int length = bigNumber.precision();
          if (maxLength > 0) {
            if (length > maxLength) {
              throw new IllegalArgumentException(fieldName + "=" + value
                + " length " + length + " > " + maxLength);
            }
          }

          final int scale = bigNumber.scale();
          final int maxScale = getScale();
          if (maxScale > 0) {
            if (scale > maxScale) {
              throw new IllegalArgumentException(fieldName + "=" + value
                + " scale " + scale + " > " + maxScale);
            }
          }
          final Number minValue = getMinValue();
          if (minValue != null) {
            if (NumericComparator.numericCompare(number, minValue) < 0) {
              throw new IllegalArgumentException(fieldName + "=" + value
                + " > " + minValue);
            }
          }
          final Number maxValue = getMaxValue();
          if (maxValue != null) {
            if (NumericComparator.numericCompare(number, maxValue) > 0) {
              throw new IllegalArgumentException(fieldName + "=" + value
                + " < " + maxValue);
            }
          }
        } else if (value instanceof String) {
          final String string = (String)value;
          final int length = string.length();
          if (maxLength > 0) {
            if (length > maxLength) {
              throw new IllegalArgumentException(fieldName + "=" + value
                + " length " + length + " > " + maxLength);
            }
          }
        }
        if (!allowedValues.isEmpty()) {
          if (!allowedValues.containsKey(value)) {
            throw new IllegalArgumentException(fieldName + "=" + value
              + " not in (" + CollectionUtil.toString(",", allowedValues) + ")");
          }
        }
      }
    }

  }
}