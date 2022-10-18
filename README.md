<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright Contributors to the ODPi Egeria project. -->

[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/3044/badge)](https://bestpractices.coreinfrastructure.org/projects/3044)

[//]: # ([![Azure]&#40;https://dev.azure.com/odpi/egeria/_apis/build/status/odpi.egeria&#41;]&#40;https://dev.azure.com/odpi/Egeria/_build&#41;)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=odpi_egeria&metric=alert_status)](https://sonarcloud.io/dashboard?id=odpi_egeria)
[![Maven Central](https://img.shields.io/maven-central/v/org.odpi.egeria/egeria)](https://mvnrepository.com/artifact/org.odpi.egeria)


# Egeria integration connector for Strimzi Topics

This project contains the Egeria integration connector for event schemas which are read from a Schema Registry - e. g. Confluent&trade; Schema Registry. It uses API-calls to retrieve 
schema definitions for all subjects in all versions. The connector tries to match
the schemas to existing topics in Egeria using the part of the subject name up to the last hyphen (`-`). 
If the subject name does not contain any hypens the complete name is considered as the topic.
Each schema version is mapped to an [EventType](https://egeria-project.org/types/5/0535-Event-Schemas/#eventtype)  
and each attribute is mapped to an [EventSchemaAttribute](https://egeria-project.org/types/5/0535-Event-Schemas/#eventschemaattribute). 
Nested schema attributes are recursively mapped with the [NestedSchemaAttribute](https://egeria-project.org/types/5/0505-Schema-Attributes/#nestedschemaattribute-relationship)
relationship.


[//]: # (This integration connector needs to be configured. The following steps can be performed using the postman collection to set this connector up.)


## For testing

### Build
On a terminal,
* navigate to the folder that this README.md is in.
* run ```./gradlew clean build```

### Running locally for testing

You will need to have an OMAG platform with the connector jar, a metadata server defined.

### Testing mutations

[//]: # (TODO: extend with testing information)

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright Contributors to the ODPi Egeria project.
