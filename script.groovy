def buildGoBinaries() {
    echo "Compiling and Building the application..."
    sh "go build"
}

def buildDockerImage() {
    echo "Building the Docker Image..."
    sh "docker build -t oumaymacharrad/data-seed-job:${IMAGE_VERSION} ."
}

def pushToDockerHub() {
    withCredentials([usernamePassword(credentialsId: "Docker-Hub-Credentials", passwordVariable: "PASS", usernameVariable: "USER")]) {
        echo "Pushing the Docker Image to Docker Hub..."
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh "docker push oumaymacharrad/data-seed-job:${IMAGE_VERSION}"
    }
}

def trivyScan(){
    echo "Running Trivy Security Scan..."
    sh "trivy image --format template --template '@/usr/local/share/trivy/templates/html.tpl' -o TrivyReport.html oumaymacharrad/data-seed-job:${IMAGE_VERSION} --scanners vuln"
}

def pushToDeploymentGitHub() {
    echo "Pushing to Deployment GitHub..."
}

return this
