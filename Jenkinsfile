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

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/PolakuJoshikaSree/Capstone-ServiceManagement-Backend.git'
            }
        }

        stage('Build All Services') {
            steps {
                sh '''
                for service in api-gateway auth-service booking-service billing-service config-server notification-service service-catalog service-registry
                do
                  echo "Building $service"
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
                for service in auth-service booking-service billing-service service-catalog
                do
                  echo "Testing $service"
                  cd $service
                  mvn test
                  cd ..
                done
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker compose build --no-cache'
            }
        }

        stage('Docker Deploy') {
            steps {
                sh '''
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
            echo 'Pipeline failed. Check logs.'
        }
    }
}
