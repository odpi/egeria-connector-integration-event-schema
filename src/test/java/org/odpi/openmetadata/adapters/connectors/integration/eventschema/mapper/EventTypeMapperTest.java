package org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EventTypeMapperTest {

    @Test
    void testComputeTopicName() {
        EventTypeMapper eventTypeMapper = new EventTypeMapper(null);
        String topicName = eventTypeMapper.computeTopicName(null, null,"ab.cd.ef-key");
        assertNotNull(topicName);
        assertEquals("ab.cd.ef", topicName);

        topicName = eventTypeMapper.computeTopicName(null, null,"ab.cd.ef.key");
        assertNotNull(topicName);
        assertEquals("ab.cd.ef.key", topicName);

        topicName = eventTypeMapper.computeTopicName(null, null,null);
        assertNull(topicName);
    }
}
