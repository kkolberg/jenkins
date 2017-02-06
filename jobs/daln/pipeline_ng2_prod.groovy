stage('fetch') {
    node {
        if ( '$BRANCH'?.trim() ) {
            git branch: '$BRANCH', credentialsId: 'jenkins-git', url: '$SSH_REPO'
        }else {
            git credentialsId: 'jenkins-git', url: '$SSH_REPO'
        }
    }
}
stage('build') {
    node {
        sh "npm run buildProd"
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'DALN_AWS_KEY_ID_DEV', variable: 'DALN_AWS_KEY_ID_DEV'),
            string(credentialsId: 'DALN_AWS_SECRET_KEY_DEV', variable: 'DALN_AWS_SECRET_KEY_DEV')]) {
                sh '''aws s3 sync ./www s3://$BUCKET --delete'''
            }
    }
}
