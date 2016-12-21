stage('fetch') {
    node {
        git credentialsId: 'jenkins-git', url: '$SSH_REPO'
    }
}
stage('build') {
    node {
        sh '''npm run globals'''
        sh '''npm run sonar'''
        sh '''sonar-runner'''
        junit 'testreports/*jenkins.xml'
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'), 
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
            sh '''serverless deploy --stage dev'''
        }
    }
}
