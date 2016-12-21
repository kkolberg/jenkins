stage('fetch') {
    node {
        git credentialsId: '$GIT_USER', url: '$SSH_REPO'
        stash name: '$NAME'
    }
}
stage('build') {
    node {
        unstash '$NAME'
        sh '''npm run globals'''
        sh '''npm run sonar'''
        sh '''sonar-runner'''
        stash '$NAME'
        junit 'testreports/*jenkins.xml'
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'), 
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
            unstash '$NAME'
            sh '''npm run deploy -- --stage dev'''
        }
    }
}
