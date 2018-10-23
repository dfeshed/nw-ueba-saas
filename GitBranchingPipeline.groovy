rsaAsocGitHubApiUrl = "https://github.rsa.lab.emc.com/api/v3/repos/asoc/"

pipeline {
    agent {
        label 'el7 && java8'
    }
    environment {
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
    }
    stages {
        stage('Git Branching Pipeline Initialization') {
            steps { cleanWs() }
        }
        stage('Branching in Presidio Test Utils') {
            when { expression { return env.BRANCH_IN_PRESIDIO_TEST_UTILS == 'true' } }
            steps { branchOutFromBaseToSide("presidio-test-utils") }
            post { failure { cleanRemoteRepository("presidio-test-utils") } }
        }
        stage('Branching in Presidio Core') {
            when { expression { return env.BRANCH_IN_PRESIDIO_CORE == 'true' } }
            steps { branchOutFromBaseToSide("presidio-core") }
            post { failure { cleanRemoteRepository("presidio-core") } }
        }
        stage('Branching in Presidio Flume') {
            when { expression { return env.BRANCH_IN_PRESIDIO_FLUME == 'true' } }
            steps { branchOutFromBaseToSide("presidio-flume") }
            post { failure { cleanRemoteRepository("presidio-flume") } }
        }
        stage('Branching in Presidio Netwitness') {
            when { expression { return env.BRANCH_IN_PRESIDIO_NETWITNESS == 'true' } }
            steps { branchOutFromBaseToSide("presidio-netwitness") }
            post { failure { cleanRemoteRepository("presidio-netwitness") } }
        }
        stage('Branching in Presidio UI') {
            when { expression { return env.BRANCH_IN_PRESIDIO_UI == 'true' } }
            steps { branchOutFromBaseToSide("presidio-ui") }
            post { failure { cleanRemoteRepository("presidio-ui") } }
        }
        stage('Branching in Presidio Integration Test') {
            when { expression { return env.BRANCH_IN_PRESIDIO_INTEGRATION_TEST == 'true' } }
            steps { branchOutFromBaseToSide("presidio-integration-test") }
            post { failure { cleanRemoteRepository("presidio-integration-test") } }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}

/**************************
 * Git Branching Pipeline *
 **************************/
def branchOutFromBaseToSide(
        String repositoryName,
        // Parameters with default values.
        String baseBranch = env.BASE_BRANCH,
        String sideBranch = env.SIDE_BRANCH,
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW) {

    String response = getAsocBranchReference(repositoryName, baseBranch, userName, userPassword)
    // Extract the SHA-1 value of the reference from the response.
    String sha = response.substring(response.indexOf("\"sha\": \""))
    sha = sha.substring(8, sha.indexOf("\","))
    createAsocBranchReference(repositoryName, sideBranch, sha, userName, userPassword)
}

def cleanRemoteRepository(
        String repositoryName,
        // Parameters with default values.
        String sideBranch = env.SIDE_BRANCH,
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW) {

    deleteAsocBranchReference(repositoryName, sideBranch, userName, userPassword)
}

/******************
 * cURL Utilities *
 ******************/
def getAsocBranchReference(String repositoryName, String branchName, String userName, String userPassword) {
    sh(returnStdout: true, script: """\
        curl -d '{}'\
        -X GET ${rsaAsocGitHubApiUrl}${repositoryName}/git/refs/heads/${branchName}\
        -u ${userName}:${userPassword}\
    """).trim()
}

def createAsocBranchReference(String repositoryName, String branchName, String sha, String userName, String userPassword) {
    sh """\
        curl -d '{"ref": "refs/heads/${branchName}", "sha": "${sha}"}'\
        -X POST ${rsaAsocGitHubApiUrl}${repositoryName}/git/refs/\
        -u ${userName}:${userPassword}\
    """
}

def deleteAsocBranchReference(String repositoryName, String branchName, String userName, String userPassword) {
    sh """\
        curl -d '{}'\
        -X DELETE ${rsaAsocGitHubApiUrl}${repositoryName}/git/refs/heads/${branchName}\
        -u ${userName}:${userPassword}\
    """
}
