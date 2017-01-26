stage('fetch') {
    node {
         git branch: 'live', credentialsId: 'jenkins-git', url: '$SSH_REPO'
        sh '''git checkout tags/$TAG'''
    }
}
stage('build') {
    node {
        sh '''npm run globals'''
        sh '''npm install'''
        sh '''npm run ionic:build'''
    }
}
stage('update environment'){
    node {
        echo '@@@@ Setting Environment Variables File for PROD @@@@'
        sh "rm ./www/assets/json/env.json"
        sh "rm ./www/assets/json/env.sandbox.json"
        sh "rm ./www/assets/json/env.qa.json"
        sh "rm ./www/assets/json/env.dev.json"
        sh "mv ./www/assets/json/env.prod.json ./www/assets/json/env.json"        
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
