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
                    sh "mvn clean install -DskipTests"
                }
            }
        }
        stage ("Tests") {
            steps {
                withMaven(jdk: 'jdk19', maven: 'mvn3') {
                    sh "mvn test"
                }
            }
        }
    }
}