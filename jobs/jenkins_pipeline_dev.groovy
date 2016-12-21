stage('fetch') {
    node {
        git credentialsId: 'jenkins-git', url: '$SSH_REPO'
        if ( '$BRANCH'?.trim() ) {
            sh 'git checkout ' + '$BRANCH'
            sh 'git reset --hard'
            sh 'git pull'
        }
    }
}
stage('build and test') {
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
            sh '''npm run deploy -- --stage dev'''
        }
    }
}
