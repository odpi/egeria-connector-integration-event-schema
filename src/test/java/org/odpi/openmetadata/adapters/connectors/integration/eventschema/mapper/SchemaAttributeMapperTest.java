package org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.odpi.openmetadata.accessservices.datamanager.metadataelements.EventTypeElement;
import org.odpi.openmetadata.accessservices.datamanager.metadataelements.SchemaAttributeElement;
import org.odpi.openmetadata.accessservices.datamanager.properties.EventTypeProperties;
import org.odpi.openmetadata.accessservices.datamanager.properties.SchemaAttributeProperties;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SchemaAttributeMapperTest {

    @Mock
    TopicIntegratorContext context;

    @Mock
    EventTypeElement et;
    @Mock
    EventTypeProperties ep;
    @Mock
    SchemaAttributeElement at;
    @Mock
    SchemaAttributeProperties ap;

    @Test
    void testMap() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        assertNotNull(context);
        when( ep.getQualifiedName())
                .thenReturn("testQualifiedName");
        when(et.getProperties())
                .thenReturn(ep);
        when(context.getEventTypeByGUID(anyString()))
                .thenReturn(et);
        String json = "{ \"name\": \"Id\", \"type\": \"string\", \"doc\": \"Unique customer ID.\" , \"default\": \"bla\"}";
        JsonObject ob = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(ob.isJsonObject());
        SchemaAttributeMapper mapper = new SchemaAttributeMapper(context, ob, "guid", EventTypeElement.class.getName());
        mapper.mapEgeriaSchemaAttribute();
        assertEquals("Id", mapper.getName());
        assertEquals("string", mapper.getType());
        assertEquals("Unique customer ID.", mapper.getDoc());
        assertEquals("bla", mapper.getDefault());
        json = "{ \"name\": \"Id\", \"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"} , \"default\": null}";
        ob = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(ob.isJsonObject());
        mapper = new SchemaAttributeMapper(context, ob, "guid", EventTypeElement.class.getName());
        mapper.mapEgeriaSchemaAttribute();
        assertEquals("Id", mapper.getName());
        assertEquals("string", mapper.getType());
        assertNull(mapper.getDoc());
        assertNull(mapper.getDefault());
        assertFalse(mapper.isNullable());
        json = "{ \"name\": \"Id\", \"type\":[\"null\", {\"type\":\"string\",\"avro.java.string\":\"String\"}]}";
        ob = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(ob.isJsonObject());
        mapper = new SchemaAttributeMapper(context, ob, "guid", EventTypeElement.class.getName());
        mapper.mapEgeriaSchemaAttribute();
        assertEquals("Id", mapper.getName());
        assertEquals("string", mapper.getType());
        assertNull(mapper.getDoc());
        assertNull(mapper.getDefault());
        assertTrue(mapper.isNullable());
    }

    @Test
    void testMapComplex() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        assertNotNull(context);
        when(context.createSchemaAttribute(anyString(), any(SchemaAttributeProperties.class)))
                .thenReturn("anyGuid");
        when( ep.getQualifiedName())
                .thenReturn("testQualifiedName");
        when(et.getProperties())
                .thenReturn(ep);
        when(context.getEventTypeByGUID(anyString()))
                .thenReturn(et);
        when( ap.getQualifiedName())
                .thenReturn("testQualifiedName");
        when(at.getProperties())
                .thenReturn(ap);
        when(context.getSchemaAttributeByGUID(anyString()))
                .thenReturn(at);
        final String json = "{\"name\":\"accountReferenceIban\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"AccountReferenceIban\",\"fields\":[{\"name\":\"iban\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"currency\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":\"Foo\"}]}],\"default\":null}";
        JsonObject ob = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(ob.isJsonObject());
        SchemaAttributeMapper mapper = new SchemaAttributeMapper(context, ob, "guid", EventTypeElement.class.getName());
        assertEquals("accountReferenceIban", mapper.getName());
        assertEquals("AccountReferenceIban", mapper.getType());
        assertNull(mapper.getDoc());
        assertNull(mapper.getDefault());
        assertTrue(mapper.isNullable());
        List<JsonObject> children = mapper.getChildren();
        assertNotNull(children);
        assertEquals(2, children.size());

        mapper.map();
        assertEquals(2, mapper.getChildSchemaAttributes().size());
        SchemaAttributeMapper child1 = mapper.getChildSchemaAttributes().get(0);
        assertTrue(child1.getChildren().isEmpty());
        assertEquals("iban", child1.getName());
        assertEquals("string", child1.getType());
        assertTrue(child1.isNullable());
        assertNull(child1.getDefault());

        SchemaAttributeMapper child2 = mapper.getChildSchemaAttributes().get(1);
        assertTrue(child2.getChildren().isEmpty());
        assertEquals("currency", child2.getName());
        assertEquals("string", child2.getType());
        assertFalse(child2.isNullable());
        assertEquals("Foo", child2.getDefault());
    }

    @Test
    void testMapEnumeration() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        when(context.createSchemaAttribute(anyString(), any(SchemaAttributeProperties.class)))
                .thenReturn("anyGuid");
        when( ep.getQualifiedName())
                .thenReturn("testQualifiedName");
        when(et.getProperties())
                .thenReturn(ep);
        when(context.getEventTypeByGUID(anyString()))
                .thenReturn(et);
        assertNotNull(context);
        final String json = "{\"name\":\"testEnum\",\"type\":{\"type\":\"enum\",\"name\":\"TestEnum\",\"doc\":\"Documentation of Enumeration.\",\"symbols\":[\"ONE\",\"TWO\",\"THREE\",\"FOUR\"]},\"doc\":\"Documentation of attribute\"}";
        JsonObject ob = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(ob.isJsonObject());
        SchemaAttributeMapper mapper = new SchemaAttributeMapper(context, ob, "guid", EventTypeElement.class.getName());
        assertEquals("testEnum", mapper.getName());
        assertEquals("TestEnum", mapper.getType());
        assertEquals("Documentation of attribute", mapper.getDoc());
        assertNull(mapper.getDefault());
        assertFalse(mapper.isNullable());
        List<JsonObject> children = mapper.getChildren();
        assertNotNull(children);
        assertEquals(0, children.size());

        mapper.map();
//        assertEquals(0, mapper.getChildSchemaAttributes().size());
//        SchemaAttributeMapper child1 = mapper.getChildSchemaAttributes().get(0);
//        assertTrue(child1.getChildren().isEmpty());
//        assertEquals("TestEnum", child1.getName());
//        assertEquals("enum", child1.getType());
//        assertEquals("Documentation of Enumeration.", child1.getDoc());
//        assertFalse(child1.isNullable());
//        assertNull(child1.getDefault());
    }
}
