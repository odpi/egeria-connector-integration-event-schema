package org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.odpi.openmetadata.accessservices.datamanager.metadataelements.TopicElement;
import org.odpi.openmetadata.accessservices.datamanager.properties.EventTypeProperties;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.exception.*;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

import java.util.List;
import java.util.Optional;

public class EventTypeMapper {

    TopicIntegratorContext context;
    static final String SEPARATOR = ".";

    public EventTypeMapper(TopicIntegratorContext context) {
        this.context = context;
    }

    public String createEgeriaEventType(JsonObject jsEventType, String version, String subject) throws TopicNotFoundException {
        String name = jsEventType.get("name").getAsString();
//        String doc = jsEventType.get("doc").getAsString();
        String doc = Optional.ofNullable(jsEventType.get("doc")).map(JsonElement::getAsString).orElse("");
        String namespace = jsEventType.get("namespace").getAsString();
        String type = jsEventType.get("type").getAsString();
        String qualifiedName = subject + SEPARATOR + namespace + SEPARATOR + name;
        EventTypeProperties eventProperties = new EventTypeProperties();
        eventProperties.setDisplayName(name);
        eventProperties.setQualifiedName(qualifiedName);
        eventProperties.setDescription(doc);
        eventProperties.setNamespace(namespace);
        eventProperties.setTypeName(type);
        eventProperties.setVersionNumber(version);
        String topicName = computeTopicName(jsEventType, version, subject);
        try {
            String topicGUID = getTopicGuid(topicName);
            return context.createEventType(topicGUID, eventProperties);
        } catch (InvalidParameterException | UserNotAuthorizedException | PropertyServerException e ) {
            e.printStackTrace();
        }
        return null;
    }

    protected String getTopicGuid(String topicName) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException, TopicNotFoundException {
        //TODO: What to do if no topic is found?
        //Maybe we should use another egeria type?
        List<TopicElement> topicsByName = context.getTopicsByName(topicName, 1, 1);
        if (topicsByName != null && !topicsByName.isEmpty()) {
            return topicsByName.get(0).getElementHeader().getGUID();

        }
        throw new TopicNotFoundException(topicName);
    }

    /**
     * The topic name ist not part of the returned schema information. It has to be derived, depending on the naming strategy
     * Please override this method if another strategy is used.
     * The implemented strategy is based on the subject name. The other parameters are not used.
     */
    protected String computeTopicName(JsonObject jsEventType, String version, String subject) {
        if (subject == null || !subject.contains("-")) {
            return subject;
        }
//        return subject.substring(0, subject.indexOf('-'));
        return subject.substring(0, subject.lastIndexOf('-'));
    }
}
