/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the  Egeria project. */

package org.odpi.openmetadata.adapters.connectors.integration.eventschema;

import org.odpi.openmetadata.frameworks.connectors.ConnectorProviderBase;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.ConnectorType;

import java.util.ArrayList;
import java.util.List;


/**
 * EventSchemaIntegrationProvider is the connector provider for the integration connector
 * that processes schemas from a Confluent (c) Schema Registry
 */
public class EventSchemaIntegrationProvider extends ConnectorProviderBase {

    private static final String connectorTypeGUID = "d64cbdb1-69bf-4da1-93cc-a8ba99b41428";
    private static final String connectorTypeQualifiedName = "Event Schema Integration Connector";
    private static final String connectorTypeDisplayName = "Event Schema Integration Connector";
    private static final String connectorTypeDescription = "Connector maintains schema information associated with the topic.";

    /**
     * Constructor used to initialize the ConnectorProvider with the Java class name of the specific
     * store implementation.
     */
    public EventSchemaIntegrationProvider() {
        super();

        super.setConnectorClassName(EventSchemaIntegrationConnector.class.getName());

        var connectorType = new ConnectorType();
        connectorType.setType(ConnectorType.getConnectorTypeType());
        connectorType.setGUID(connectorTypeGUID);
        connectorType.setQualifiedName(connectorTypeQualifiedName);
        connectorType.setDisplayName(connectorTypeDisplayName);
        connectorType.setDescription(connectorTypeDescription);
        connectorType.setConnectorProviderClassName(this.getClass().getName());

        List<String> recognizedConfigurationProperties = new ArrayList<>();
        recognizedConfigurationProperties.add("topicNamespace");
        connectorType.setRecognizedConfigurationProperties(recognizedConfigurationProperties);

        super.connectorTypeBean = connectorType;
    }
}
