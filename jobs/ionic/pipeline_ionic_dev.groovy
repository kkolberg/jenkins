stage('fetch') {
    node {
        if ( '$BRANCH'?.trim() ) {
            git branch: '$BRANCH', credentialsId: 'jenkins-git', url: '$SSH_REPO'
        }else {
            git credentialsId: 'jenkins-git', url: '$SSH_REPO'
        }
    }
}
stage('update environment service'){
    node {
        echo '@@@@ Setting Environment Variables File for DEV @@@@'
        sh "rm ./src/services/env.service.ts"
        sh "rm ./src/services/env.service.qa.ts"
        sh "rm ./src/services/env.service.prod.ts"
        sh "mv ./src/services/env.service.dev.ts ./src/services/env.service.ts"   
    }
}
stage('build') {
    node {
        sh "npm run deps"
        sh "npm run ionic:build"
        sh "echo '' > ./www/cordova.js"
    }
}
stage('code quality') {
    node {
        // sh '''npm run sonar'''
        // sh '''sonar-runner'''
        // junit 'testreports/*jenkins.xml'
    }
}
stage('update environment'){
    node {
        echo '@@@@ Setting Environment Variables File for DEV @@@@'
        sh "rm ./www/assets/json/settings.json"
        sh "rm ./www/assets/json/settings.local.json"
        sh "mv ./www/assets/json/settings.aws.json ./www/assets/json/settings.json"
        
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                sh '''aws s3 sync ./www s3://$BUCKET --delete'''
            }
    }
}
