stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                def output = sh returnStdout: true, script: "aws s3 ls ${BUCKET}/stage/${TAG} | wc -l"
                if(output == "1"){
                    sh '''aws s3 sync s3://$BUCKET/stage/$TAG s3://$BUCKET/live --delete'''
                }else{
                    currentBuild.result = 'FAILURE'
                }
            }
    }
}