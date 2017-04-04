stage('fetch') {
    node {
        git branch: 'live', credentialsId: 'jenkins-git', url: '$SSH_REPO'
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
        sh '''npm run jenkins'''
        junit 'testreports/*jenkins.xml'
    }
}
stage('update environment'){
    node {
        echo '@@@@ Setting Environment Variables File for QA @@@@'
        sh "rm ./deploy.env.yml"
        sh "rm ./deploy.env.dev.yml"
        sh "rm ./deploy.env.prod.yml"
        sh "mv ./deploy.env.qa.yml ./deploy.env.yml"
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'), 
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                sh '''npm run deploy -- --stage qa'''
            }
    }
}