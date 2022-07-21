pipeline {
//     agent {label 'jdk11'}

    environment {
        LOG_LEVEL = 'INFO'
        NEXUS_CREDENTIALS = credentials('SCM_CREDENTIALS')
    }

    options {
        buildDiscarder(logRotator(daysToKeepStr: '14'))
        timeout(time: 1, unit: 'HOURS')
    }

    stages {
        stage('Start image build job') {
            steps {
                build job: 'egeria-connector-event-schema/master', wait: true
            }
        }
//         stage('Start deployment job') {
//             steps {
//                 build job: 'egeria-event-catalog-prepare/master', wait: false
//             }
//         }
    }
}