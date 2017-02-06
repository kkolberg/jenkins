stage('fetch') {
    node {
        git credentialsId: 'jenkins-git', url: '$SSH_REPO'
        sh '''git checkout tags/$TAG'''
    }
}
stage('build') {
    node {
        sh '''npm run build'''
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'PROD_AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'), 
            string(credentialsId: 'PROD_AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
            withEnv(['NODE_ENV=production', 'IS_NOT_LOCAL=true']) {
                sh '''npm run deploy -- --stage prod'''
            }
        }
    }
}
