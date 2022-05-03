/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.adapters.connectors.integration.eventschema;

import com.google.gson.JsonElement;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.connection.ConnectionStrategy;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper.EventTypeMapper;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper.SchemaAttributeMapper;
import org.odpi.openmetadata.frameworks.connectors.ffdc.ConnectorCheckedException;

import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorConnector;

import java.util.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

public class EventSchemaIntegrationConnector extends TopicIntegratorConnector {

    protected ConnectionStrategy delegate= null;
    protected Map<String, List<String>> subjectCache = new HashMap<>();

    @Override
    public void setContext(TopicIntegratorContext context) {
        this.context = context;
    }

    protected TopicIntegratorContext context;
    protected EventTypeMapper eventTypeMapper;
    protected SchemaAttributeMapper schemaAttributeMapper;


    void setDelegateForTestOnly(ConnectionStrategy connectionStrategy) {
        delegate = connectionStrategy;
    }

    @Override
    public void start() throws ConnectorCheckedException {
        super.start();
        subjectCache = new HashMap<>();
        //TODO: Initialise the cache with information from Egeria, iff possible

        context = getContext();
    }

    @Override
    public void refresh() throws ConnectorCheckedException {
        getSchemaRegistryContend();
    }

    protected void getSchemaRegistryContend() {
        eventTypeMapper = new EventTypeMapper(context);
        List<String> subjects = delegate.listAllSubjects();

        for (String subject : subjects) {
            //Start caching stuff
            List<String> currentVersions;
            if (!subjectCache.containsKey(subject)) {
                currentVersions = new ArrayList<>();
                subjectCache.put(subject, currentVersions);
            } else {
                currentVersions = subjectCache.get(subject);
            }
            //end caching stuff

            List<String> versions = delegate.getVersionsOfSubject(subject);
            for (String version: versions) {
                if (!currentVersions.contains(version)) {
                    String new_schema = delegate.getSchema(subject, version);
                    addSchema(new_schema, version, subject);
                    currentVersions.add(version);
                } //else -> everything is fine. Version of schema is already in egeria
            }
        }
    }

    private void addSchema(String schema, String version, String subject) {
        JsonObject ob = JsonParser.parseString(schema).getAsJsonObject();
        JsonElement fieldsObject = ob.get("fields");
        String guid = eventTypeMapper.createEgeriaEventType(ob, version, subject);
        if (fieldsObject != null || fieldsObject.isJsonArray()) {
            JsonArray fields = fieldsObject.getAsJsonArray();
            for (Iterator<JsonElement> it = fields.iterator(); it.hasNext(); ) {
                JsonElement field = it.next();
                if (field.isJsonObject()) {
                    schemaAttributeMapper = new SchemaAttributeMapper(context, (JsonObject) field, guid);
                    //TODO
                    schemaAttributeMapper.mapEgeriaSchemaAttribute();
                }

            }
        }
    }

}
