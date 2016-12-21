stage('fetch') {
    node {
        git credentialsId: 'jenkins-git', url: '$SSH_REPO'
        sh '''git checkout tags/$TAG'''
    }
}
stage('build and test') {
    node {
        sh '''npm run jenkins'''
        junit 'starter/testreports/*.xml'
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'), 
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
            withEnv(['NODE_ENV=production', 'IS_NOT_LOCAL=true']) {
                sh '''npm run deploy -- --stage prod'''
            }
        }
    }
}