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
                archiveArtifacts artifacts: 'target/*.jar', followSymlinks: false
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
                sh 'mv *.jar app.jar'
                sh 'cd target && java -jar app.jar'
            }
        }
        
    }
}
