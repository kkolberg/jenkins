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
        sh '''npm run build'''
    }
}
stage('code quality') {
    node {
        sh '''npm run sonar'''
        sh '''sonar-runner'''
        junit 'testreports/*jenkins.xml'
    }
}
stage('update environment'){
    node {
        echo '@@@@ Setting Environment Variables File for DEV @@@@'
        sh "rm ./deploy.env.yml"
        sh "rm ./deploy.env.prod.yml"
        sh "rm ./deploy.env.qa.yml"
        sh "mv ./deploy.env.dev.yml ./deploy.env.yml"
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                sh '''npm run deploy -- --stage dev'''
            }
    }
}
// stage('integration tests') {
//     node {
//         sh '''npm install -g newman'''
//         withCredentials([
//             string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
//             string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
//                 try {
//                     echo 'do integration tests'
//                     if(fileExists('integration/integration.json')) {
//                         sh '''newman run integration/integration.json'''
//                     }
//                 } catch (Exception err) {
//                     def output = sh returnStdout: true, script: "sls deploy list | grep 'Timestamp' | awk '{print \$3}'"
//                     echo output
//                     def stampStrings = output.tokenize("\n")

//                     if(stampStrings.size()>1) {
//                         def previous = stampStrings.sort()[-2].toString()
//                         sh([script: 'sls rollback --timestamp ' + previous])
//                     }

//                     currentBuild.result = 'FAILURE'
//                 }
//             }
//     }
// }
