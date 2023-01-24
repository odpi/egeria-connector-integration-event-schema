/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.adapters.connectors.integration.eventschema.connection;

import java.util.List;

/**
 * This interface allows to implement a connection to an external system that would provide
 * information about Atlas entities.
 */
public interface ConnectionStrategy {

    List<String> listAllSubjects();

    List<String> getVersionsOfSubject(String subject);

    String getSchema(String subject, String version);
}
