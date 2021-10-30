pipeline {
    agent any
    parameters {
        booleanParam(name: 'doPublicRelease', defaultValue: false, description: 'Deploy to maven central repostiroy')
    }
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
        stage('Example') {
            steps {
                echo "doPublicRelease ${params.doPublicRelease? 'aaa': 'BBBB'}"
            }
        }
    }
}