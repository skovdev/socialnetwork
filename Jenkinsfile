pipeline {
    agent any
    tools {
        jdk: 'openjdk-14'
    }
    stages {
        stage("Clone the project from GitHub") {
            steps {
                git branch: 'master', url: 'https://github.com/skovdev/socialnetwork.git'
            }
        }
        stage("Compilation") {
            steps {
                withMaven(maven: 'mvn') {
                    sh "mvn clean install -DskipTests"
                }
            }
        }
    }
}