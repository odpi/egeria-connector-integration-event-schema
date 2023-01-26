/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the  Egeria project. */

package org.odpi.openmetadata.adapters.connectors.integration.eventschema.connection;

import java.util.List;

/**
 * This interface allows to implement a connection to a schema registry that provides
 * information about event schemas.
 */
public interface ConnectionStrategy {

    List<String> listAllSubjects();

    List<String> getVersionsOfSubject(String subject);

    String getSchema(String subject, String version);
}
