pipeline {
    agent any
    environment {
        JAVA_HOME = '$JAVA_HOME'
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
                    sh "echo JAVA_HOME=$JAVA_HOME"
                    sh "mvn clean install -DskipTests"
                }
            }
        }
    }
}