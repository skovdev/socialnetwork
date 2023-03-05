node {
    stage("Clone the project from GitHub") {
        git branch: 'master', url: 'https://github.com/skovdev/socialnetwork.git'
    }

    stage("Compilation") {
       sh "/.mvnw clean install -DskipTests"
    }
}