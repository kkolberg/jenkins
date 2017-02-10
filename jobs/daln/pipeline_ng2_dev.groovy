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
        try{
            sh "npm run globals"
            sh "npm install"
            sh "npm run buildDev"
            emailext attachLog: true, body: 'Daln Build Successful.', recipientProviders: [[$class: 'DevelopersRecipientProvider']], subject: 'Build Success', to: 'appdevmgr@gsu.edu; wmomen1@mygsu.onmicrosoft.com'
        } catch(e){
            emailext attachLog: true, body: 'There is a build failure.', recipientProviders: [[$class: 'FailingTestSuspectsRecipientProvider'], [$class: 'FirstFailingBuildSuspectsRecipientProvider']], subject: 'Build Failure', to: 'appdevmgr@gsu.edu; wmomen1@mygsu.onmicrosoft.com'
        }   
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'DALN_AWS_KEY_ID_DEV', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'DALN_AWS_SECRET_KEY_DEV', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                sh '''aws s3 sync ./dist s3://$BUCKET --delete'''
            }
    }
}
// stage('notify'){
//     emailext attachLog: true, body: 'There is a build failure.', recipientProviders: [[$class: 'FailingTestSuspectsRecipientProvider'], [$class: 'FirstFailingBuildSuspectsRecipientProvider']], subject: 'Build Failure', to: 'appdevmgr@gsu.edu'
// }