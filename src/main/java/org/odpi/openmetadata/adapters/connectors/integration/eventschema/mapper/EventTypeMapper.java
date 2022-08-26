package org.odpi.openmetadata.adapters.connectors.integration.eventschema.mapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.odpi.openmetadata.accessservices.datamanager.metadataelements.TopicElement;
import org.odpi.openmetadata.accessservices.datamanager.properties.EventTypeProperties;
import org.odpi.openmetadata.adapters.connectors.integration.eventschema.exception.TopicNotFoundException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.integrationservices.topic.connector.TopicIntegratorContext;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class EventTypeMapper {

    TopicIntegratorContext context;
    static final String SEPARATOR = "§";

    HashMap<String,String> topicMap = new HashMap<>();

    public EventTypeMapper(TopicIntegratorContext context) {
        this.context = context;
        // This is a workaround due to the issue https://github.com/odpi/egeria-connector-xtdb/issues/399
        try {
            List<TopicElement> topics = this.context.findTopics(".*", 1, 0);
            for( TopicElement topic : topics) {
                this.topicMap.put(topic.getProperties().getDisplayName(), topic.getElementHeader().getGUID() );
            }
        } catch (InvalidParameterException | UserNotAuthorizedException | PropertyServerException e ) {
            //TODO write audit log
            e.printStackTrace();
        }
    }

    public String createEgeriaEventType(JsonObject jsEventType, String version, String subject) throws TopicNotFoundException {
        String name = jsEventType.get("name").getAsString();
        String doc = Optional.ofNullable(jsEventType.get("doc")).map(JsonElement::getAsString).orElse("");
        String namespace = jsEventType.get("namespace").getAsString();
        String type = jsEventType.get("type").getAsString();
        String qualifiedName = subject.concat(SEPARATOR).concat(namespace).concat(SEPARATOR).concat(name).concat(SEPARATOR).concat(version);
        EventTypeProperties eventProperties = new EventTypeProperties();
        eventProperties.setDisplayName(name);
        eventProperties.setQualifiedName(qualifiedName);
        eventProperties.setDescription(doc);
        eventProperties.setNamespace(namespace);
//        eventProperties.setTypeName(type); // Type is alway EventType and does not have to be set
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
        // see above. get the GUID from the map which we initialize in the constructor
//        List<TopicElement> topicsByName = context.getTopicsByName(topicName, 1, 1);
//        List<TopicElement> topicsByName = context.findTopics(topicName.concat(".*"), 1, 1);
//        if (topicsByName != null && !topicsByName.isEmpty()) {
//            return topicsByName.get(0).getElementHeader().getGUID();
//
//        }
        if( this.topicMap.containsKey(topicName) ) {
            return topicMap.get(topicName);
        }
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
        if (subject == null || !subject.contains("-")) {
            return subject;
        }
//        return subject.substring(0, subject.indexOf('-'));
        return subject.substring(0, subject.lastIndexOf('-'));
    }
}
