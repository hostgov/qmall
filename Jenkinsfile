pipeline {
  agent {
    node {
      label 'maven'
    }
  }
    parameters {
            string(name:'PROJECT_VERSION',defaultValue: 'v0.0Beta',description:'')
            string(name:'PROJECT_NAME',defaultValue: '',description:'')
    }

    environment {
            DOCKER_CREDENTIAL_ID = 'dockerhub-id'
            GITHUB_CREDENTIAL_ID = 'github-id'
            KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
            REGISTRY = 'docker.io'
            DOCKERHUB_NAMESPACE = 'hostgov'
            GITHUB_ACCOUNT = 'hostgov'
            SONAR_CREDENTIAL_ID = 'sonar-qube'
            RANCH_NAME = 'master'
    }

    stages {
      stage('拉取代码') {
        steps {
          git(credentialsId: 'github-id', url: 'https://github.com/hostgov/qmall.git', branch: 'master', changelog: true, poll: false)
          sh 'echo 正在构建 $PROJECT_NAME 版本号: $PROJECT_VERSION'
          container ('maven') {
                    sh "mvn clean install -Dmaven.test.skip=true"
          }
        }
      }

      stage('sonarqube 代码analysis') {
            steps {
              container ('maven') {
                withCredentials([string(credentialsId: "$SONAR_CREDENTIAL_ID", variable: 'SONAR_TOKEN')]) {
                  sh "mvn sonar:sonar -gs `pwd`/mvn-setting.xml -Dsonar.branch=$BRANCH_NAME -Dsonar.login=$SONAR_TOKEN"
                }
                timeout(time: 1, unit: 'HOURS') {
                  waitForQualityGate abortPipeline: true
                }
              }
            }
      }
    }


}
