stage('fetch') {
    node {

            git branch: 'live', credentialsId: 'jenkins-git', url: '$SSH_REPO'
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
        sh '''echo "@@@@ Setting Environment Variables File for QA @@@@"'''
        sh '''pwd'''
        sh '''cat ./www/assets/json/student-resouces.qa.json'''
        sh '''rm ./www/assets/json/student-resources.json'''
        sh '''rm ./www/assets/json/student-resources.dev.json'''
        sh '''rm ./www/assets/json/student-resources.prod.json'''
        sh '''mv ./www/assets/json/student-resources.qa.json ./www/assets/json/student-resources.json'''
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
