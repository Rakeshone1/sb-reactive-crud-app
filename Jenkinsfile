pipeline {
    agent any

    tools {
        jdk 'jdk-21'
        maven 'maven-3.8.4'
    }

    environment {
        // SonarQube configuration
        SONARQUBE_SERVER      = 'SonarCloud'
        SONAR_PROJECT_KEY     = 'rakeshone1_sb-reactive-crud-app'
        SONAR_PROJECT_NAME    = 'sb-reactive-crud-api'
        SONAR_ORGANIZATION    = 'Rakeshone1'

        // Docker configuration
        APP_NAME              = 'sb-reactive-crud-app'
        DOCKER_IMAGE          = "rakeshnewone/${APP_NAME}"
        CONTAINER_NAME        = 'sb-reactive-crud-app'
        APP_PORT              = '8081'
        DOCKERHUB_CREDENTIALS = 'id'
        HOST_PORT_MAPPING     = '8081:8081'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Cloning repository...'
                git branch: 'main', url: 'https://github.com/Rakeshone1/sb-reactive-crud-app.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Building with Maven...'
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Running SonarQube analysis...'
                withSonarQubeEnv(SONARQUBE_SERVER) {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.organization=${SONAR_ORGANIZATION} \
                        -Dsonar.projectName=${SONAR_PROJECT_NAME}
                    """
                }
            }
            post {
                always {
                    echo 'SonarQube analysis completed.'
                }
            }
        }

        stage('Quality Gate Check') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    echo 'Waiting for SonarQube quality gate...'
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    env.IMAGE_TAG = "${env.BUILD_NUMBER}"
                }
                sh """
                    echo "Building Docker image: ${DOCKER_IMAGE}:${IMAGE_TAG}"
                    docker build --pull -t ${DOCKER_IMAGE}:${IMAGE_TAG} -t ${DOCKER_IMAGE}:latest .
                """
            }
        }

        stage('Docker Push Image') {
            steps {
                echo "Logging into registry and pushing ${DOCKER_IMAGE}:${IMAGE_TAG}"
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKERHUB_CREDENTIALS}",
                    usernameVariable: 'DOCKERHUB_USERNAME',
                    passwordVariable: 'DOCKERHUB_PASSWORD'
                )]) {
                    sh '''
                        echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                        docker push ${DOCKER_IMAGE}:${IMAGE_TAG}
                        docker push ${DOCKER_IMAGE}:latest
                        docker logout || true
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline completed with status: ${currentBuild.currentResult}"
        }
    }
}
