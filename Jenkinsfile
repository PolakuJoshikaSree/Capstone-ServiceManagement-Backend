pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        SPRING_PROFILES_ACTIVE = 'docker'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/PolakuJoshikaSree/Capstone-ServiceManagement-Backend.git'
            }
        }

        stage('Build Microservices') {
            steps {
                script {
                    def services = [
                        'service-registry',
                        'config-server',
                        'api-gateway',
                        'auth-service',
                        'booking-service',
                        'billing-service',
                        'notification-service'
                    ]

                    for (service in services) {
                        echo "🔨 Building ${service}"
                        dir(service) {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    def services = [
                        'auth-service',
                        'booking-service',
                        'billing-service',
                        'service-catalog'
                    ]

                    for (service in services) {
                        echo "🧪 Testing ${service}"
                        dir(service) {
                            sh 'mvn test'
                        }
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker compose build'
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
            echo '✅ Capstone backend pipeline completed successfully'
        }
        failure {
            echo '❌ Capstone backend pipeline failed'
        }
    }
}
