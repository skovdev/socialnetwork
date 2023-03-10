pipeline {
    agent any
    tools {
        jdk 'jdk19'
    }
    stages {
        stage("Clone the project from GitHub") {
            steps {
                git branch: 'master', url: 'https://github.com/skovdev/socialnetwork.git'
            }
        }
        stage("Compilation") {
            steps {
                withMaven(jdk: 'jdk19', maven: 'mvn3') {
                    sh "echo JAVA_HOME=$JAVA_HOME"
                    sh "java -version"
                    sh "mvn clean install -DskipTests"
                }
            }
        }
    }
}