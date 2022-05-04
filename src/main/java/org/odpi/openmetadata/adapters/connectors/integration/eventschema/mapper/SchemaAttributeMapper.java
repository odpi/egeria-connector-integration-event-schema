package org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.odpi.openmetadata.accessservices.datamanager.properties.SchemaAttributeProperties;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

import java.util.ArrayList;
import java.util.List;

public class SchemaAttributeMapper {

    public static final String TYPE = "type";
    public static final String DEFAULT = "default";
    public static final String NAME = "name";
    public static final String DOC = "doc";
    public static final String FIELDS = "fields";
    final TopicIntegratorContext context;
    final JsonObject jsObject;
    final String parentGUID;
    final SchemaAttributeProperties schemaAttributeProperties = new SchemaAttributeProperties();
    String guid = null;

    public List<SchemaAttributeMapper> getChildSchemaAttributes() {
        return childSchemaAttributes;
    }

    List<SchemaAttributeMapper> childSchemaAttributes = new ArrayList<>();

    public SchemaAttributeMapper(TopicIntegratorContext context, JsonObject jsObject, String parentGUID) {
        this.context = context;
        this.jsObject = jsObject;
        this.parentGUID = parentGUID;
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
                    JsonObject typetypeObject = (JsonObject) typetype;
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
            if (typeObject.has(TYPE)) {
                return typeObject.get(TYPE).getAsString();
            }
        }
        return null;
    }

    public String getDefault() {
        if (jsObject.has(DEFAULT) && !jsObject.get(DEFAULT).isJsonNull()) {
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
                    return "null".equals(typetype.getAsString());
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
                    JsonObject typetypeObject = (JsonObject) typetype;
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
        }
        if (fields != null && fields.isJsonArray()) {
            JsonArray fieldsObject = (JsonArray) fields;
            for (JsonElement field : fieldsObject) {
                if (field.isJsonObject()) {
                    children.add((JsonObject) field);
                }
            }
        }

        return children;
    }

    public void mapEgeriaSchemaAttribute() {
        schemaAttributeProperties.setDisplayName(getName());
        schemaAttributeProperties.setDescription(getDoc());
        schemaAttributeProperties.setTypeName(getType());
        schemaAttributeProperties.setDefaultValue(getDefault());
        schemaAttributeProperties.setIsNullable(isNullable());
    }

    public String createEgeriaSchemaAttribute() {
        try {
            guid = context.createSchemaAttribute(parentGUID, schemaAttributeProperties);
        } catch (InvalidParameterException | UserNotAuthorizedException | PropertyServerException e) {
            e.printStackTrace();
        }
        return guid;
    }

    public void map() {
        mapEgeriaSchemaAttribute();
        String guid = createEgeriaSchemaAttribute();
        for (JsonObject json : getChildren()) {
            SchemaAttributeMapper mapper = new SchemaAttributeMapper(context, json, guid);
            mapper.map();
            childSchemaAttributes.add(mapper);
        }
    }
}
