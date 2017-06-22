stage('fetch') {
    node {
        if ( '$BRANCH'?.trim() ) {
            git branch: '$BRANCH', credentialsId: 'jenkins-git', url: '$SSH_REPO'
        }else {
            git credentialsId: 'jenkins-git', url: '$SSH_REPO'
        }
    }
}
stage('update environment service'){
    node {
        echo '@@@@ Setting Environment Variables File for DEV @@@@'
        sh "rm ./PAWS-OIDC-bridge-env.js"
        sh "rm ./PAWS-OIDC-bridge-env.qa.js"
        sh "rm ./PAWS-OIDC-bridge-env.prod.js"
        sh "mv ./PAWS-OIDC-bridge-env.dev.js ./PAWS-OIDC-bridge-env.js"       
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                sh '''aws s3 sync ./ s3://$BUCKET --delete --exclude ".git*"'''
            }
    }
}
