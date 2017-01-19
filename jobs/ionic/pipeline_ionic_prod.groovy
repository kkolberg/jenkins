stage('deploy') {
    node {
        withCredentials([
            string(credentialsId: 'PROD_AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
            string(credentialsId: 'PROD_AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                def output = sh returnStdout: true, script: "aws s3 ls ${STAGE_BUCKET}/${TAG} | wc -l"
                echo output
                if(output as Integer == 1){
                    sh '''aws s3 sync s3://$STAGE_BUCKET/$TAG/ s3://$BUCKET/ --delete'''
                }else{
                    currentBuild.result = 'FAILURE'
                }
            }
    }
}
