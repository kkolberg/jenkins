stage('fetch') {
    node {
        git credentialsId: 'jenkins-git', url: '$SSH_REPO' 
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
        sh '''sonar-runner'''
        junit 'testreports/*jenkins.xml'
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
stage('integration tests') {
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'), 
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                try {
                    echo 'do integration tests'
                    //do postman stuff here
                } catch (Exception err) {
                    def output = sh returnStdout: true, script: "sls deploy list | grep 'Timestamp' | awk '{print \$3}'"
                    echo output
                    def stampStrings = output.tokenize("\n")
                    
                    if(stampStrings.size()>1) {
                        def previous = stampStrings.sort()[-2].toString()
                        sh([script: 'sls rollback --timestamp ' + previous])
                    }

                    currentBuild.result = 'FAILURE'
                }
            }
    }
}