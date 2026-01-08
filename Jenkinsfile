pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    environment {
        NETWORK = 'service-net'
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/PolakuJoshikaSree/Capstone-ServiceManagement-Backend.git'
            }
        }

        stage('Create Docker Network') {
            steps {
                sh '''
                  docker network create ${NETWORK} || true
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    def services = [
                        'service-registry',
                        'api-gateway',
                        'auth-service',
                        'service-catalog',
                        'booking-service',
                        'billing-service',
                        'notification-service'
                    ]

                    for (service in services) {
                        dir(service) {
                            sh '''
                              mvn clean package -DskipTests
                              docker build -t ${service}:latest .
                            '''
                        }
                    }
                }
            }
        }

        stage('Run Core Infra') {
            steps {
                sh '''
                  docker run -d --name mongo --network ${NETWORK} -p 27017:27017 mongo
                  docker run -d --name redis --network ${NETWORK} -p 6379:6379 redis
                '''
            }
        }

        stage('Run Services') {
            steps {
                sh '''
                  docker run -d --name service-registry \
                    --network ${NETWORK} -p 8761:8761 service-registry:latest

                  sleep 15

                  docker run -d --name api-gateway \
                    --network ${NETWORK} -p 8765:8765 api-gateway:latest

                  docker run -d --name auth-service \
                    --network ${NETWORK} -p 8802:8802 auth-service:latest
                '''
            }
        }

        stage('Health Check Tests') {
            steps {
                sh '''
                  echo " Waiting for services..."
                  sleep 20

                  echo " Testing API Gateway"
                  curl -f http://localhost:8765/actuator/health

                  echo " Testing Auth Service"
                  curl -f http://localhost:8802/actuator/health
                '''
            }
        }
    }

    post {
        always {
            echo " Cleaning up containers"
            sh '''
              docker stop $(docker ps -aq) || true
              docker rm $(docker ps -aq) || true
              docker network rm ${NETWORK} || true
            '''
        }
        success {
            echo " DOCKER PIPELINE SUCCESSFUL"
        }
        failure {
            echo " DOCKER PIPELINE FAILED"
        }
    }
}
