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
package org.apache.olingo.server.core.deserializer.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.IConstants;
import org.apache.olingo.commons.api.constants.Constantsv00;
import org.apache.olingo.commons.api.constants.Constantsv01;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.DeletedEntity;
import org.apache.olingo.commons.api.data.DeletedEntity.Reason;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerException.MessageKeys;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.deserializer.DeserializerResultImpl;
import org.apache.olingo.server.core.deserializer.helper.ExpandTreeBuilder;
import org.apache.olingo.server.core.deserializer.helper.ExpandTreeBuilderImpl;
import org.apache.olingo.server.core.serializer.utils.ContentTypeHelper;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ODataJsonDeserializer implements ODataDeserializer {

  private static final Map<String, Class<? extends Geospatial>> jsonNameToGeoDataType;
  static {
    final Map<String, Class<? extends Geospatial>> temp = new HashMap<>();
    temp.put(Constants.ELEM_POINT, Point.class);
    temp.put(Constants.ELEM_MULTIPOINT, MultiPoint.class);
    temp.put(Constants.ELEM_LINESTRING, LineString.class);
    temp.put("MultiLineString", MultiLineString.class);
    temp.put(Constants.ELEM_POLYGON, Polygon.class);
    temp.put("MultiPolygon", MultiPolygon.class);
    temp.put("GeometryCollection", GeospatialCollection.class);
    jsonNameToGeoDataType = Collections.unmodifiableMap(temp);
  }

  private static final String ODATA_ANNOTATION_MARKER = "@";

  private static final String ODATA_CONTROL_INFORMATION_PREFIX = "@odata.";

  private static final String REASON = "reason";

  private static final String ODATA_STREAM_PROPERTY_MEDIA_READ_LINK = "mediaReadLink";

  private static final String ODATA_STREAM_PROPERTY_MEDIA_EDIT_LINK = "mediaEditLink";

  private static final String ODATA_STREAM_PROPERTY_MEDIA_MIME_TYPE = "mediaMimeType";

  private final boolean isIEEE754Compatible;

  private ServiceMetadata serviceMetadata;

  private final IConstants constants;

  private final ODataJsonInstanceAnnotationDeserializer instanceAnnotDeserializer;

  public ODataJsonDeserializer(final ContentType contentType) {
    this(contentType, null, new Constantsv00());
  }

  public ODataJsonDeserializer(final ContentType contentType, final IConstants constants) {
    this.isIEEE754Compatible = ContentTypeHelper.isODataIEEE754Compatible(contentType);
    this.constants = constants;
    this.instanceAnnotDeserializer = new ODataJsonInstanceAnnotationDeserializer();
  }

  public ODataJsonDeserializer(final ContentType contentType,
    final ServiceMetadata serviceMetadata) {
    this.isIEEE754Compatible = ContentTypeHelper.isODataIEEE754Compatible(contentType);
    this.serviceMetadata = serviceMetadata;
    this.constants = new Constantsv00();
    this.instanceAnnotDeserializer = new ODataJsonInstanceAnnotationDeserializer();
  }

  public ODataJsonDeserializer(final ContentType contentType, final ServiceMetadata serviceMetadata,
    final IConstants constants) {
    this.isIEEE754Compatible = ContentTypeHelper.isODataIEEE754Compatible(contentType);
    this.serviceMetadata = serviceMetadata;
    this.constants = constants;
    this.instanceAnnotDeserializer = new ODataJsonInstanceAnnotationDeserializer();
  }

  @Override
  public DeserializerResult actionParameters(final InputStream stream, final EdmAction edmAction)
    throws DeserializerException {
    Map<String, Parameter> parameters = new HashMap<>();
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] inputContent = null;
    try {
      IOUtils.copy(stream, byteArrayOutputStream);
      // copy the content of input stream to reuse it
      inputContent = byteArrayOutputStream.toByteArray();
      if (inputContent.length > 0) {
        final InputStream inputStream1 = new ByteArrayInputStream(inputContent);
        final ObjectNode tree = parseJsonTree(inputStream1);
        parameters = consumeParameters(edmAction, tree);

        if (tree.isObject()) {
          removeAnnotations(tree);
        }
        assertJsonNodeIsEmpty(tree);
      }
      return DeserializerResultImpl.with().actionParameters(parameters).build();

    } catch (final IOException e) {
      throw wrapParseException(e);
    }
  }

  private void assertIsNullNode(final String key, final JsonNode jsonNode)
    throws DeserializerException {
    if (jsonNode.isNull()) {
      throw new DeserializerException("Annotation: " + key + "must not have a null value.",
        DeserializerException.MessageKeys.INVALID_NULL_ANNOTATION, key);
    }
  }

  /**
   * Validates that node is empty (<code>node.size() == 0</code>).
   * @param node node to be checked
   * @throws DeserializerException if node is not empty
   */
  private void assertJsonNodeIsEmpty(final JsonNode node) throws DeserializerException {
    if (node.size() != 0) {
      final String unknownField = node.fieldNames().next();
      throw new DeserializerException(
        "Tree should be empty but still has content left: " + unknownField,
        DeserializerException.MessageKeys.UNKNOWN_CONTENT, unknownField);
    }
  }

  /**
   * Check if JsonNode is a value node (<code>jsonNode.isValueNode()</code>) and if not throw
   * an DeserializerException.
   * @param name name of property which is checked
   * @param jsonNode node which is checked
   * @throws DeserializerException is thrown if json node is not a value node
   */
  private void checkForValueNode(final String name, final JsonNode jsonNode)
    throws DeserializerException {
    if (!jsonNode.isValueNode()) {
      throw new DeserializerException(
        "Invalid value for property: " + name + " must not be an object or array.",
        DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, name);
    }
  }

  private void checkJsonTypeBasedOnPrimitiveType(final String propertyName,
    final EdmPrimitiveType edmPrimitiveType, final JsonNode jsonNode) throws DeserializerException {
    boolean valid = true;
    if (edmPrimitiveType.getKind() == EdmTypeKind.DEFINITION) {
      checkJsonTypeBasedOnPrimitiveType(propertyName,
        ((EdmTypeDefinition)edmPrimitiveType).getUnderlyingType(), jsonNode);
    } else if (edmPrimitiveType.getKind() == EdmTypeKind.ENUM) {
      // Enum values must be strings.
      valid = jsonNode.isTextual();
    } else {
      final String name = edmPrimitiveType.getName();
      EdmPrimitiveTypeKind primKind;
      try {
        primKind = EdmPrimitiveTypeKind.valueOf(name);
      } catch (final IllegalArgumentException e) {
        throw new DeserializerException("Unknown Primitive Type: " + name, e,
          DeserializerException.MessageKeys.UNKNOWN_PRIMITIVE_TYPE, name, propertyName);
      }
      valid = matchTextualCase(jsonNode, primKind) || matchNumberCase(jsonNode, primKind)
        || matchBooleanCase(jsonNode, primKind) || matchIEEENumberCase(jsonNode, primKind)
        || jsonNode.isObject() && name.startsWith("Geo");
    }
    if (!valid) {
      throw new DeserializerException(
        "Invalid json type: " + jsonNode.getNodeType() + " for " + edmPrimitiveType + " property: "
          + propertyName,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, propertyName);
    }
  }

  /**
   * Check if jsonNode is not null or if null but nullable or collection navigationProperty
   *
   * @param jsonNode related json node
   * @param edmNavigationProperty related navigation property
   * @throws DeserializerException if jsonNode is not null or if null but nullable or collection navigationProperty
   */
  private void checkNotNullOrValidNull(final JsonNode jsonNode,
    final EdmNavigationProperty edmNavigationProperty) throws DeserializerException {
    final boolean isNullable = edmNavigationProperty.isNullable();
    if (jsonNode.isNull() && !isNullable
      || jsonNode.isNull() && edmNavigationProperty.isCollection()) {
      throw new DeserializerException(
        "Property: " + edmNavigationProperty.getName() + " must not be null.",
        MessageKeys.INVALID_NULL_PROPERTY, edmNavigationProperty.getName());
    }
  }

  private Link consumeBindingLink(final String key, final JsonNode jsonNode,
    final EdmEntityType edmEntityType) throws DeserializerException {
    final String[] splitKey = key.split(ODATA_ANNOTATION_MARKER);
    final String navigationPropertyName = splitKey[0];
    final EdmNavigationProperty edmNavigationProperty = edmEntityType
      .getNavigationProperty(navigationPropertyName);
    if (edmNavigationProperty == null) {
      throw new DeserializerException("Invalid navigationPropertyName: " + navigationPropertyName,
        DeserializerException.MessageKeys.NAVIGATION_PROPERTY_NOT_FOUND, navigationPropertyName);
    }
    final Link bindingLink = new Link();
    bindingLink.setTitle(navigationPropertyName);

    if (edmNavigationProperty.isCollection()) {
      assertIsNullNode(key, jsonNode);
      if (!jsonNode.isArray()) {
        throw new DeserializerException("Binding annotation: " + key + " must be an array.",
          DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, key);
      }
      final List<String> bindingLinkStrings = new ArrayList<>();
      for (final JsonNode arrayValue : jsonNode) {
        assertIsNullNode(key, arrayValue);
        if (!arrayValue.isTextual()) {
          throw new DeserializerException(
            "Binding annotation: " + key + " must have string valued array.",
            DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, key);
        }
        bindingLinkStrings.add(arrayValue.asText());
      }
      bindingLink.setType(Constants.ENTITY_COLLECTION_BINDING_LINK_TYPE);
      bindingLink.setBindingLinks(bindingLinkStrings);
    } else {
      if (!jsonNode.isValueNode()) {
        throw new DeserializerException("Binding annotation: " + key + " must be a string value.",
          DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, key);
      }
      if (edmNavigationProperty.isNullable() && jsonNode.isNull()) {
        bindingLink.setBindingLink(null);
      } else {
        assertIsNullNode(key, jsonNode);
        bindingLink.setBindingLink(jsonNode.asText());
      }
      bindingLink.setType(Constants.ENTITY_BINDING_LINK_TYPE);
    }
    return bindingLink;
  }

  private void consumeDeltaJsonNodeFields(final EdmEntityType edmEntityType, final ObjectNode node,
    final Entity entity, final ExpandTreeBuilder expandBuilder) throws DeserializerException {
    if (this.constants instanceof Constantsv01) {
      final List<String> navigationPropertyNames = edmEntityType.getNavigationPropertyNames();
      for (final String navigationPropertyName : navigationPropertyNames) {
        // read expanded navigation property for delta
        final String delta = navigationPropertyName + Constants.AT + Constants.DELTAVALUE;
        final JsonNode jsonNode = node.get(delta);
        final EdmNavigationProperty edmNavigationProperty = edmEntityType
          .getNavigationProperty(navigationPropertyName);
        if (jsonNode != null && jsonNode.isArray() && edmNavigationProperty.isCollection()) {
          checkNotNullOrValidNull(jsonNode, edmNavigationProperty);
          final Link link = new Link();
          link.setType(Constants.ENTITY_SET_NAVIGATION_LINK_TYPE);
          link.setTitle(navigationPropertyName);
          final Delta deltaValue = new Delta();
          for (final JsonNode arrayElement : jsonNode) {
            final String removed = Constants.AT + Constants.REMOVED;
            if (arrayElement.get(removed) != null) {
              // if @removed is present create a DeletedEntity Object
              final JsonNode reasonNode = arrayElement.get(removed);
              final DeletedEntity deletedEntity = new DeletedEntity();
              Reason reason = null;
              if (reasonNode.get(REASON) != null) {
                if (reasonNode.get(REASON).asText().equals(Reason.changed.name())) {
                  reason = Reason.changed;
                } else if (reasonNode.get(REASON).asText().equals(Reason.deleted.name())) {
                  reason = Reason.deleted;
                }
              } else {
                throw new DeserializerException("DeletedEntity reason is null.",
                  SerializerException.MessageKeys.MISSING_DELTA_PROPERTY, Constants.REASON);
              }
              deletedEntity.setReason(reason);
              try {
                deletedEntity.setId(new URI(arrayElement.get(this.constants.getId()).asText()));
              } catch (final URISyntaxException e) {
                throw new DeserializerException("Could not set Id for deleted Entity", e,
                  DeserializerException.MessageKeys.UNKNOWN_CONTENT);
              }
              deltaValue.getDeletedEntities().add(deletedEntity);
            } else {
              // For @id and properties create normal entity
              final Entity inlineEntity = consumeEntityNode(edmNavigationProperty.getType(),
                (ObjectNode)arrayElement, expandBuilder);
              deltaValue.getEntities().add(inlineEntity);
            }
          }
          link.setInlineEntitySet(deltaValue);
          entity.getNavigationLinks().add(link);
          node.remove(navigationPropertyName);
        }
      }
    }

  }

  private EntityCollection consumeEntityCollectionNode(final EdmEntityType edmEntityType,
    final ObjectNode tree, final ExpandTreeBuilder expandBuilder) throws DeserializerException {
    final EntityCollection entitySet = new EntityCollection();

    // Consume entities
    final JsonNode jsonNode = tree.get(Constants.VALUE);
    if (jsonNode != null) {
      entitySet.getEntities().addAll(consumeEntitySetArray(edmEntityType, jsonNode, expandBuilder));
      tree.remove(Constants.VALUE);
    } else {
      throw new DeserializerException("Could not find value array.",
        DeserializerException.MessageKeys.VALUE_ARRAY_NOT_PRESENT);
    }

    if (tree.isObject()) {
      removeAnnotations(tree);
    }
    assertJsonNodeIsEmpty(tree);

    return entitySet;
  }

  private Entity consumeEntityNode(final EdmEntityType edmEntityType, final ObjectNode tree,
    final ExpandTreeBuilder expandBuilder) throws DeserializerException {
    final Entity entity = new Entity();
    entity.setType(edmEntityType.getFullQualifiedName().getFullQualifiedNameAsString());

    // Check and consume @id for v4.01
    consumeId(tree, entity);

    // Check and consume all Properties
    consumeEntityProperties(edmEntityType, tree, entity);

    // Check and consume all expanded Navigation Properties
    consumeExpandedNavigationProperties(edmEntityType, tree, entity, expandBuilder);

    // consume delta json node fields for v4.01
    consumeDeltaJsonNodeFields(edmEntityType, tree, entity, expandBuilder);

    // consume remaining json node fields
    consumeRemainingJsonNodeFields(edmEntityType, tree, entity);

    assertJsonNodeIsEmpty(tree);

    return entity;
  }

  private void consumeEntityProperties(final EdmEntityType edmEntityType, final ObjectNode node,
    final Entity entity) throws DeserializerException {
    final List<String> propertyNames = edmEntityType.getPropertyNames();
    for (final String propertyName : propertyNames) {
      final JsonNode jsonNode = node.get(propertyName);
      if (jsonNode != null) {
        final EdmProperty edmProperty = (EdmProperty)edmEntityType.getProperty(propertyName);
        if (jsonNode.isNull() && !edmProperty.isNullable()) {
          throw new DeserializerException("Property: " + propertyName + " must not be null.",
            DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, propertyName);
        }
        final Property property = consumePropertyNode(edmProperty.getName(), edmProperty.getType(),
          edmProperty.isCollection(), edmProperty.isNullable(), edmProperty.getMaxLength(),
          edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
          edmProperty.getMapping(), jsonNode);
        entity.addProperty(property);
        node.remove(propertyName);
      }
    }
  }

  private List<Entity> consumeEntitySetArray(final EdmEntityType edmEntityType,
    final JsonNode jsonNode, final ExpandTreeBuilder expandBuilder) throws DeserializerException {
    if (jsonNode.isArray()) {
      final List<Entity> entities = new ArrayList<>();
      for (final JsonNode arrayElement : jsonNode) {
        if (arrayElement.isArray() || arrayElement.isValueNode()) {
          throw new DeserializerException(
            "Nested Arrays and primitive values are not allowed for an entity value.",
            DeserializerException.MessageKeys.INVALID_ENTITY);
        }
        final EdmEntityType derivedEdmEntityType = (EdmEntityType)getDerivedType(edmEntityType,
          arrayElement);
        entities
          .add(consumeEntityNode(derivedEdmEntityType, (ObjectNode)arrayElement, expandBuilder));
      }
      return entities;
    } else {
      throw new DeserializerException("The content of the value tag must be an Array but is not.",
        DeserializerException.MessageKeys.VALUE_TAG_MUST_BE_AN_ARRAY);
    }
  }

  private void consumeExpandedNavigationProperties(final EdmEntityType edmEntityType,
    final ObjectNode node, final Entity entity, final ExpandTreeBuilder expandBuilder)
    throws DeserializerException {
    final List<String> navigationPropertyNames = edmEntityType.getNavigationPropertyNames();
    for (final String navigationPropertyName : navigationPropertyNames) {
      // read expanded navigation property
      final JsonNode jsonNode = node.get(navigationPropertyName);
      if (jsonNode != null) {
        final EdmNavigationProperty edmNavigationProperty = edmEntityType
          .getNavigationProperty(navigationPropertyName);
        checkNotNullOrValidNull(jsonNode, edmNavigationProperty);

        final Link link = createLink(expandBuilder, navigationPropertyName, jsonNode,
          edmNavigationProperty);
        entity.getNavigationLinks().add(link);
        node.remove(navigationPropertyName);
      }
    }
  }

  private void consumeId(final ObjectNode node, final Entity entity) throws DeserializerException {
    if (node.get(this.constants.getId()) != null && this.constants instanceof Constantsv01) {
      try {
        entity.setId(new URI(node.get(this.constants.getId()).textValue()));
        node.remove(this.constants.getId());
      } catch (final URISyntaxException e) {
        throw new DeserializerException("Could not form Id", e,
          DeserializerException.MessageKeys.UNKNOWN_CONTENT);
      }
    }
  }

  private Map<String, Parameter> consumeParameters(final EdmAction edmAction, final ObjectNode node)
    throws DeserializerException {
    List<String> parameterNames = edmAction.getParameterNames();
    if (edmAction.isBound()) {
      // The binding parameter must not occur in the payload.
      parameterNames = parameterNames.subList(1, parameterNames.size());
    }
    final Map<String, Parameter> parameters = new LinkedHashMap<>();
    for (final String paramName : parameterNames) {
      final EdmParameter edmParameter = edmAction.getParameter(paramName);

      switch (edmParameter.getType().getKind()) {
        case PRIMITIVE:
        case DEFINITION:
        case ENUM:
        case COMPLEX:
        case ENTITY:
          final Parameter parameter = createParameter(node.get(paramName), paramName, edmParameter);
          parameters.put(paramName, parameter);
          node.remove(paramName);
        break;
        default:
          throw new DeserializerException(
            "Invalid type kind " + edmParameter.getType().getKind() + " for action parameter: "
              + paramName,
            DeserializerException.MessageKeys.INVALID_ACTION_PARAMETER_TYPE, paramName);
      }
    }
    return parameters;
  }

  private void consumePropertyCollectionNode(final String name, final EdmType type,
    final boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
    final boolean isUnicode, final EdmMapping mapping, final JsonNode jsonNode,
    final Property property) throws DeserializerException {

    Iterator<JsonNode> iterator;
    final List<Object> valueArray = new ArrayList<>();
    if (!jsonNode.isArray()) {
      iterator = Arrays.asList(jsonNode).iterator();
    } else {
      iterator = jsonNode.iterator();
    }
    switch (type.getKind()) {
      case PRIMITIVE:
      case DEFINITION:
      case ENUM:
        while (iterator.hasNext()) {
          final JsonNode arrayElement = iterator.next();
          final Object value = readPrimitiveValue(name, (EdmPrimitiveType)type, isNullable,
            maxLength, precision, scale, isUnicode, mapping, arrayElement);
          valueArray.add(value);
        }
        property.setValue(type.getKind() == EdmTypeKind.ENUM ? ValueType.COLLECTION_ENUM
          : ValueType.COLLECTION_PRIMITIVE, valueArray);
      break;
      case COMPLEX:
        while (iterator.hasNext()) {
          // read and add all complex properties
          final Object value = readComplexNode(name, type, isNullable, iterator.next());
          valueArray.add(value);
        }
        property.setValue(ValueType.COLLECTION_COMPLEX, valueArray);
      break;
      default:
        throw new DeserializerException("Invalid Type Kind for a property found: " + type.getKind(),
          DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, name);
    }
  }

  private Property consumePropertyNode(final String name, final EdmType type,
    final boolean isCollection, final boolean isNullable, final Integer maxLength,
    final Integer precision, final Integer scale, final boolean isUnicode, final EdmMapping mapping,
    final JsonNode jsonNode) throws DeserializerException {
    final Property property = new Property();
    property.setName(name);
    property.setType(type.getFullQualifiedName().getFullQualifiedNameAsString());
    if (isCollection) {
      consumePropertyCollectionNode(name, type, isNullable, maxLength, precision, scale, isUnicode,
        mapping, jsonNode, property);
    } else {
      consumePropertySingleNode(name, type, isNullable, maxLength, precision, scale, isUnicode,
        mapping, jsonNode, property);
    }
    return property;
  }

  private void consumePropertySingleNode(final String name, final EdmType type,
    final boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
    final boolean isUnicode, final EdmMapping mapping, final JsonNode jsonNode,
    final Property property) throws DeserializerException {
    switch (type.getKind()) {
      case PRIMITIVE:
      case DEFINITION:
      case ENUM:
        Object value = readPrimitiveValue(name, (EdmPrimitiveType)type, isNullable, maxLength,
          precision, scale, isUnicode, mapping, jsonNode);
        property.setValue(type.getKind() == EdmTypeKind.ENUM ? ValueType.ENUM : ValueType.PRIMITIVE,
          value);
      break;
      case COMPLEX:
        final EdmType derivedType = getDerivedType((EdmComplexType)type, jsonNode);
        property.setType(derivedType.getFullQualifiedName().getFullQualifiedNameAsString());

        value = readComplexNode(name, derivedType, isNullable, jsonNode);
        property.setValue(ValueType.COMPLEX, value);
      break;
      default:
        throw new DeserializerException("Invalid Type Kind for a property found: " + type.getKind(),
          DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, name);
    }
  }

  /**
   * Consumes all remaining fields of Json ObjectNode and tries to map found values
   * to according Entity fields and omits OData fields to be ignored (e.g., control information).
   *
   * @param edmEntityType edm entity type which for which the json node is consumed
   * @param node json node which is consumed
   * @param entity entity instance which is filled
   * @throws DeserializerException if an exception during consumation occurs
   */
  private void consumeRemainingJsonNodeFields(final EdmEntityType edmEntityType,
    final ObjectNode node, final Entity entity) throws DeserializerException {
    final List<String> toRemove = new ArrayList<>();
    final Iterator<Entry<String, JsonNode>> fieldsIterator = node.fields();
    while (fieldsIterator.hasNext()) {
      final Entry<String, JsonNode> field = fieldsIterator.next();

      if (field.getKey().contains(this.constants.getBind())) {
        final Link bindingLink = consumeBindingLink(field.getKey(), field.getValue(),
          edmEntityType);
        entity.getNavigationBindings().add(bindingLink);
        toRemove.add(field.getKey());
      } else if (!field.getKey().contains(ODATA_CONTROL_INFORMATION_PREFIX)
        && field.getKey().contains(ODATA_ANNOTATION_MARKER)
        && field.getKey()
          .substring(field.getKey().indexOf(ODATA_ANNOTATION_MARKER))
          .contains(".")) {
        // Instance annotations start with @ sign followed by
        // alias or namespace
        // followed by a dot and then term name
        final String[] keySplit = field.getKey().split(ODATA_ANNOTATION_MARKER);
        final String termName = keySplit[1];
        final Annotation annotation = this.instanceAnnotDeserializer
          .consumeInstanceAnnotation(termName, field.getValue());
        // If keySplit has a value at zeroth index then instance annotation is
        // specified like
        // propertyName@Term
        if (!keySplit[0].isEmpty()) {
          if (edmEntityType.getPropertyNames().contains(keySplit[0])) {
            entity.getProperty(keySplit[0]).getAnnotations().add(annotation);
          } else if (edmEntityType.getNavigationPropertyNames().contains(keySplit[0])) {
            final Link link = entity.getNavigationLink(keySplit[0]);
            link.getAnnotations().add(annotation);
          }
        } else {
          entity.getAnnotations().add(annotation);
        }
        toRemove.add(field.getKey());
      } else if (isStreamPropertyNode(field.getKey())) {
        consumeStreamPropertyNode(entity, edmEntityType, field);
        toRemove.add(field.getKey());
      }
    }
    // remove here to avoid iterator issues.
    node.remove(toRemove);

    removeAnnotations(node);
  }

  /**
   * Construct a empty {@code Property} and fill stream property annotation data into it
   *
   * @param entity entity instance which is filled
   * @param edmEntityType edm entity type which for which the json node is consumed
   * @param field Json field entry which current consuming
   *
   * @throws DeserializerException thrown by {@code instanceAnnotDeserializer} if consume
   * instance annotation failed
   */
  private void consumeStreamPropertyNode(final Entity entity, final EdmEntityType edmEntityType,
    final Entry<String, JsonNode> field) throws DeserializerException {
    final String[] keySplit = field.getKey().split(ODATA_ANNOTATION_MARKER);
    final String termName = keySplit[1];
    final Annotation annotation = this.instanceAnnotDeserializer.consumeInstanceAnnotation(termName,
      field.getValue());
    final String propertyName = keySplit[0];
    if (edmEntityType.getProperty(propertyName) == null) {
      return;
    }

    Property property = entity.getProperty(propertyName);
    if (property == null) {
      property = new Property();
      property.setName(propertyName);
      entity.addProperty(property);
    }
    property.getAnnotations().add(annotation);
  }

  private Link createLink(final ExpandTreeBuilder expandBuilder,
    final String navigationPropertyName, final JsonNode jsonNode,
    final EdmNavigationProperty edmNavigationProperty) throws DeserializerException {
    final Link link = new Link();
    link.setTitle(navigationPropertyName);
    final ExpandTreeBuilder childExpandBuilder = expandBuilder != null
      ? expandBuilder.expand(edmNavigationProperty)
      : null;
    final EdmEntityType derivedEdmEntityType = (EdmEntityType)getDerivedType(
      edmNavigationProperty.getType(), jsonNode);
    if (jsonNode.isArray() && edmNavigationProperty.isCollection()) {
      link.setType(Constants.ENTITY_SET_NAVIGATION_LINK_TYPE);
      final EntityCollection inlineEntitySet = new EntityCollection();
      inlineEntitySet.getEntities()
        .addAll(consumeEntitySetArray(derivedEdmEntityType, jsonNode, childExpandBuilder));
      link.setInlineEntitySet(inlineEntitySet);
    } else if (!jsonNode.isArray() && (!jsonNode.isValueNode() || jsonNode.isNull())
      && !edmNavigationProperty.isCollection()) {
      link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
      if (!jsonNode.isNull()) {
        final Entity inlineEntity = consumeEntityNode(derivedEdmEntityType, (ObjectNode)jsonNode,
          childExpandBuilder);
        link.setInlineEntity(inlineEntity);
      }
    } else {
      throw new DeserializerException(
        "Invalid value: " + jsonNode.getNodeType() + " for expanded navigation property: "
          + navigationPropertyName,
        MessageKeys.INVALID_VALUE_FOR_NAVIGATION_PROPERTY, navigationPropertyName);
    }
    return link;
  }

  private Parameter createParameter(final JsonNode node, final String paramName,
    final EdmParameter edmParameter) throws DeserializerException {
    final Parameter parameter = new Parameter();
    parameter.setName(paramName);
    if (node == null || node.isNull()) {
      if (!edmParameter.isNullable()) {
        throw new DeserializerException("Non-nullable parameter not present or null: " + paramName,
          MessageKeys.INVALID_NULL_PARAMETER, paramName);
      }
      if (edmParameter.isCollection()) {
        throw new DeserializerException("Collection must not be null for parameter: " + paramName,
          MessageKeys.INVALID_NULL_PARAMETER, paramName);
      }
      parameter.setValue(ValueType.PRIMITIVE, null);
    } else if (edmParameter.getType().getKind() == EdmTypeKind.ENTITY) {
      if (edmParameter.isCollection()) {
        final EntityCollection entityCollection = new EntityCollection();
        entityCollection.getEntities()
          .addAll(consumeEntitySetArray((EdmEntityType)edmParameter.getType(), node, null));
        parameter.setValue(ValueType.COLLECTION_ENTITY, entityCollection);
      } else {
        final Entity entity = consumeEntityNode((EdmEntityType)edmParameter.getType(),
          (ObjectNode)node, null);
        parameter.setValue(ValueType.ENTITY, entity);
      }
    } else {
      final Property property = consumePropertyNode(edmParameter.getName(), edmParameter.getType(),
        edmParameter.isCollection(), edmParameter.isNullable(), edmParameter.getMaxLength(),
        edmParameter.getPrecision(), edmParameter.getScale(), true, edmParameter.getMapping(),
        node);
      parameter.setValue(property.getValueType(), property.getValue());
      parameter.setType(property.getType());
    }
    return parameter;
  }

  @Override
  public DeserializerResult entity(final InputStream stream, final EdmEntityType edmEntityType)
    throws DeserializerException {
    try {
      final ObjectNode tree = parseJsonTree(stream);
      final ExpandTreeBuilder expandBuilder = ExpandTreeBuilderImpl.create();

      final EdmEntityType derivedEdmEntityType = (EdmEntityType)getDerivedType(edmEntityType, tree);

      return DeserializerResultImpl.with()
        .entity(consumeEntityNode(derivedEdmEntityType, tree, expandBuilder))
        .expandOption(expandBuilder.build())
        .build();
    } catch (final IOException e) {
      throw wrapParseException(e);
    }
  }

  @Override
  public DeserializerResult entityCollection(final InputStream stream,
    final EdmEntityType edmEntityType) throws DeserializerException {
    try {
      return DeserializerResultImpl.with()
        .entityCollection(consumeEntityCollectionNode(edmEntityType, parseJsonTree(stream), null))
        .build();
    } catch (final IOException e) {
      throw wrapParseException(e);
    }
  }

  @Override
  public DeserializerResult entityReferences(final InputStream stream)
    throws DeserializerException {
    try {
      final List<URI> parsedValues = new ArrayList<>();
      final ObjectNode tree = parseJsonTree(stream);
      final String key = this.constants.getId();
      final JsonNode jsonNode = tree.get(Constants.VALUE);
      if (jsonNode != null) {
        if (jsonNode.isArray()) {
          final ArrayNode arrayNode = (ArrayNode)jsonNode;
          for (final JsonNode next : arrayNode) {
            if (next.has(key)) {
              parsedValues.add(new URI(next.get(key).asText()));
            }
          }
        } else {
          throw new DeserializerException("Value must be an array",
            DeserializerException.MessageKeys.UNKNOWN_CONTENT);
        }
        tree.remove(Constants.VALUE);
        return DeserializerResultImpl.with().entityReferences(parsedValues).build();
      }
      if (tree.get(key) != null) {
        parsedValues.add(new URI(tree.get(key).asText()));
      } else {
        throw new DeserializerException("Missing entity reference",
          DeserializerException.MessageKeys.UNKNOWN_CONTENT);
      }
      return DeserializerResultImpl.with().entityReferences(parsedValues).build();
    } catch (final IOException e) {
      throw wrapParseException(e);
    } catch (final URISyntaxException e) {
      throw new DeserializerException("failed to read @odata.id", e,
        DeserializerException.MessageKeys.UNKNOWN_CONTENT);
    }
  }

  private EdmType getDerivedType(final EdmStructuredType edmType, final JsonNode jsonNode)
    throws DeserializerException {
    final JsonNode odataTypeNode = jsonNode.get(this.constants.getType());
    if (odataTypeNode != null) {
      String odataType = odataTypeNode.asText();
      if (!odataType.isEmpty()) {
        odataType = odataType.substring(1);

        if (odataType
          .equalsIgnoreCase(edmType.getFullQualifiedName().getFullQualifiedNameAsString())) {
          return edmType;
        } else if (this.serviceMetadata == null) {
          throw new DeserializerException(
            "Failed to resolve Odata type " + odataType + " due to metadata is not available",
            DeserializerException.MessageKeys.UNKNOWN_CONTENT);
        }

        final EdmStructuredType currentEdmType = edmType.getKind() == EdmTypeKind.ENTITY
          ? this.serviceMetadata.getEdm().getEntityType(new FullQualifiedName(odataType))
          : this.serviceMetadata.getEdm().getComplexType(new FullQualifiedName(odataType));
        if (!isAssignable(edmType, currentEdmType)) {
          throw new DeserializerException("Odata type " + odataType + " not allowed here",
            DeserializerException.MessageKeys.UNKNOWN_CONTENT);
        }

        return currentEdmType;
      }
    }
    return edmType;
  }

  private double getDoubleValue(final String value) throws EdmPrimitiveTypeException {
    final BigDecimal bigDecimalValue = new BigDecimal(value);
    final Double result = bigDecimalValue.doubleValue();
    // "Real" infinite values cannot occur, so we can throw an exception
    // if the conversion to a double results in an infinite value.
    // An exception is also thrown if the number cannot be stored in a double
    // without loss.
    if (result.isInfinite() || BigDecimal.valueOf(result).compareTo(bigDecimalValue) != 0) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
    }
    return result;
  }

  /**
   * Returns the primitive type's default class or the manually mapped class if present.
   * @param mapping
   * @param type
   * @return the java class to be used during deserialization
   */
  private Class<?> getJavaClassForPrimitiveType(final EdmMapping mapping,
    final EdmPrimitiveType type) {
    final EdmPrimitiveType edmPrimitiveType = type.getKind() == EdmTypeKind.ENUM
      ? ((EdmEnumType)type).getUnderlyingType()
      : type.getKind() == EdmTypeKind.DEFINITION ? ((EdmTypeDefinition)type).getUnderlyingType()
        : type;
    return mapping == null || mapping.getMappedJavaClass() == null
      ? edmPrimitiveType.getDefaultType()
      : mapping.getMappedJavaClass();
  }

  private boolean isAssignable(final EdmStructuredType edmStructuredType,
    final EdmStructuredType edmStructuredTypeToAssign) {
    return edmStructuredTypeToAssign != null && (edmStructuredType.getFullQualifiedName()
      .equals(edmStructuredTypeToAssign.getFullQualifiedName())
      || isAssignable(edmStructuredType, edmStructuredTypeToAssign.getBaseType()));
  }

  /**
   * Process stream property instance annotation,
   * include
   * <ul>
   * <li>odata.mediaReadLink for 4.0 or mediaReadLink for 4.01</li>
   * <li>odata.mediaEditLink for 4.0 or mediaEditLink for 4.01</li>
   * <li>odata.mediaMimeType for 4.0 or mediaMimeType for 4.01</li>
   * </ul>
   *
   * @return true if jsonNodeKey present stream property annotation, false for otherwise
   */
  private boolean isStreamPropertyNode(final String jsonNodeKey) {
    return jsonNodeKey.endsWith(ODATA_STREAM_PROPERTY_MEDIA_READ_LINK)
      || jsonNodeKey.endsWith(ODATA_STREAM_PROPERTY_MEDIA_EDIT_LINK)
      || jsonNodeKey.endsWith(ODATA_STREAM_PROPERTY_MEDIA_MIME_TYPE);
  }

  private boolean isValidNull(final String name, final boolean isNullable, final JsonNode jsonNode)
    throws DeserializerException {
    if (jsonNode.isNull()) {
      if (isNullable) {
        return true;
      } else {
        throw new DeserializerException("Property: " + name + " must not be null.",
          DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, name);
      }
    }
    return false;
  }

  private boolean matchBooleanCase(final JsonNode node, final EdmPrimitiveTypeKind primKind) {
    return node.isBoolean() && primKind == EdmPrimitiveTypeKind.Boolean;
  }

  private boolean matchIEEENumberCase(final JsonNode node, final EdmPrimitiveTypeKind primKind) {
    return (this.isIEEE754Compatible ? node.isTextual() : node.isNumber())
      && (primKind == EdmPrimitiveTypeKind.Int64 || primKind == EdmPrimitiveTypeKind.Decimal);
  }

  private boolean matchNumberCase(final JsonNode node, final EdmPrimitiveTypeKind primKind) {
    return node.isNumber()
      && (primKind == EdmPrimitiveTypeKind.Int16 || primKind == EdmPrimitiveTypeKind.Int32
        || primKind == EdmPrimitiveTypeKind.Byte || primKind == EdmPrimitiveTypeKind.SByte
        || primKind == EdmPrimitiveTypeKind.Single || primKind == EdmPrimitiveTypeKind.Double);
  }

  private boolean matchTextualCase(final JsonNode node, final EdmPrimitiveTypeKind primKind) {
    return node.isTextual()
      && (primKind == EdmPrimitiveTypeKind.String || primKind == EdmPrimitiveTypeKind.Binary
        || primKind == EdmPrimitiveTypeKind.Date || primKind == EdmPrimitiveTypeKind.DateTimeOffset
        || primKind == EdmPrimitiveTypeKind.Duration || primKind == EdmPrimitiveTypeKind.Guid
        || primKind == EdmPrimitiveTypeKind.TimeOfDay);
  }

  /** Reads a parameter value from a String. */
  public Parameter parameter(final String content, final EdmParameter parameter)
    throws DeserializerException {
    try {
      final JsonParser parser = new JsonFactory(
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true))
          .createParser(content);
      final JsonNode node = parser.getCodec().readTree(parser);
      if (node == null) {
        throw new DeserializerException("Invalid JSON syntax.",
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
      }
      final Parameter result = createParameter(node, parameter.getName(), parameter);
      if (node.isObject()) {
        removeAnnotations((ObjectNode)node);
        assertJsonNodeIsEmpty(node);
      }
      return result;
    } catch (final IOException e) {
      throw wrapParseException(e);
    }
  }

  private ObjectNode parseJsonTree(final InputStream stream)
    throws IOException, DeserializerException {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
    objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
    final JsonParser parser = new JsonFactory(objectMapper).createParser(stream);
    final JsonNode tree = parser.getCodec().readTree(parser);
    if (tree == null || !tree.isObject()) {
      throw new DeserializerException("Invalid JSON syntax.",
        DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    }
    return (ObjectNode)tree;
  }

  @Override
  public DeserializerResult property(final InputStream stream, final EdmProperty edmProperty)
    throws DeserializerException {
    try {
      final ObjectNode tree = parseJsonTree(stream);

      final Property property;
      final JsonNode jsonNode = tree.get(Constants.VALUE);
      if (jsonNode != null) {
        property = consumePropertyNode(edmProperty.getName(), edmProperty.getType(),
          edmProperty.isCollection(), edmProperty.isNullable(), edmProperty.getMaxLength(),
          edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
          edmProperty.getMapping(), jsonNode);
        tree.remove(Constants.VALUE);
      } else {
        property = consumePropertyNode(edmProperty.getName(), edmProperty.getType(),
          edmProperty.isCollection(), edmProperty.isNullable(), edmProperty.getMaxLength(),
          edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
          edmProperty.getMapping(), tree);
      }
      return DeserializerResultImpl.with().property(property).build();
    } catch (final IOException e) {
      throw wrapParseException(e);
    }
  }

  private Object readComplexNode(final String name, final EdmType type, final boolean isNullable,
    final JsonNode jsonNode) throws DeserializerException {
    // read and add all complex properties
    final ComplexValue value = readComplexValue(name, type, isNullable, jsonNode);

    if (jsonNode.isObject()) {
      removeAnnotations((ObjectNode)jsonNode);
    }
    // Afterwards the node must be empty
    assertJsonNodeIsEmpty(jsonNode);

    return value;
  }

  private ComplexValue readComplexValue(final String name, final EdmType type,
    final boolean isNullable, final JsonNode jsonNode) throws DeserializerException {
    if (isValidNull(name, isNullable, jsonNode)) {
      return null;
    }
    if (jsonNode.isArray() || !jsonNode.isContainerNode()) {
      throw new DeserializerException(
        "Invalid value for property: " + name + " must not be an array or primitive value.",
        DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, name);
    }
    // Even if there are no properties defined we have to give back an empty
    // list
    final ComplexValue complexValue = new ComplexValue();
    EdmComplexType edmType = (EdmComplexType)type;

    // Check if the properties are from derived type
    edmType = (EdmComplexType)getDerivedType(edmType, jsonNode);

    // Check and consume all Properties
    for (final String propertyName : edmType.getPropertyNames()) {
      final JsonNode subNode = jsonNode.get(propertyName);
      if (subNode != null) {
        final EdmProperty edmProperty = (EdmProperty)edmType.getProperty(propertyName);
        if (subNode.isNull() && !edmProperty.isNullable()) {
          throw new DeserializerException("Property: " + propertyName + " must not be null.",
            DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, propertyName);
        }
        final Property property = consumePropertyNode(edmProperty.getName(), edmProperty.getType(),
          edmProperty.isCollection(), edmProperty.isNullable(), edmProperty.getMaxLength(),
          edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
          edmProperty.getMapping(), subNode);
        complexValue.getValue().add(property);
        ((ObjectNode)jsonNode).remove(propertyName);
      }
    }
    complexValue.setTypeName(edmType.getFullQualifiedName().getFullQualifiedNameAsString());
    return complexValue;
  }

  private Point readGeoPointValue(final String name, final Geospatial.Dimension dimension,
    final JsonNode node, final SRID srid) throws DeserializerException, EdmPrimitiveTypeException {
    if (node.isArray() && (node.size() == 2 || node.size() == 3) && node.get(0).isNumber()
      && node.get(1).isNumber() && (node.get(2) == null || node.get(2).isNumber())) {
      final Point point = new Point(dimension, srid);
      point.setX(getDoubleValue(node.get(0).asText()));
      point.setY(getDoubleValue(node.get(1).asText()));
      if (node.get(2) != null) {
        point.setZ(getDoubleValue(node.get(2).asText()));
      }
      return point;
    }
    throw new DeserializerException("Invalid point value '" + node + "' in property: " + name,
      DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, name);
  }

  private List<Point> readGeoPointValues(final String name, final Geospatial.Dimension dimension,
    final int minimalSize, final boolean closed, final JsonNode node)
    throws DeserializerException, EdmPrimitiveTypeException {
    if (node.isArray()) {
      final List<Point> points = new ArrayList<>();
      for (final JsonNode element : node) {
        points.add(readGeoPointValue(name, dimension, element, null));
      }
      if (points.size() >= minimalSize
        && (!closed || points.get(points.size() - 1).equals(points.get(0)))) {
        return points;
      }
    }
    throw new DeserializerException("Invalid point values '" + node + "' in property: " + name,
      DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, name);
  }

  private Polygon readGeoPolygon(final String name, final Geospatial.Dimension dimension,
    final JsonNode node, final SRID srid) throws DeserializerException, EdmPrimitiveTypeException {
    // GeoJSON would allow for more than one interior polygon (hole).
    // But there is no place in the data object to store this information so for
    // now we throw an error.
    // There could be a more strict verification that the lines describe
    // boundaries and have the correct winding order.
    if (node.isArray() && (node.size() == 1 || node.size() == 2)) {
      return new Polygon(dimension, srid,
        node.size() > 1 ? readGeoPointValues(name, dimension, 4, true, node.get(1)) : null,
        readGeoPointValues(name, dimension, 4, true, node.get(0)));
    }
    throw new DeserializerException("Invalid polygon values '" + node + "' in property: " + name,
      DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, name);
  }

  /**
   * Reads a geospatial JSON value following the GeoJSON specification defined in RFC 7946.
   * @param name property name
   * @param type EDM type of the value
   *             (can be <code>null</code> for recursive calls while parsing a GeometryCollection)
   */
  private Geospatial readPrimitiveGeoValue(final String name, final EdmPrimitiveType type,
    final ObjectNode jsonNode) throws DeserializerException, EdmPrimitiveTypeException {
    final JsonNode typeNode = jsonNode.remove(Constants.ATTR_TYPE);
    if (typeNode != null && typeNode.isTextual()) {
      final Class<? extends Geospatial> geoDataType = jsonNameToGeoDataType.get(typeNode.asText());
      if (geoDataType != null && (type == null || geoDataType.equals(type.getDefaultType()))) {
        final JsonNode topNode = jsonNode
          .remove(geoDataType.equals(GeospatialCollection.class) ? Constants.JSON_GEOMETRIES
            : Constants.JSON_COORDINATES);

        SRID srid = null;
        if (jsonNode.has(Constants.JSON_CRS)) {
          srid = SRID.valueOf(jsonNode.remove(Constants.JSON_CRS)
            .get(Constants.PROPERTIES)
            .get(Constants.JSON_NAME)
            .asText()
            .split(":")[1]);
        }

        assertJsonNodeIsEmpty(jsonNode);

        if (topNode != null && topNode.isArray()) {
          final Geospatial.Dimension dimension = type == null
            || type.getName().startsWith("Geometry") ? Geospatial.Dimension.GEOMETRY
              : Geospatial.Dimension.GEOGRAPHY;
          if (geoDataType.equals(Point.class)) {
            return readGeoPointValue(name, dimension, topNode, srid);
          } else if (geoDataType.equals(MultiPoint.class)) {
            return new MultiPoint(dimension, srid,
              readGeoPointValues(name, dimension, 0, false, topNode));
          } else if (geoDataType.equals(LineString.class)) {
            // Although a line string with less than two points is not really
            // one, the OData specification says:
            // "The coordinates member of a LineString can have zero or more
            // positions".
            // Therefore the required minimal size of the points array currently
            // is zero.
            return new LineString(dimension, srid,
              readGeoPointValues(name, dimension, 0, false, topNode));
          } else if (geoDataType.equals(MultiLineString.class)) {
            final List<LineString> lines = new ArrayList<>();
            for (final JsonNode element : topNode) {
              // Line strings can be empty (see above).
              lines.add(new LineString(dimension, srid,
                readGeoPointValues(name, dimension, 0, false, element)));
            }
            return new MultiLineString(dimension, srid, lines);
          } else if (geoDataType.equals(Polygon.class)) {
            return readGeoPolygon(name, dimension, topNode, srid);
          } else if (geoDataType.equals(MultiPolygon.class)) {
            final List<Polygon> polygons = new ArrayList<>();
            for (final JsonNode element : topNode) {
              polygons.add(readGeoPolygon(name, dimension, element, null));
            }
            return new MultiPolygon(dimension, srid, polygons);
          } else if (geoDataType.equals(GeospatialCollection.class)) {
            final List<Geospatial> elements = new ArrayList<>();
            for (final JsonNode element : topNode) {
              if (element.isObject()) {
                elements.add(readPrimitiveGeoValue(name, null, (ObjectNode)element));
              } else {
                throw new DeserializerException(
                  "Invalid value '" + element + "' in property: " + name,
                  DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, name);
              }
            }
            return new GeospatialCollection(dimension, srid, elements);
          }
        }
      }
    }
    throw new DeserializerException("Invalid value '" + jsonNode + "' for property: " + name,
      DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, name);
  }

  private Object readPrimitiveValue(final String name, final EdmPrimitiveType type,
    final boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
    final boolean isUnicode, final EdmMapping mapping, final JsonNode jsonNode)
    throws DeserializerException {
    if (isValidNull(name, isNullable, jsonNode)) {
      return null;
    }
    final boolean isGeoType = type.getName().startsWith("Geo");
    if (!isGeoType) {
      checkForValueNode(name, jsonNode);
    }
    checkJsonTypeBasedOnPrimitiveType(name, type, jsonNode);
    try {
      if (isGeoType) {
        return readPrimitiveGeoValue(name, type, (ObjectNode)jsonNode);
      }
      return type.valueOfString(jsonNode.asText(), isNullable, maxLength, precision, scale,
        isUnicode, getJavaClassForPrimitiveType(mapping, type));
    } catch (final EdmPrimitiveTypeException e) {
      throw new DeserializerException(
        "Invalid value: " + jsonNode.asText() + " for property: " + name, e,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, name);
    }
  }

  private void removeAnnotations(final ObjectNode tree) throws DeserializerException {
    final List<String> toRemove = new ArrayList<>();
    final Iterator<Entry<String, JsonNode>> fieldsIterator = tree.fields();
    while (fieldsIterator.hasNext()) {
      final Map.Entry<String, JsonNode> field = fieldsIterator.next();

      if (field.getKey().contains(ODATA_CONTROL_INFORMATION_PREFIX)) {
        // Control Information is ignored for requests as per specification
        // chapter "4.5 Control Information"
        toRemove.add(field.getKey());
      } else if (field.getKey().contains(ODATA_ANNOTATION_MARKER)) {
        if (this.constants instanceof Constantsv01) {
          toRemove.add(field.getKey());
        } else {
          throw new DeserializerException(
            "Custom annotation with field name: " + field.getKey() + " not supported",
            DeserializerException.MessageKeys.NOT_IMPLEMENTED);
        }
      }
    }
    // remove here to avoid iterator issues.
    tree.remove(toRemove);
  }

  private DeserializerException wrapParseException(final IOException e) {
    if (e instanceof JsonParseException) {
      return new DeserializerException("A JsonParseException occurred.", e,
        DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } else if (e instanceof JsonMappingException) {
      return new DeserializerException("Duplicate json property detected.", e,
        DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } else {
      return new DeserializerException("An IOException occurred.", e,
        DeserializerException.MessageKeys.IO_EXCEPTION);
    }
  }
}
