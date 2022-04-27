/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.adapters.connectors.integration.eventschema;

import com.google.gson.JsonElement;
import org.odpi.openmetadata.accessservices.datamanager.metadataelements.EventTypeElement;
import org.odpi.openmetadata.accessservices.datamanager.properties.EventTypeProperties;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.commands.ContextCommand;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.commands.CreateEventTypeCommand;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.connection.ConnectionStrategy;
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
    static final String SEPARATOR = ".";
    protected TopicIntegratorContext context;

    // list of commands to execute
    List<ContextCommand> commands;
    /**
     * for testing
     *
     * @return a list of Context Commands that can be executed to sync Egeria with Atlas content
     */
    public List<ContextCommand> getCommands() {
        return commands;
    }

    public void setDelegateForTestOnly(ConnectionStrategy connectionStrategy) {
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
        // reset the commands to issue
        commands = new ArrayList<>();

        getSchemaRegistryContend();
    }

    protected void getSchemaRegistryContend() {
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
        createEgeriaEventType(ob, version, subject);
        if (fieldsObject != null || fieldsObject.isJsonArray()) {
            JsonArray fields = fieldsObject.getAsJsonArray();
//            for ()
        }
    }

    private void createEgeriaEventType(JsonObject jsEventType, String version, String subject) {
        String name = jsEventType.get("name").getAsString();
        String doc = jsEventType.get("doc").getAsString();
        String namespace = jsEventType.get("namespace").getAsString();
        String type = jsEventType.get("type").getAsString();
        String qualifiedName = subject + SEPARATOR + namespace + SEPARATOR + name;
        EventTypeProperties eventProperties = new EventTypeProperties();
        eventProperties.setDisplayName(name);
        eventProperties.setQualifiedName(name);
        eventProperties.setDescription(doc);
        eventProperties.setNamespace(namespace);
        eventProperties.setTypeName(type);
        eventProperties.setVersionNumber(version);
        CreateEventTypeCommand command = new CreateEventTypeCommand(context, "", eventProperties);
        commands.add(command);
    }
}
