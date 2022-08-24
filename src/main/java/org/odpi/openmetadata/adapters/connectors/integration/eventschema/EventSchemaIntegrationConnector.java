/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.adapters.connectors.integration.eventschema;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.connection.ConfluentRestCalls;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.connection.ConnectionStrategy;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.exception.TopicNotFoundException;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.ffdc.EventSchemaIntegrationConnectorAuditCode;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.ffdc.EventSchemaIntegrationConnectorErrorCode;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper.EventTypeMapper;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper.SchemaAttributeMapper;
import org.odpi.openmetadata.frameworks.auditlog.messagesets.ExceptionMessageDefinition;
import org.odpi.openmetadata.frameworks.connectors.ffdc.ConnectorCheckedException;
import org.odpi.openmetadata.frameworks.connectors.properties.EndpointProperties;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorConnector;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSchemaIntegrationConnector extends TopicIntegratorConnector {

    protected ConnectionStrategy delegate = null;
    protected Map<String, List<String>> subjectCache = new HashMap<>();
    protected TopicIntegratorContext context;
    protected EventTypeMapper eventTypeMapper;
    protected SchemaAttributeMapper schemaAttributeMapper;
    protected ConnectionStrategy callDelegate;
    String targetURL = null;

//    @Override
//    public void setContext(TopicIntegratorContext context) {
//        super.setContext(context);
//        this.context = context;
//    }

    void setDelegateForTestOnly(ConnectionStrategy connectionStrategy) {
        delegate = connectionStrategy;
    }

    @Override
    public void start() throws ConnectorCheckedException {
        super.start();
        subjectCache = new HashMap<>();
        //TODO: Initialise the cache with information from Egeria, iff possible

        final String methodName = "start";

        context = this.getContext();

        if (connectionProperties != null) {
            EndpointProperties endpoint = connectionProperties.getEndpoint();

            if (endpoint != null) {
                targetURL = endpoint.getAddress();
                try {
                    new URI(targetURL);
//                    callDelegate = new ConfluentRestCalls(targetURL);
                    delegate = new ConfluentRestCalls(targetURL);
                } catch (URISyntaxException e) {
                    ExceptionMessageDefinition messageDefinition =
                            EventSchemaIntegrationConnectorErrorCode.INVALID_URL_IN_CONFIGURATION.getMessageDefinition(targetURL);
                    throw new ConnectorCheckedException(messageDefinition,
                            this.getClass().getName(),
                            methodName);
                }
            }
            Map<String, Object> configurationProperties = connectionProperties.getConfigurationProperties();
            /*
             * Record the configuration
             */

            if (auditLog != null) {
                // do not record the token in the log which could be sensitive
                auditLog.logMessage(methodName,
                        EventSchemaIntegrationConnectorAuditCode.CONNECTOR_CONFIGURATION.getMessageDefinition(connectorName,
                                targetURL
                        ));
            }
        } else {
            if (auditLog != null) {
                auditLog.logMessage(methodName,
                        EventSchemaIntegrationConnectorAuditCode.NO_CONNECTION_PROPERTIES.getMessageDefinition(connectorName,
                                targetURL));
            }
            ExceptionMessageDefinition messageDefinition =
                    EventSchemaIntegrationConnectorErrorCode.NO_CONNECTION_CONFIGURATION.getMessageDefinition(connectorName);
            throw new ConnectorCheckedException(messageDefinition,
                    this.getClass().getName(),
                    methodName);
        }
        context = getContext();
    }


    @Override
    public void refresh() throws ConnectorCheckedException {
        getSchemaRegistryContent();
    }

    protected void getSchemaRegistryContent() throws ConnectorCheckedException {
//        eventTypeMapper = new EventTypeMapper(context);
        eventTypeMapper = new EventTypeMapper(this.getContext());
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
            for (String version : versions) {
                if (!currentVersions.contains(version)) {
                    String new_schema = delegate.getSchema(subject, version);
                    addSchema(new_schema, version, subject);
                    currentVersions.add(version);
                } //else -> everything is fine. Version of schema is already in egeria
            }
        }
    }

    private void addSchema(String schema, String version, String subject) {
        //TODO: if schema is an array, this will throw an  IllegalStateExcpetion
        JsonElement schemaTree = JsonParser.parseString(schema);
        if( schemaTree.isJsonArray()) {
            if (auditLog != null) {
                auditLog.logMessage("addSchema",
                        EventSchemaIntegrationConnectorAuditCode.UNABLE_TO_PARSE_SCHEMA.getMessageDefinition(connectorName,
                                subject));
            }
            return;
        }
        JsonObject ob =schemaTree.getAsJsonObject();
        JsonElement fieldsObject = ob.get("fields");
        String guid = null;

        //TODO: replace with audit log
        try {
            guid = eventTypeMapper.createEgeriaEventType(ob, version, subject);
        } catch (TopicNotFoundException e) {
            if (auditLog != null) {
                auditLog.logMessage("addSchema",
                        EventSchemaIntegrationConnectorAuditCode.NO_TOPIC_FOUND.getMessageDefinition(connectorName,
                                subject));
            }
        }
        if (fieldsObject != null && fieldsObject.isJsonArray()) {
            JsonArray fields = fieldsObject.getAsJsonArray();
            for (JsonElement field : fields) {
                if (field.isJsonObject()) {
                    schemaAttributeMapper = new SchemaAttributeMapper(context, (JsonObject) field, guid);
                    //TODO
                    schemaAttributeMapper.mapEgeriaSchemaAttribute();
                }

            }
        }
    }

}
