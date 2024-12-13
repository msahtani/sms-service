pipeline {
    agent {label 'linux'}

    stages {

        
        stage('Build') {
            steps {
                pwd
                echo 'Building project....'
            }
        }
        stage('Test') {
            steps {
                echo 'Running tests...'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying into the cloud...'
            }
        }
    }
}
