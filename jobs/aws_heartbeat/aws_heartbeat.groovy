stage('ping studentDashboard DEV') {
    node {
        sh '''
string="alive"
if curl -header "Authorization: Heartbeat"  "https://api-dev.gsu.edu/studentDashboard/bannerData" | grep -q "$string"; then
    echo "'$string' found"
    exit 0
else
    echo "'$string' not found"
    exit 1
fi
'''
    }
}
stage('ping studentDashboard QA') {
    node {
        sh '''
string="alive"
if curl -header "Authorization: Heartbeat"  "https://api-qa.gsu.edu/studentDashboard/bannerData" | grep -q "$string"; then
    echo "'$string' found"
    exit 0
else
    echo "'$string' not found"
    exit 1
fi
'''
    }
}
stage('ping studentDashboard PROD') {
    node {
        sh '''
string="alive"
if curl -header "Authorization: Heartbeat"  "https://api.gsu.edu/studentDashboard/bannerData" | grep -q "$string"; then
    echo "'$string' found"
    exit 0
else
    echo "'$string' not found"
    exit 1
fi
'''
    }
}


