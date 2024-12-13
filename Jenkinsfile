pipeline {
    agent {label 'linux'}

    triggers {
        githubPush() // This triggers the build on GitHub push evnts.
    }

    stages {
        stage('Checkout') {
            steps {
                git clone 'https://github.com/msahtani/sms-service.git'
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
