stage('fetch') {
    node {
        git branch: 'live', credentialsId: 'jenkins-git', url: '$SSH_REPO'
        sh '''git checkout tags/$TAG'''
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
        echo '@@@@ Setting Environment Variables File for PROD @@@@'
        sh "rm ./deploy.env.yml"
        sh "rm ./deploy.env.dev.yml"
        sh "rm ./deploy.env.qa.yml"
        sh "mv ./deploy.env.prod.yml ./deploy.env.yml"
    }
}
stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'PROD_AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'PROD_AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
            withEnv(['NODE_ENV=production', 'IS_NOT_LOCAL=true']) {
                sh '''npm run deploy -- --stage prod'''
            }
        }
    }
}
