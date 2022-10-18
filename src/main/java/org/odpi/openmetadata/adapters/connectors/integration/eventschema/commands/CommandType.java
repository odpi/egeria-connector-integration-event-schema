/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.connectors.integration.eventschema.commands;

/**
 * This enum holds all the different command types that are used to trigger actions in Egeria
 */
public enum CommandType {
    CREATE_EVENT_TYPE,
    DELETE_EVENT_TYPE,
    UPDATE_EVENT_TYPE,
    CREATE_SCHEMA_ATTRIBUTE,
    DELETE_SCHEMA_ATTRIBUTE,
    UPDATE_SCHEMA_ATTRIBUTE,
}
