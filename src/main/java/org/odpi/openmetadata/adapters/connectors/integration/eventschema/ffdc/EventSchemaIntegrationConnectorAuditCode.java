/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright © 2021 Atruvia AG <opensource@atruvia.de> as contributor to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.connectors.integration.eventschema.ffdc;

import org.odpi.openmetadata.frameworks.auditlog.messagesets.AuditLogMessageDefinition;
import org.odpi.openmetadata.frameworks.auditlog.messagesets.AuditLogMessageSet;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLogRecordSeverity;


/**
 * The EventSchemaIntegrationConnectorAuditCode is used to define the message content for the OMRS Audit Log.
 * <p>
 * The 5 fields in the enum are:
 * <ul>
 *     <li>Log Message Id - to uniquely identify the message</li>
 *     <li>Severity - is this an event, decision, action, error or exception</li>
 *     <li>Log Message Text - includes placeholder to allow additional values to be captured</li>
 *     <li>Additional Information - further parameters and data relating to the audit message (optional)</li>
 *     <li>SystemAction - describes the result of the situation</li>
 *     <li>UserAction - describes how a user should correct the situation</li>
 * </ul>
 */
public enum EventSchemaIntegrationConnectorAuditCode implements AuditLogMessageSet {
    CONNECTOR_CONFIGURATION("EVENT-SCHEMA-INTEGRATION-CONNECTOR-0001",
            OMRSAuditLogRecordSeverity.INFO,
            "The {0} integration connector successfully has been initialized to monitor schema registry at URL {1}",
            "The connector is designed to monitor changes to the schemas managed by the registry.",
            "No specific action is required.  This message is to confirm the configuration for the integration connector."),

    NO_CONNECTION_PROPERTIES("EVENT-SCHEMA-INTEGRATION-CONNECTOR-0002",
            OMRSAuditLogRecordSeverity.ERROR,
            "The {0} integration connector has been initialized to monitor a schema registry but there is no configuration properties supplied. Please provide an endpoint address.",
            "The connector is designed to monitor changes to the topics managed by the event broker.",
            "No specific action is required.  This message is to confirm the configuration for the integration connector."),

    NO_TOPIC_FOUND("EVENT-SCHEMA-INTEGRATION-CONNECTOR-0003",
            OMRSAuditLogRecordSeverity.INFO,
            "The {0} integration connector has tried to find a topic for schema {1}. No topic was found, so the schema is not imported into Egeria.",
            "Every EgeriaEventType needs a Topic to be persisted. If no topic is found, the schema can not be persisted.",
            "No specific action is required. "),
    UNABLE_TO_PARSE_SCHEMA("EVENT-SCHEMA-INTEGRATION-CONNECTOR-0004",
            OMRSAuditLogRecordSeverity.EXCEPTION,
            "The {0} integration connector has tried to parse the schema of subject {1}. No JSON Object was found.",
            "It may be that the JSON is an array of schemas. This is currently not supported. The subject will be skipped.",
            "No specific action is required.  This message is to confirm the configuration for the integration connector."),
    UNABLE_TO_MAP_SCHEMA("EVENT-SCHEMA-INTEGRATION-CONNECTOR-0005",
            OMRSAuditLogRecordSeverity.EXCEPTION,
            "The {0} integration connector was unable to map the schema of subject {1}.",
            "Either the parent of the schema is not available or the user is not authorized.",
            "Ensure that all Topics have been inserted into Egeria before inserting EventTypes. " +
                    "Ensure that the user has access to all relevant Topics and EventTypes")
    ;

    private final String logMessageId;
    private final OMRSAuditLogRecordSeverity severity;
    private final String logMessage;
    private final String systemAction;
    private final String userAction;


    /**
     * The constructor for EventSchemaIntegrationConnectorAuditCode expects to be passed one of the enumeration rows defined in
     * EventSchemaIntegrationConnectorAuditCode above.   For example:
     * <p>
     * EventSchemaIntegrationConnectorAuditCode   auditCode = EventSchemaIntegrationConnectorAuditCode.SERVER_NOT_AVAILABLE;
     * <p>
     * This will expand out to the 4 parameters shown below.
     *
     * @param messageId    - unique Id for the message
     * @param severity     - severity of the message
     * @param message      - text for the message
     * @param systemAction - description of the action taken by the system when the condition happened
     * @param userAction   - instructions for resolving the situation, if any
     */
    EventSchemaIntegrationConnectorAuditCode(String messageId,
                                             OMRSAuditLogRecordSeverity severity,
                                             String message,
                                             String systemAction,
                                             String userAction) {
        this.logMessageId = messageId;
        this.severity = severity;
        this.logMessage = message;
        this.systemAction = systemAction;
        this.userAction = userAction;
    }


    /**
     * Retrieve a message definition object for logging.  This method is used when there are no message inserts.
     *
     * @return message definition object.
     */
    @Override
    public AuditLogMessageDefinition getMessageDefinition() {
        return new AuditLogMessageDefinition(logMessageId,
                severity,
                logMessage,
                systemAction,
                userAction);
    }


    /**
     * Retrieve a message definition object for logging.  This method is used when there are values to be inserted into the message.
     *
     * @param params array of parameters (all strings).  They are inserted into the message according to the numbering in the message text.
     * @return message definition object.
     */
    @Override
    public AuditLogMessageDefinition getMessageDefinition(String... params) {
        AuditLogMessageDefinition messageDefinition = new AuditLogMessageDefinition(logMessageId,
                severity,
                logMessage,
                systemAction,
                userAction);
        messageDefinition.setMessageParameters(params);
        return messageDefinition;
    }


    /**
     * JSON-style toString
     *
     * @return string of property names and values for this enum
     */
    @Override
    public String toString() {
        return "StrimziIntegrationConnectorAuditCode{" +
                "logMessageId='" + logMessageId + '\'' +
                ", severity=" + severity +
                ", logMessage='" + logMessage + '\'' +
                ", systemAction='" + systemAction + '\'' +
                ", userAction='" + userAction + '\'' +
                '}';
    }
}
