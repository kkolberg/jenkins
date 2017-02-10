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
            notifyBuild('STARTED')
            sh "npm run globals"
            sh "npm install"
            sh "npm run buildDev"
        } catch (e) {
            // If there was an exception thrown, the build failed
            currentBuild.result = "FAILED"
            throw e
        } finally {
            // Success or failure, always send notifications
            notifyBuild(currentBuild.result)
        }
    }

        def notifyBuild(String buildStatus = 'STARTED') {
        // build status of null means successful
        buildStatus =  buildStatus ?: 'SUCCESSFUL'

        // Default values
        def colorName = 'RED'
        def colorCode = '#FF0000'
        def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
        def summary = "${subject} (${env.BUILD_URL})"
        def details = """<p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
            <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""

        // Override default values based on build status
        if (buildStatus == 'STARTED') {
            color = 'YELLOW'
            colorCode = '#FFFF00'
        } else if (buildStatus == 'SUCCESSFUL') {
            color = 'GREEN'
            colorCode = '#00FF00'
        } else {
            color = 'RED'
            colorCode = '#FF0000'
        }

        // Send notifications
        slackSend (color: colorCode, message: summary)

        hipchatSend (color: color, notify: true, message: summary)

        emailext (
            subject: subject,
            body: details,
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
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