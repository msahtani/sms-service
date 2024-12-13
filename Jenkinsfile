pipeline {
    agent {label 'linux'}

    stages {

        
        stage('Build') {
            steps {
                sh 'pwd'
                echo 'Building project....'
            }
        }
        stage('Test') {
            steps {
                echo 'Running tests.....'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying into the cloud...'
            }
        }
    }
}
