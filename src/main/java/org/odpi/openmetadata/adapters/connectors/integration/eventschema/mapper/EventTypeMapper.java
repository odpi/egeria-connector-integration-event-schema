/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright © 2022 Atruvia AG <opensource@atruvia.de> as contributor to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.odpi.openmetadata.accessservices.datamanager.metadataelements.EventTypeElement;
import org.odpi.openmetadata.accessservices.datamanager.metadataelements.TopicElement;
import org.odpi.openmetadata.accessservices.datamanager.properties.EventTypeProperties;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.exception.TopicNotFoundException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;

import java.util.List;
import java.util.Optional;

public class EventTypeMapper {

    TopicIntegratorContext context;

    // The namespace of the topics which is used as a prefix in the topic name
    protected String topicNamespace = "";
    static final String SEPARATOR = "~";

    public EventTypeMapper(TopicIntegratorContext context) {
        this.context = context;
    }

    public EventTypeMapper(TopicIntegratorContext context, String topicNamespace) {
        this(context);
        this.topicNamespace = topicNamespace;
    }
    public String createEgeriaEventType(JsonObject jsEventType, String version, String subject) throws TopicNotFoundException, InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        String guid = null;
        String name = jsEventType.get("name").getAsString();
        String doc = Optional.ofNullable(jsEventType.get("doc")).map(JsonElement::getAsString).orElse("");
        String namespace = jsEventType.get("namespace").getAsString();
        String qualifiedName = subject.concat(SEPARATOR).concat(namespace).concat(SEPARATOR).concat(name).concat(SEPARATOR).concat(version);
        EventTypeProperties eventProperties = new EventTypeProperties();
        eventProperties.setDisplayName(name);
        eventProperties.setQualifiedName(qualifiedName);
        eventProperties.setDescription(doc);
        eventProperties.setNamespace(namespace);
        eventProperties.setVersionNumber(version);
        String topicName = computeTopicName(jsEventType, version, subject);
        String topicGUID = getTopicGuid(topicName);
        List<EventTypeElement> existingEventTypes = context.getEventTypesByName(qualifiedName,0,0);
        if( existingEventTypes == null || existingEventTypes.isEmpty() ) {
            guid =  context.createEventType(topicGUID, eventProperties);
        } else {
            if( existingEventTypes.get(0) != null && existingEventTypes.get(0).getElementHeader() != null ) {
                guid =  existingEventTypes.get(0).getElementHeader().getGUID();
                context.updateEventType(guid, true, eventProperties);
            }
        }
        return guid;
    }

    protected String getTopicGuid(String topicName) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException, TopicNotFoundException {
        //TODO: What to do if no topic is found?
        //Maybe we should use another egeria type?
        // see above. get the GUID from the map which we initialize in the constructor
        List<TopicElement> topicsByName = context.getTopicsByName(topicName, 0, 1);
//        List<TopicElement> topicsByName = context.findTopics(topicName.concat(".*"), 1, 1);
        if (topicsByName != null && !topicsByName.isEmpty()) {
            return topicsByName.get(0).getElementHeader().getGUID();

        }
//        if( this.topicMap.containsKey(topicName) ) {
//            return topicMap.get(topicName);
//        }
        else {
            throw new TopicNotFoundException(topicName);
        }
    }

    /**
     * The topic name ist not part of the returned schema information. It has to be derived, depending on the naming strategy
     * Please override this method if another strategy is used.
     * The implemented strategy is based on the subject name. The other parameters are not used.
     *
     * @param jsEventType   JSON containing the event type
     * @param version       Version of the schema
     * @param subject       Subject in Schema Registry
     * @return  the extracted topic name or if none is found the original subject
     */
    protected String computeTopicName(JsonObject jsEventType, String version, String subject) {
        String topicName;
        if (subject == null || !subject.contains("-")) {
            topicName = subject;
        } else {
            topicName = subject.substring(0, subject.lastIndexOf('-'));
        }

        if( !this.topicNamespace.equals("")) {
            topicName = topicNamespace.concat(".").concat(topicName);
        }
        return topicName;
    }
}
