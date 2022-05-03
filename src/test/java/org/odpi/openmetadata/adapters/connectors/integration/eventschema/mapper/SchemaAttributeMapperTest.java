package org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

@ExtendWith(MockitoExtension.class)
public class SchemaAttributeMapperTest {

    @Mock
    TopicIntegratorContext context;

    @Test
    void testMap() {
        assertNotNull(context);
        String json = "{ \"name\": \"Id\", \"type\": \"string\", \"doc\": \"Unique customer ID.\" , \"default\": \"bla\"}";
        JsonObject ob = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(ob.isJsonObject());
        SchemaAttributeMapper mapper = new SchemaAttributeMapper(context, ob, "guid");
        mapper.mapEgeriaSchemaAttribute();
        assertEquals("Id", mapper.getName());
        assertEquals("string", mapper.getType());
        assertEquals("Unique customer ID.", mapper.getDoc());
        assertEquals("bla", mapper.getDefault());
        json = "{ \"name\": \"Id\", \"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"} , \"default\": null}";
        ob = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(ob.isJsonObject());
        mapper = new SchemaAttributeMapper(context, ob, "guid");
        mapper.mapEgeriaSchemaAttribute();
        assertEquals("Id", mapper.getName());
        assertEquals("string", mapper.getType());
        assertNull(mapper.getDoc());
        assertNull(mapper.getDefault());
    }

    @Test
    void testMapComplex() {
        assertNotNull(context);
        final String json = "{\"name\":\"accountReferenceIban\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"AccountReferenceIban\",\"fields\":[{\"name\":\"iban\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"currency\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null}]}],\"default\":null}";
        JsonObject ob = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(ob.isJsonObject());
        SchemaAttributeMapper mapper = new SchemaAttributeMapper(context, ob, "guid");
        mapper.mapEgeriaSchemaAttribute();
        assertEquals("accountReferenceIban", mapper.getName());
        assertEquals("AccountReferenceIban", mapper.getType());
        assertNull(mapper.getDoc());
        assertNull(mapper.getDefault());
    }
}
