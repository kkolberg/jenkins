stage('fetch') {
    node {

            git branch: 'live', credentialsId: 'jenkins-git', url: '$SSH_REPO'
          }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                sh '''aws s3 sync ./ s3://$BUCKET --delete'''
            }
    }
}