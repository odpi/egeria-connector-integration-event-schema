<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright Contributors to the Egeria project. -->



# Egeria integration connector for Event Schemas

This project contains the Egeria integration connector for event schemas which are read from a Schema Registry - e. g. Confluent&trade; Schema Registry. It uses API-calls to retrieve 
schema definitions for all subjects in all versions. The connector tries to match
the schemas to existing topics in Egeria using the part of the subject name up to the last hyphen (`-`). 
If the subject name does not contain any hypens the complete name is considered as the topic.
Each schema version is mapped to an [EventType](https://egeria-project.org/types/5/0535-Event-Schemas/#eventtype)  
and each attribute is mapped to an [EventSchemaAttribute](https://egeria-project.org/types/5/0535-Event-Schemas/#eventschemaattribute). 
Nested schema attributes are recursively mapped with the [NestedSchemaAttribute](https://egeria-project.org/types/5/0505-Schema-Attributes/#nestedschemaattribute-relationship)
relationship.

For detailed documentation see [Event Schema Integration Connector](https://egeria-project.org/connectors/integration/event-schema-integration-connector)

## Known Limitations
* Currently only Avro Schemas are supported. 


----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright Contributors to the  Egeria project.
