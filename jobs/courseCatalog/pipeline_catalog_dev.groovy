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
        sh "rm ./src/assets/env/env.ts"
        sh "rm ./src/assets/env/prod.env.ts"
        sh "rm ./src/assets/env/qa.env.ts"
        sh "mv ./src/assets/env/dev.env.ts ./src/assets/env/env.ts"   
    }
}
stage('build') {
    node {
        sh "npm run globals"
        sh "npm install"
        sh "npm run ionic:build"
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
