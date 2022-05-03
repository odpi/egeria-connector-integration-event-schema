package org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.odpi.openmetadata.accessservices.datamanager.properties.SchemaAttributeProperties;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

import java.util.Iterator;

public class SchemaAttributeMapper {

    final TopicIntegratorContext context;
    final JsonObject jsEventType;
    final String parentGUID;
    final SchemaAttributeProperties schemaAttributeProperties = new SchemaAttributeProperties();
    String guid = null;

    public SchemaAttributeMapper(TopicIntegratorContext context, JsonObject jsEventType, String parentGUID) {
        this.context = context;
        this.jsEventType = jsEventType;
        this.parentGUID = parentGUID;
    }

    public String getName() {
        return jsEventType.get("name").getAsString();
    }

    public String getDoc() {
        if (jsEventType.has("doc")) {
            return jsEventType.get("doc").getAsString();
        }
        return null;
    }

    public String getType() {
        if (!jsEventType.has("type")) {
            return null;
        }
        if (jsEventType.get("type").isJsonPrimitive()) {
            return jsEventType.getAsJsonPrimitive("type").getAsString();
        }
        if (jsEventType.get("type").isJsonArray()) {
            JsonArray typeObject = jsEventType.getAsJsonArray("type");
            if (typeObject.isEmpty() || typeObject.isJsonNull()) {
                return null;
            }
            for (Iterator<JsonElement> it = typeObject.iterator(); it.hasNext(); ) {
                JsonElement typetype = it.next();
                if (typetype.isJsonObject()) {
                    JsonObject typetypeObject = (JsonObject) typetype;
                    if (typetypeObject.has("type")) {
                        return typetypeObject.get("name").getAsString();
                    }
                }

            }
        }
        if (jsEventType.get("type").isJsonObject()) {
            JsonObject typeObject = jsEventType.getAsJsonObject("type");
            if (typeObject.has("type")) {
                return typeObject.get("type").getAsString();
            }
        }
        return null;
    }

    public String getDefault() {
        if (jsEventType.has("default") && !jsEventType.get("default").isJsonNull()) {
            return jsEventType.get("default").getAsString();
        }
        return null;
    }

    public void mapEgeriaSchemaAttribute() {
        schemaAttributeProperties.setDisplayName(getName());
        schemaAttributeProperties.setDescription(getDoc());
        schemaAttributeProperties.setTypeName(getType());
        schemaAttributeProperties.setDefaultValue(getDefault());
    }

    public String createEgeriaSchemaAttribute() {
        try {
            guid = context.createSchemaAttribute(parentGUID, schemaAttributeProperties);
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        } catch (UserNotAuthorizedException e) {
            e.printStackTrace();
        } catch (PropertyServerException e) {
            e.printStackTrace();
        }
        return guid;
    }
}
