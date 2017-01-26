stage('fetch') {
    node {
        if ( '$BRANCH'?.trim() ) {
            git branch: '$BRANCH', credentialsId: 'jenkins-git', url: '$SSH_REPO'
        }else {
            git credentialsId: 'jenkins-git', url: '$SSH_REPO'
        }
    }
}
stage('build') {
    node {
        sh '''npm run globals'''
        sh '''npm install'''
        sh '''npm run ionic:build'''
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
        sh "rm ./www/assets/json/env.json"
        sh "rm ./www/assets/json/env.sandbox.json"
        sh "rm ./www/assets/json/env.qa.json"
        sh "rm ./www/assets/json/env.prod.json"
        sh "mv ./www/assets/json/env.dev.json ./www/assets/json/env.json"
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
