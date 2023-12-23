def incrementDataSeedJobVersion(){
    echo "Incrementing the Application Version"
    def currentVersion = sh(script: "grep 'const version' main.go | awk '{print \$NF}' | tr -d '\"'", returnStdout: true).trim()
    // Incrementing the Version
    def newVersion = incrementVersion(currentVersion)
    // Updating the Version in the Source Code
    sh "sed -i 's/const version = \"$currentVersion\"/const version = \"$newVersion\"/' main.go"
    // Commit the Changes
    sh "git remote add oumayma git@github.com:Cloudees/data-seed-job.git"
    sh "git checkout main"
    sh "git commit -am 'Increment Version to $newVersion'"
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
    sh "mkdir data-seed-job-deployment"
    sh "cd data-seed-job-deployment" 
    sh "git clone https://github.com/Cloudees/deployment.git"
    sh "cd microservices/playlist"
    sh 'CURRENT_VERSION=$(grep "image: oumaymacharrad/data-seed-job" deployment-playlist.yaml | awk -F: \'{print \$3}\' | cut -d\'@\' -f1)'
    sh "sed -i \"s|- image: oumaymacharrad/data-seed-job:\${CURRENT_VERSION}|- image: oumaymacharrad/data-seed-job:\${IMAGE_VERSION}|\" deployment-playlist.yaml"
    sh "cd ../videos"
    sh "CURRENT_VERSION=$(grep 'image: oumaymacharrad/data-seed-job' deployment-videos.yaml | awk -F':' '{print \$3}' | cut -d'@' -f1)"
    sh "sed -i \"s|- image: oumaymacharrad/data-seed-job:\${CURRENT_VERSION}|- image: oumaymacharrad/data-seed-job:\${IMAGE_VERSION}|\" deployment-videos.yaml"
    sh "cd ../.."
    sh "git commit -am 'Increment Version to $newVersion'"
    sh "git push origin main"
}

def gitpush(){
    // Push the Changes to GitHub
    sshagent (credentials: ["Private-Key"]) {
        sh "git push oumayma main"
        sh "git remote remove oumayma"
    }
}

return this
