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

        stage('Build') {
            steps {
                sh 'mvn -q clean package -DskipTests'
            }
        }

        stage('Test (No Eureka / DB)') {
            steps {
                sh 'mvn -q test -Dspring.cloud.discovery.enabled=false -Deureka.client.enabled=false'
            }
        }

        stage('Docker Build (Optional)') {
            when {
                expression { sh(script: 'which docker', returnStatus: true) == 0 }
            }
            steps {
                sh 'docker compose build'
            }
        }
    }

    post {
        success {
            echo ' Pipeline completed successfully'
        }
        failure {
            echo ' Pipeline failed'
        }
    }
}
