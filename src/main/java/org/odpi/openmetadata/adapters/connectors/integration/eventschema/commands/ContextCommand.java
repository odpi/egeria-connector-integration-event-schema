/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the  Egeria project. */
package org.odpi.openmetadata.adapters.connectors.integration.eventschema.commands;

import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

/**
 * This is an abstract class to be implemented by the commands
 */
public abstract class ContextCommand {

    final CommandType commandType;
    final TopicIntegratorContext myContext;

    protected ContextCommand(TopicIntegratorContext myContext, CommandType actionType) {
        super();
        this.commandType = actionType;
        this.myContext = myContext;
    }

    public abstract void execute() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException;
}
