/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.adapters.connectors.integration.eventschema;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.ElementHeader;
import org.odpi.openmetadata.accessservices.datamanager.metadataelements.EventTypeElement;
import org.odpi.openmetadata.accessservices.datamanager.metadataelements.TopicElement;
import org.odpi.openmetadata.accessservices.datamanager.properties.EventTypeProperties;
import org.odpi.openmetadata.accessservices.datamanager.properties.SchemaAttributeProperties;
import org.odpi.openmetadata.accessservices.datamanager.properties.TopicProperties;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.connection.ConnectionStrategy;
import org.odpi.openmetadata.frameworks.connectors.ffdc.ConnectorCheckedException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConfluentConnectorTest {

    static class TestConnectionStrategy implements ConnectionStrategy {

        final static String SUBJECT_1 = "test.subject";
        final static String SUBJECT_2 = "test.subject2";

        @Override
        public List<String> listAllSubjects() {
            List<String> testMock = new ArrayList<>();
            testMock.add(SUBJECT_1);
            testMock.add(SUBJECT_2);
            return testMock;
        }

        @Override
        public List<String> getVersionsOfSubject(String subject) {
            List<String> testMock = new ArrayList<>();
            testMock.add("1");
            return testMock;
        }

        @Override
        public String getSchema(String subject, String version) {
            if (subject.equals(SUBJECT_1)) {
                return " { \"type\": \"record\", \"name\": \"testValue\", \"doc\": \"A test subject.\", \"namespace\": \"org.egeria.test\", \"fields\": [ { \"name\": \"Id\", \"type\": \"string\", \"doc\": \"Unique customer ID.\" }, { \"name\": \"SL_RZBK\", \"type\": \"int\", \"doc\": \"Unique bank ID.\" } ] } ";
            }
            if (subject.equals(SUBJECT_2)) {
                return " { \"type\": \"record\", \"name\": \"testKey\", \"doc\": \"A test subject.\", \"namespace\": \"org.egeria.test2\", \"fields\": [ { \"name\": \"Id\", \"type\": \"string\", \"doc\": \"Unique customer ID.\" }, { \"name\": \"SL_RZBK\", \"type\": \"int\", \"doc\": \"Unique bank ID.\" } ] } ";
            }
            return "";
        }
    }


    @Mock
    TopicIntegratorContext context;

    @Mock
    EventTypeProperties ep;

    @Mock
    EventTypeElement et;

    @InjectMocks
    private EventSchemaIntegrationConnector connector;


    @BeforeEach
    void setup() {
        connector.setDelegateForTestOnly(new TestConnectionStrategy());
        connector.setContext(context);
    }

    @Test
    void testRefresh() throws ConnectorCheckedException, InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        assertNotNull(context);
        when(context.getTopicsByName("test.subject", 0, 1))
                .thenReturn(createTopic("guid.test.subject"));
        when(context.getTopicsByName("test.subject2", 0, 1))
                .thenReturn(createTopic("guid.test.subject2"));
        when( ep.getQualifiedName())
                .thenReturn("testQualifiedName");
        when(et.getProperties())
                .thenReturn(ep);
        when(context.getEventTypeByGUID(anyString()))
                .thenReturn(et);
        when(context.createSchemaAttribute(anyString(), any(SchemaAttributeProperties.class)))
                .thenReturn("guid");
        when(context.createEventType(anyString(), any(EventTypeProperties.class)))
                .thenReturn("guid");
        connector.refresh();
        EventTypeProperties eventProperties = new EventTypeProperties();
        eventProperties.setDisplayName("testValue");
        eventProperties.setQualifiedName("test.subject~org.egeria.test~testValue~1");
        eventProperties.setDescription("A test subject.");
        eventProperties.setNamespace("org.egeria.test");
//        eventProperties.setTypeName("record");
        eventProperties.setVersionNumber("1");
        verify(context).getTopicsByName("test.subject", 0,1);
        verify(context).createEventType("guid.test.subject", eventProperties);
        eventProperties.setDisplayName("testKey");
        eventProperties.setQualifiedName("test.subject2~org.egeria.test2~testKey~1");
        eventProperties.setDescription("A test subject.");
        eventProperties.setNamespace("org.egeria.test2");
//        eventProperties.setTypeName("record");
        eventProperties.setVersionNumber("1");
        verify(context).getTopicsByName("test.subject2", 0,1);
        verify(context).createEventType("guid.test.subject2", eventProperties);
    }

    public List<TopicElement> createTopic(String guid) {
        TopicElement topicElement = new TopicElement();
        ElementHeader elementHeader = new ElementHeader();
        TopicProperties props = new TopicProperties();
        props.setDisplayName("fakeDisplayName");
        elementHeader.setGUID(guid);
        topicElement.setElementHeader(elementHeader);
        topicElement.setProperties(props);
        List<TopicElement> list = new ArrayList<>();
        list.add(topicElement);
        return list;
    }

}
