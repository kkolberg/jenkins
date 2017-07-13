stage('fetch') {
    node {
         git branch: 'live', credentialsId: 'jenkins-git', url: '$SSH_REPO'
        sh '''git checkout tags/$TAG'''
    }
}
stage('update environment service'){
    node {
        echo '@@@@ Setting Environment Variables File for DEV @@@@'
        sh "rm ./src/services/env.service.ts"
        sh "rm ./src/services/env.service.qa.ts"
        sh "rm ./src/services/env.service.dev.ts"
        sh "mv ./src/services/env.service.prod.ts ./src/services/env.service.ts"   
    }
}
stage('build') {
    node {
        sh '''npm run globals'''
        sh '''npm install'''
        sh '''npm run ionic:build'''
        sh "echo '' > ./www/cordova.js"
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'PROD_AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'PROD_AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                sh '''aws s3 sync ./www s3://$BUCKET/$TAG/ --delete'''
            }
    }
}
