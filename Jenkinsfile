pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        SPRING_PROFILES_ACTIVE = 'test'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/PolakuJoshikaSree/Capstone-ServiceManagement-Backend.git'
            }
        }

        stage('Build & Test Services') {
            steps {
                script {
                    def services = [
                        'auth-service',
                        'booking-service',
                        'billing-service',
                        'notification-service',
                        'Service-catalog',
                        'api-gateway',
                        'service-registry'
                    ]

                    for (s in services) {
                        dir(s) {
                            sh '''
                              mvn -q clean test \
                              -Deureka.client.enabled=false \
                              -Dspring.cloud.discovery.enabled=false
                            '''
                        }
                    }
                }
            }
        }
    }

    post {
        success { echo ' Pipeline SUCCESS' }
        failure { echo ' Pipeline FAILED' }
    }
}
