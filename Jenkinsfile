pipeline {
  agent any

  triggers {
      pollSCM 'H H * * *'
  }
  options {
    disableConcurrentBuilds()
    ansiColor('xterm')
    buildDiscarder(logRotator(
                artifactDaysToKeepStr: "5",
                artifactNumToKeepStr: "5",
                daysToKeepStr: "5",
                numToKeepStr: "5"
    ))
    timeout(time: 5, unit: 'MINUTES')
    timestamps()
  }
  stages {
    stage('Tests jdk-8') {
      tools {
        jdk "zulu-jdk-8"
        maven 'Maven 3.6.1'
      }
      steps {
        sh script: 'mvn clean test -Pjdk8,-jdk11,run-jam-tests'
      }
      post {
          success {
              junit 'target/surefire-reports/**/*.xml'
          }
      }
    }
    stage('Tests jdk-11') {
      tools {
        jdk "zulu-jdk-11"
        maven 'Maven 3.6.1'
      }
      steps {
        sh script: 'mvn clean test -P-jdk8,jdk11,run-jam-tests'
      }
      post {
          success {
              junit 'target/surefire-reports/**/*.xml'
          }
      }
    }
  }

  post {
      cleanup {
          deleteDir() /* clean up our workspace */
      }
  }
}
