/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.adapters.connectors.integration.eventschema.exception;

public class TopicNotFoundException extends Exception {
    public TopicNotFoundException(String topicName) {
        super(String.format("Event Type cannot be mapped to a topic. Topic %s is unknown.", topicName));
    }
}
