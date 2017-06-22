stage('fetch') {
    node {
         git branch: 'live', credentialsId: 'jenkins-git', url: '$SSH_REPO'
        sh '''git checkout tags/$TAG'''
    }
}
stage('update environment service'){
    node {
        echo '@@@@ Setting Environment Variables File for PROD @@@@'
        sh "rm ./PAWS-OIDC-bridge-env.js"
        sh "rm ./PAWS-OIDC-bridge-env.qa.js"
        sh "rm ./PAWS-OIDC-bridge-env.dev.js"
        sh "mv ./PAWS-OIDC-bridge-env.prod.js ./PAWS-OIDC-bridge-env.js"       
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'PROD_AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'PROD_AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                sh '''aws s3 sync ./ s3://$BUCKET/$TAG/ --delete --exclude ".git*"'''
            }
    }
}
