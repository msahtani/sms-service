pipeline {
    agent any

    triggers {
        githubPush() // This triggers the build on GitHub push eents.
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/msahtani/sms-service.git'
            }
        }
        stage('Build') {
            steps {
                echo 'Building project....'
            }
        }
        stage('Test') {
            steps {
                echo 'Running tests...'
            }
        }
    }
}
