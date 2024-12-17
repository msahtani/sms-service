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
      
    stage('Test') {
      steps {
        echo 'Running tests..'
      }
    }

    stage('Deploy') {
      steps {
        // stop the previous process if exists
        sh 'fuser -kv -TERM 80/tcp || :'
        // run the app using JVM
        sh 'nohup java -jar app.jar &'
      }
    }

  
  }
  
}
