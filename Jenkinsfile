node {
    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'dev') {
        def image

        stage('Clone Repository') {
            checkout scm
        }

        stage('Maven Build') {
            docker.image('maven:3-jdk-13-slim').inside('-v /root/.m2:/root/.m2') {
                sh 'mvn -B clean install'
            }

            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        }
    }
}