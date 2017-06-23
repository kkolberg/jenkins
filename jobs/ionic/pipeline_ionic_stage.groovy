stage('fetch') {
    node {
         git branch: 'live', credentialsId: 'jenkins-git', url: '$SSH_REPO'
        sh '''git checkout tags/$TAG'''
    }
}
stage('build') {
    node {
        sh "npm run deps"
        sh '''npm run ionic:build'''
        sh "echo '' > ./www/cordova.js"
    }
}
stage('update environment'){
    node {
        echo '@@@@ Setting Environment Variables File for PROD @@@@'
        sh "rm -f ./www/assets/json/student-resources.json"
        sh "rm -f ./www/assets/json/student-resources.dev.json"
        sh "rm -f ./www/assets/json/student-resources.qa.json"
        sh "mv ./www/assets/json/student-resources.prod.json ./www/assets/json/student-resources.json 2>/dev/null"
        sh "rm -f ./www/assets/json/env.json"
        sh "rm -f ./www/assets/json/env.qa.json"
        sh "rm -f ./www/assets/json/env.dev.json"
        sh "mv ./www/assets/json/env.prod.json ./www/assets/json/env.json 2>/dev/null"
        sh "rm -f ./www/assets/json/settings.json"
        sh "rm -f ./www/assets/json/settings.local.json"
        sh "mv ./www/assets/json/settings.aws.json ./www/assets/json/settings.json 2>/dev/null"
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
