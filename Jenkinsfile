pipeline {
    
    agent {
        label 'linux'
    }

    triggers {
        githubPush()
    }

    stages {
        
        stage('Build') {
            steps {
                echo 'Building project....'
                sh 'mvn -B -DskipTests clean package'
                sh 'mv target/*.jar app.jar'
                archiveArtifacts artifacts: '*.jar', followSymlinks: false
            }
        }
        
        stage('Test') {
            steps {
                echo 'Running tests..'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying into the cloud...'
                sh 'java -jar app.jar &'
            }
        }
        
    }
}
