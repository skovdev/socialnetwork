node {
    tools {
        jdk: 'openjdk-14'
    }
    stage("Clone the project from GitHub") {
        git branch: 'master', url: 'https://github.com/skovdev/socialnetwork.git'
    }

    stage("Compilation") {
        withMaven(maven: 'mvn') {
           sh "mvn clean install -DskipTests"
        }
    }
}