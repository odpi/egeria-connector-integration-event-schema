/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the  Egeria project. */

package org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.exception.TopicNotFoundException;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EventTypeMapperTest {

    @Mock
    TopicIntegratorContext context;

    @InjectMocks
    EventTypeMapper eventTypeMapper;

    @Test
    void testComputeTopicName() {

        EventTypeMapper eventTypeMapper = new EventTypeMapper(context);
        String topicName = eventTypeMapper.computeTopicName(null, null,"ab.cd.ef-value");
        assertNotNull(topicName);
        assertEquals("ab.cd.ef", topicName);

        topicName = eventTypeMapper.computeTopicName(null, null,"ab.cd.ef.value");
        assertNotNull(topicName);
        assertEquals("ab.cd.ef.value", topicName);

        topicName = eventTypeMapper.computeTopicName(null, null,"ab.cd.ef-someEventValue");
        assertNotNull(topicName);
        assertEquals("ab.cd.ef", topicName);

        topicName = eventTypeMapper.computeTopicName(null, null,"ab.cd.ef-thisispartofthetopicname-someEventValue");
        assertNotNull(topicName);
        assertEquals("ab.cd.ef-thisispartofthetopicname", topicName);

        topicName = eventTypeMapper.computeTopicName(null, null,null);
        assertNull(topicName);
    }

    // Test should throw TopicNotFoundException
    @Test
    void getTopicGuid() { //throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        final String topicNameIn = "ab.cd.ef.value";
        eventTypeMapper = new EventTypeMapper(context);

//        when(context.getTopicsByName(topicNameIn, 1, 1))
//                .thenReturn(new ArrayList<TopicElement>());
//        when(context.findTopics(topicNameIn.concat(".*"), 1, 0))
//                .thenReturn(new ArrayList<TopicElement>());

        Throwable t = Assertions.assertThrows(TopicNotFoundException.class, () -> {
            String topicName;
            topicName = eventTypeMapper.getTopicGuid(topicNameIn);
            Assertions.assertNotNull(topicName);
        });
        System.out.println(t.getLocalizedMessage());

    }
}
