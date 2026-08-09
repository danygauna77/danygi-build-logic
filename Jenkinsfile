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
            bat '.\\gradlew.bat clean classes'
        }
    }

    stage('Unit Tests') {
        steps {
            bat '.\\gradlew.bat test'
        }
    }

    stage('Build') {
        steps {
            bat '.\\gradlew.bat build'
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