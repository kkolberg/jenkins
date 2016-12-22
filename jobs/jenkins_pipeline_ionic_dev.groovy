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
        sh '''npm run sonar'''
        sh '''sonar-runner'''
        junit 'testreports/*jenkins.xml'
    }
}
stage('deploy') {
    node {
        
    }
}