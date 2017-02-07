stage('fetch') {
    node {
         git branch: 'deploy', credentialsId: 'jenkins-git', url: '$SSH_REPO'
        sh '''git checkout tags/$TAG'''
    }
}
stage('build') {
    node {
        sh '''npm run globals'''
        sh '''npm install'''
        sh '''ng build -prod'''
    }
}

stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'DALN_AWS_KEY_ID_PROD', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'DALN_AWS_SECRET_KEY_PROD', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                sh '''aws s3 sync ./dist s3://$BUCKET/$TAG/ --delete'''
            }
    }
}
