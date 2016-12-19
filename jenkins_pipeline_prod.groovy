stage 'fetch'
    node {
        git credentialsId: 'jenkins', url: 'git@github.com:kkolberg/prototypes.git'
        sh '''git checkout tags/$TAG'''
        stash name: 'starter'
    }
stage 'build'
    node {
        unstash 'starter'
        dir('starter') {
            sh '''npm install'''
            sh '''npm run jenkins'''
        }
        stash 'starter'
        junit 'starter/testreports/*.xml'
    }
stage 'deploy'
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'), 
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                withEnv(['NODE_ENV=production', 'IS_NOT_LOCAL=true']) {
                    unstash 'starter'
                    dir('starter') {
                        sh '''npm prune'''
                        sh '''npm version major'''
                        sh '''serverless deploy --stage prod'''
                    }
                }
            stash 'starter'
        }
    }
