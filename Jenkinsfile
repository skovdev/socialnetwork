pipeline {
    agent any
    environment {
      JAVA_HOME = "/var/jenkins_home/tools/hudson.model.JDK/openjdk-14"
    }
    tools {
        jdk 'openjdk-14'
    }
    stages {
        stage("Clone the project from GitHub") {
            steps {
                git branch: 'master', url: 'https://github.com/skovdev/socialnetwork.git'
            }
        }
        stage("Compilation") {
            steps {
                withMaven(jdk: 'openjdk-14', maven: 'mvn') {
                    sh "mvn clean install -DskipTests"
                }
            }
        }
    }
}