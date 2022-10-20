/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright © 2021 Atruvia AG <opensource@atruvia.de> as contributor to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.connectors.integration.eventschema.commands;

import org.odpi.openmetadata.accessservices.datamanager.properties.EventTypeProperties;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

/**
 * This Command creates a new event type in Egeria
 */
public class CreateEventTypeCommand extends ContextCommand {

    private final String topicGUID;
    private final EventTypeProperties properties;

    public CreateEventTypeCommand(TopicIntegratorContext myContext,
                                  String topicGUID,
                                  EventTypeProperties properties) {
        super(myContext, CommandType.CREATE_EVENT_TYPE);
        this.topicGUID = topicGUID;
        this.properties = properties;

    }

    @Override
    public void execute() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
      String eventTypeGUID = myContext.createEventType(topicGUID, properties);
    }

}
