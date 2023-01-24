/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.odpi.openmetadata.accessservices.datamanager.metadataelements.EventTypeElement;
import org.odpi.openmetadata.accessservices.datamanager.metadataelements.SchemaAttributeElement;
import org.odpi.openmetadata.accessservices.datamanager.properties.EnumSchemaTypeProperties;
import org.odpi.openmetadata.accessservices.datamanager.properties.LiteralSchemaTypeProperties;
import org.odpi.openmetadata.accessservices.datamanager.properties.SchemaAttributeProperties;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.exception.UnableToCreateSchemaAttributeException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.EnumSchemaType;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.LiteralSchemaType;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SchemaAttributeMapper {

    public static final String SEPARATOR = "~";
    public static final String TYPE = "type";
    public static final String DEFAULT = "default";
    public static final String NAME = "name";
    public static final String DOC = "doc";
    public static final String FIELDS = "fields";
    final TopicIntegratorContext context;
    final JsonObject jsObject;
    final String parentGUID;

    final String parentClass;
    final SchemaAttributeProperties schemaAttributeProperties = new SchemaAttributeProperties();
    String guid = null;

    public List<SchemaAttributeMapper> getChildSchemaAttributes() {
        return childSchemaAttributes;
    }

    List<SchemaAttributeMapper> childSchemaAttributes = new ArrayList<>();

    public SchemaAttributeMapper(TopicIntegratorContext context, JsonObject jsObject, String parentGUID, String parentClass) {
        this.context = context;
        this.jsObject = jsObject;
        this.parentGUID = parentGUID;
        this.parentClass = parentClass;
    }

    public String getName() {
        return jsObject.get(NAME).getAsString();
    }

    public String getDoc() {
        if (jsObject.has(DOC)) {
            return jsObject.get(DOC).getAsString();
        }
        return null;
    }

    public String getType() {
        if (!jsObject.has(TYPE)) {
            return null;
        }
        if (jsObject.get(TYPE).isJsonPrimitive()) {
            return jsObject.getAsJsonPrimitive(TYPE).getAsString();
        }
        if (jsObject.get(TYPE).isJsonArray()) {
            JsonArray typeObject = jsObject.getAsJsonArray(TYPE);
            if (typeObject.isEmpty() || typeObject.isJsonNull()) {
                return null;
            }
            for (JsonElement typetype : typeObject) {
                if (typetype.isJsonObject()) {
                    var typetypeObject = (JsonObject) typetype;
                    if (typetypeObject.has(NAME)) {
                        return typetypeObject.get(NAME).getAsString();
                    }
                    if (typetypeObject.has(TYPE)) {
                        return typetypeObject.get(TYPE).getAsString();
                    }
                }
            }
        }
        if (jsObject.get(TYPE).isJsonObject()) {
            JsonObject typeObject = jsObject.getAsJsonObject(TYPE);
            if (typeObject.has(NAME)) {
                return typeObject.get(NAME).getAsString();
            }
            if (typeObject.has(TYPE)) {
                return typeObject.get(TYPE).getAsString();
            }
        }
        return null;
    }

    public String getDefault() {
        if (jsObject.has(DEFAULT) && !jsObject.get(DEFAULT).isJsonNull() && !jsObject.get(DEFAULT).isJsonArray()) {
            return jsObject.get(DEFAULT).getAsString();
        }
        return null;
    }

    public boolean isNullable() {
        if (jsObject.get(TYPE).isJsonPrimitive()) {
            return false;
        }
        if (jsObject.get(TYPE).isJsonArray()) {
            JsonArray typeObject = jsObject.getAsJsonArray(TYPE);
            if (typeObject.isEmpty() || typeObject.isJsonNull()) {
                return false;
            }
            for (JsonElement typetype : typeObject) {
                if (typetype.isJsonPrimitive()) {
                    return typetype.getAsString().equals("null");
                }
            }
        }
        return false;
    }

    public List<JsonObject> getChildren() {
        List<JsonObject> children = new ArrayList<>();
        JsonElement fields = null;
        if (!jsObject.has(TYPE) || jsObject.get(TYPE).isJsonPrimitive()) {
            return children;
        }
        if (jsObject.get(TYPE).isJsonArray()) {
            JsonArray typeObject = jsObject.getAsJsonArray(TYPE);
            if (typeObject.isEmpty() || typeObject.isJsonNull()) {
                return children;
            }
            for (JsonElement typetype : typeObject) {
                if (typetype.isJsonObject()) {
                    var typetypeObject = (JsonObject) typetype;
                    if (typetypeObject.has(FIELDS)) {
                        fields = typetypeObject.get(FIELDS);
                    }
                }
            }
        }
        if (jsObject.get(TYPE).isJsonObject()) {
            JsonObject typeObject = jsObject.getAsJsonObject(TYPE);
            if (typeObject.has(FIELDS)) {
                fields = typeObject.get(FIELDS);
            }
//            if (typeObject.has(TYPE) && typeObject.has(NAME)) {
//                children.add(typeObject);
//            }
        }
        if (fields != null && fields.isJsonArray()) {
            var fieldsObject = (JsonArray) fields;
            for (JsonElement field : fieldsObject) {
                if (field.isJsonObject()) {
                    children.add((JsonObject) field);
                }
            }
        }

        return children;
    }

    public void mapEgeriaSchemaAttribute() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        String parentQualifiedName = getParentQualifiedName();
        schemaAttributeProperties.setDisplayName(getName());
        schemaAttributeProperties.setDescription(getDoc());
        schemaAttributeProperties.setTypeName("EventSchemaAttribute");
        schemaAttributeProperties.setDataType(getType());
        schemaAttributeProperties.setDefaultValue(getDefault());
        schemaAttributeProperties.setIsNullable(isNullable());
        schemaAttributeProperties.setQualifiedName(parentQualifiedName.concat(SEPARATOR).concat(getName()));
    }

    /**
     * Get the qualifiedName of the parent SchemaType. This can either be a SchemaAttribute or an
     *
     * @return the qualifiedName of the parent or an empty String
     */
    private String getParentQualifiedName() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        EventTypeElement et;
        SchemaAttributeElement attr;
        if(Objects.equals(this.parentClass, EventTypeElement.class.getName())) {
            et = context.getEventTypeByGUID(parentGUID);
            return et.getProperties().getQualifiedName();
        }
        else {
            attr = context.getSchemaAttributeByGUID(parentGUID);
            return attr.getProperties().getQualifiedName();
        }
   }

    public String createEgeriaSchemaAttribute() throws UnableToCreateSchemaAttributeException {
        try {
            List<SchemaAttributeElement> existingSchemaAttribute = context.getSchemaAttributesByName(schemaAttributeProperties.getQualifiedName(), "SchemaAttribute", 0, 0);
            if(existingSchemaAttribute == null || existingSchemaAttribute.isEmpty() ) {
                guid = context.createSchemaAttribute(parentGUID, schemaAttributeProperties);
            } else {
                guid = existingSchemaAttribute.get(0).getElementHeader().getGUID();
                context.updateSchemaAttribute(guid, true, schemaAttributeProperties);
            }
        } catch (InvalidParameterException | UserNotAuthorizedException | PropertyServerException e) {
            throw new UnableToCreateSchemaAttributeException();
        }
        return guid;
    }

    public String createEgeriaSchemaE() throws UnableToCreateSchemaAttributeException {
        var enumSchemaTypeProperties = new EnumSchemaTypeProperties();
        EnumSchemaType enumSchemaType;

        var literalSchemaTypeProperties = new LiteralSchemaTypeProperties();
        LiteralSchemaType literalSchemaType;

        try {
            guid = context.createEnumSchemaType(enumSchemaTypeProperties, "");

        } catch (InvalidParameterException | UserNotAuthorizedException | PropertyServerException e) {
            throw new UnableToCreateSchemaAttributeException();
        }
        return guid;
    }

    public void map() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException, UnableToCreateSchemaAttributeException {
        mapEgeriaSchemaAttribute();
        String guid = createEgeriaSchemaAttribute();
        for (JsonObject json : getChildren()) {
            var mapper = new SchemaAttributeMapper(context, json, guid, SchemaAttributeElement.class.getName());
            mapper.map();
            childSchemaAttributes.add(mapper);
        }
    }
}
