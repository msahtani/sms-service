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
        archiveArtifacts artifacts: '*.jar', followSymlinks: false
      }
    }
      

    stage('Shutdown'){
      steps {
        // stop the previous process if exists
        sh 'fuser -k -TERM 80/tcp || :'
      }
    }

    stage('Deploy') {
      steps {
        // run the app using JVM
        sh 'nohup java -jar app.jar &'
      }
    }

  
  }
  
}
