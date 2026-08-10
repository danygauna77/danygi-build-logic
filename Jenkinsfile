pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                sh './gradlew clean classes'
            }
        }

        stage('Unit Tests') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: 'build/test-results/test/*.xml'

            archiveArtifacts artifacts: 'build/libs/*.jar',
                               allowEmptyArchive: true
        }

        success {
            echo 'Build-Logic pipeline completed successfully.'
        }

        failure {
            echo 'Build-Logic pipeline failed.'
        }
    }
}