pipeline {
    agent any
//     parameters {
//         booleanParam(name: 'doPublicRelease', defaultValue: false, description: 'Deploy to maven central repostiroy')
//     }
  triggers {
      pollSCM 'H H * * *'
  }
      options {
        disableConcurrentBuilds()
        ansiColor('xterm')
        buildDiscarder(logRotator(
                    artifactDaysToKeepStr: "",
                    artifactNumToKeepStr: "5",
                    daysToKeepStr: "",
                    numToKeepStr: "10"
        ))
        timeout(time: 5, unit: 'MINUTES')
        timestamps()
      }

    stages {
        stage('Tests jdk-8') {
          steps {
	          sh '''#!/bin/bash
	            source  ~/.bashrc
	            sdk install java 8.0.462-zulu && sdk use java 8.0.462-zulu
	            sdk install maven 3.6.1 && sdk use  maven 3.6.1
	            mvn clean test -Pjdk8,-jdk11,run-jam-tests
	          '''
          }
          post {
            always {
              junit '**/target/surefire-reports/**/*.xml'
            }
          }
        }
        stage('Tests jdk-11') {
          steps {
            sh script: 'source  ~/.bashrc'
            sh script: 'sdk install java  11.0.29-zulu && sdk use java 11.0.29-zulu'
            sh script: 'sdk install maven 3.6.1 && sdk use  maven 3.6.1'
            sh script: 'mvn clean test -P-jdk8,jdk11,run-jam-tests'
          }
          post {
              always {
                  junit '**/target/surefire-reports/**/*.xml'
              }
          }
        }
        stage('Tests jdk-17') {
          steps {
            sh script: 'source  ~/.bashrc'
            sh script: 'sdk install java  17.0.17-zulu && sdk use java 17.0.17-zulu'
            sh script: 'sdk install maven 3.6.1 && sdk use  maven 3.6.1'
            sh script: 'mvn clean test -P-jdk8,jdk11,run-jam-tests'
          }
          post {
              always {
                  junit '**/target/surefire-reports/**/*.xml'
              }
          }
        }
        stage('Tests jdk-21') {
          steps {
            sh script: 'source  ~/.bashrc'
            sh script: 'sdk install java  21.0.9-zulu && sdk use java 21.0.9-zulu'
            sh script: 'sdk install maven 3.6.1 && sdk use  maven 3.6.1'
            sh script: 'mvn clean test -P-jdk8,jdk11,run-jam-tests'
          }
          post {
              always {
                  junit '**/target/surefire-reports/**/*.xml'
              }
          }
        }
        stage('Tests jdk-25') {
          steps {
            sh script: 'source  ~/.bashrc'
            sh script: 'sdk install java 25.0.1-zulu  && sdk use java 25.0.1-zulu '
            sh script: 'sdk install maven 3.6.1 && sdk use  maven 3.6.1'
            sh script: 'mvn clean test -P-jdk8,jdk11,run-jam-tests'
          }
          post {
              always {
                  junit '**/target/surefire-reports/**/*.xml'
              }
          }
        }
        stage('Deploy jdk-8') {
            steps {
                sh script: 'source  ~/.bashrc'
				sh script: 'sdk install java 25.0.1-zulu  && sdk use java 25.0.1-zulu '
				sh script: 'sdk install maven 3.6.1 && sdk use  maven 3.6.1'
				echo "mvn clean install deploy -Pjdk8,-jdk11${params.doPublicRelease ?',release':''} -e"
				sh script: "mvn clean install deploy -Pjdk8,-jdk11,release -e"
            }
        }
        stage('Deploy jdk-11') {
            steps {
				bash """
					source ~/.bashrc
					sdk install java 11.0.29-zulu && sdk use java 11.0.29-zulu
					sdk install maven 3.6.1 && sdk use maven 3.6.1
					mvn clean install deploy -P-jdk8,jdk11,release -e
				"""
            }
        }
      }

      post {
          cleanup {
              deleteDir() /* clean up our workspace */
          }
      }
}
