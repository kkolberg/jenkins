stage('fetch') {
    node {
        git credentialsId: 'jenkins-git', url: 'git@github.com:gastate/serverless-seed.git'
        stash name: 'serverless-seeder'
    }
}
stage('build') {
    node {
        unstash 'serverless-seeder'
        sh '''npm run globals'''
        sh '''npm install'''
        sh '''npm run sonar'''
        sh '''sonar-runner'''
        stash 'serverless-seeder'
        junit 'testreports/*jenkins.xml'
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'), 
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
            unstash 'serverless-seeder'
            sh '''serverless deploy --stage dev'''
        }
    }
}
