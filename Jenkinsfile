pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    environment {
        COMPOSE_DOCKER_CLI_BUILD = '1'
        DOCKER_BUILDKIT = '1'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout Code') {
            steps {
                cleanWs()
                git branch: 'main',
                    url: 'https://github.com/PolakuJoshikaSree/Capstone-ServiceManagement-Backend.git'
            }
        }

        stage('Build All Services') {
            steps {
                sh '''
                set -e
                SERVICES="api-gateway auth-service booking-service billing-service config-server notification-service service-catalog service-registry"

                for service in $SERVICES
                do
                  echo "=============================="
                  echo " Building $service"
                  echo "=============================="
                  cd $service
                  mvn clean package -DskipTests
                  cd ..
                done
                '''
            }
        }

        stage('Run Tests & Coverage') {
            steps {
                sh '''
                set -e
                TEST_SERVICES="auth-service booking-service billing-service service-catalog"

                for service in $TEST_SERVICES
                do
                  echo "=============================="
                  echo " Testing $service"
                  echo "=============================="
                  cd $service
                  mvn test
                  cd ..
                done
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                echo "=============================="
                echo " Building Docker Images"
                echo "=============================="
                docker compose build --no-cache
                '''
            }
        }

        stage('Docker Deploy') {
            steps {
                sh '''
                echo "=============================="
                echo " Deploying Application"
                echo "=============================="
                docker compose down
                docker compose up -d
                '''
            }
        }
    }

    post {
        success {
            echo ' CI/CD Pipeline completed successfully!'
        }
        failure {
            echo ' Pipeline failed. Check console logs.'
        }
        always {
            echo ' Pipeline execution finished.'
        }
    }
}
