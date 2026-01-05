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
                script {
                    def services = [
                        'api-gateway',
                        'auth-service',
                        'booking-service',
                        'billing-service',
                        'config-server',
                        'notification-service',
                        'service-catalog-service',  
                        'service-registry'
                    ]

                    for (service in services) {
                        if (!fileExists(service)) {
                            error " Folder not found: ${service}"
                        }

                        echo "=============================="
                        echo " Building ${service}"
                        echo "=============================="

                        dir(service) {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Run Tests & Coverage') {
            steps {
                script {
                    def testServices = [
                        'auth-service',
                        'booking-service',
                        'billing-service',
                        'service-catalog-service'   // 
                    ]

                    for (service in testServices) {
                        if (!fileExists(service)) {
                            error " Folder not found: ${service}"
                        }

                        echo "=============================="
                        echo " Testing ${service}"
                        echo "=============================="

                        dir(service) {
                            sh 'mvn test'
                        }
                    }
                }
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
