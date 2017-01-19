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
stage('code quality') {
    node {
        // sh '''npm run sonar'''
        // sh '''sonar-runner'''
        // junit 'testreports/*jenkins.xml'
    }
}
stage('update environment'){
    node {
        echo '@@@@ Setting Environment Variables File for PROD @@@@'
        sh "rm ./www/assets/json/student-resources.json"
        sh "rm ./www/assets/json/student-resources.dev.json"
        sh "rm ./www/assets/json/student-resources.qa.json"
        sh "mv ./www/assets/json/student-resources.prod.json ./www/assets/json/student-resources.json"
        sh "rm ./www/assets/json/env.json"
        sh "rm ./www/assets/json/env.qa.json"
        sh "rm ./www/assets/json/env.dev.json"
        sh "mv ./www/assets/json/env.prod.json ./www/assets/json/env.json"        
    }
}
stage('deploy') {
    //TO-DO!  When prod credentials are created.  Go into Jenkins > Credentials, add new secret text and then replace AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY to the contstant named in the new credentials
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                sh '''aws s3 sync ./www s3://$BUCKET/$TAG/ --delete'''
            }
    }
}
