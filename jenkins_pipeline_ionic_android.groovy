stage 'fetch'
    node {
        git credentialsId: 'jenkins', url: 'git@github.com:kkolberg/prototypes.git'
        sh '''ls'''
        stash name: 'ionic'
    }
stage 'install'
    node {
        unstash 'ionic'
        dir('HelloLambda') {
            sh '''npm install'''
        }
        stash 'ionic'
    }
stage 'build'
    node {
        unstash 'ionic'
        dir('HelloLambda') {
            sh '''ionic build android --release'''
        }
        stash 'ionic'
    }
