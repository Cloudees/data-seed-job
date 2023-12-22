def incrementDataSeedJobVersion(){
    echo "Incrementing the Application Version"
    def currentVersion = sh(script: "grep 'const version' main.go | awk '{print \$NF}' | tr -d '\"'", returnStdout: true).trim()
    // Incrementing the Version
    def newVersion = incrementVersion(currentVersion)
    // Updating the Version in the Source Code
    sh "sed -i 's/const version = \"$currentVersion\"/const version = \"$newVersion\"/' main.go"
    // Commit the Changes
    sh "git checkout main"
    sh "git commit -am 'Increment Version to $newVersion'"
    // Push the Changes to GitHub
    withCredentials([usernamePassword(credentialsId: "GitHub-Credentials", passwordVariable: "GIT_PASSWORD", usernameVariable: "GIT_USERNAME")]) {
        sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/ayadi-mohamed/data-seed-job.git"
    }
    // Setting the New Version as an Environment Variable for Later Use
    env.IMAGE_VERSION = newVersion
}

def incrementVersion(currentVersion) {
    def versionParts = currentVersion.split("\\.")
    def newPatchVersion = versionParts[2].toInteger() + 1
    return "${versionParts[0]}.${versionParts[1]}.$newPatchVersion"
}

def buildGoBinary() {
    echo "Compiling and Building the Application..."
    sh "go build -o data-seed-job-${IMAGE_VERSION}"
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