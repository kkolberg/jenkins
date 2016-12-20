stage 'fetch'
    node {
        git credentialsId: 'jenkins', url: 'git@github.com:kkolberg/prototypes.git'
        sh '''ls'''
        stash name: 'starter'
    }
stage 'build'
    node {
        unstash 'starter'
        dir('starter') {
            sh '''npm install'''
            sh '''npm run jenkins'''
            sh '''npm run sonar'''
            sh '''npm run cover'''
            sh '''whoami'''
            sh '''sudo sonar-runner'''
        }
        stash 'starter'
        junit 'starter/testreports/*jenkins.xml'
    }
stage 'deploy'
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'), 
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
            unstash 'starter'
            dir('starter') {
                sh '''serverless deploy --stage dev'''
            }
        }
    }
