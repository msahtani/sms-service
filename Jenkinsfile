pipeline {
    

  agent {
    node 'sms-service'
  }

  triggers {
    githubPush()
  }

  stages {
      
    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests clean package'
        sh 'mv target/*.jar app.jar'
        archiveArtifacts artifacts: 'app.jar', followSymlinks: false
      }
    }
      

    stage('Deploy') {
      steps {
        // run the app using JVM
        sh 'chmod +x run.sh'
        sh './run.sh > output.log 2>&1 &'
        disown
        echo "Process disowned and running in background"
      }
    }

  
  }
  
}
