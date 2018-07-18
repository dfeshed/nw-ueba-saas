rsaAsocGitHubApiUrl = "https://github.rsa.lab.emc.com/api/v3/repos/asoc/"
repositoryNameToPullRequestNumberMap = [:]

pipeline {
    agent {
        label 'el7 && java8'
    }
    environment {
        // The credentials associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        String[] REVIEWERS = REVIEWERS.replaceAll('\\s', '').split(',')
        REVIEWERS_AS_STRING = toJsonArrayAsString(REVIEWERS)
    }
    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }
        stage('Configure Git User Details') {
            steps {
                configGlobalRsaUserNameAndEmail("${env.RSA_BUILD_CREDENTIALS_USR}")
            }
        }
        stage('Presidio Test Utils Version Promotion') {
            steps {
                promoteProjectVersion("presidio-test-utils", [
                        "pom.xml": []
                ])
            }
            post {
                failure {
                    cleanRemoteRepository("presidio-test-utils")
                }
            }
        }
        stage('Presidio Core Version Promotion') {
            steps {
                promoteProjectVersion("presidio-core", [
                        "fortscale/pom.xml": ["presidio.test.utils"],
                        "package/pom.xml"  : []
                ])
            }
            post {
                failure {
                    cleanRemoteRepository("presidio-core")
                }
            }
        }
        stage('Presidio Flume Version Promotion') {
            steps {
                promoteProjectVersion("presidio-flume", [
                        "pom.xml"        : ["presidio.test.utils", "presidio.core.version"],
                        "package/pom.xml": []
                ])
            }
            post {
                failure {
                    cleanRemoteRepository("presidio-flume")
                }
            }
        }
        stage('Presidio Netwitness Version Promotion') {
            steps {
                promoteProjectVersion("presidio-netwitness", [
                        "presidio-core-extension/pom.xml": ["flume.version"],
                        "package/pom.xml"                : []
                ])
            }
            post {
                failure {
                    cleanRemoteRepository("presidio-netwitness")
                }
            }
        }
        stage('Presidio UI Version Promotion') {
            steps {
                promoteProjectVersion("presidio-ui", [
                        "pom.xml"        : ["presidio.core.version"],
                        "package/pom.xml": []
                ])
            }
            post {
                failure {
                    cleanRemoteRepository("presidio-ui")
                }
            }
        }
        stage('Presidio Integration Test Version Promotion') {
            steps {
                promoteProjectVersion("presidio-integration-test", [
                        "pom.xml": ["presidio.test.utils", "presidio.core.version", "flume.version"]
                ])
            }
            post {
                failure {
                    cleanRemoteRepository("presidio-integration-test")
                }
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}

/****************************
 * Version Promotion Pipeline
 ****************************/
def promoteProjectVersion(
        String repositoryName,
        Map<String, List<String>> pomFileToPropertiesMap,
        // Parameters with default values.
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String version = env.VERSION,
        String reviewersAsString = env.REVIEWERS_AS_STRING) {

    cloneAsocRepository(userName, userPassword, repositoryName)

    dir(repositoryName) {
        String branchName = buildBranchName(version)
        checkoutNewBranch(branchName)

        pomFileToPropertiesMap.each { pomFile, properties ->
            setProjectVersion(version, false, pomFile)
            properties.each { property -> setProjectPropertyVersion(property, version, true, false, pomFile) }
            testProject(pomFile)
            deployArtifacts(pomFile)
        }

        String message = "Promote project to version ${version}."
        commitAll(message)
        createAnnotatedTag(branchName, message)
        pushToOriginWithTags(branchName, branchName)
        String response = createAsocPullRequest(message, branchName, "master", repositoryName, userName, userPassword)

        // Extract the pull request number from the response and save it.
        String pullRequestNumber = response.substring(response.indexOf("\"number\": "))
        pullRequestNumber = pullRequestNumber.substring(10, pullRequestNumber.indexOf(","))
        repositoryNameToPullRequestNumberMap[repositoryName] = pullRequestNumber

        createAsocReviewRequest(reviewersAsString, repositoryName, pullRequestNumber, userName, userPassword)
        mergeAsocPullRequest(repositoryName, pullRequestNumber, userName, userPassword)
        deleteAsocBranch(repositoryName, branchName, userName, userPassword)
    }
}

def cleanRemoteRepository(
        String repositoryName,
        // Parameters with default values.
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String version = env.VERSION) {

    String pullRequestNumber = repositoryNameToPullRequestNumberMap[repositoryName]
    String branchName = buildBranchName(version)
    closeAsocPullRequest(repositoryName, pullRequestNumber, userName, userPassword)
    deleteAsocTag(repositoryName, branchName, userName, userPassword)
    deleteAsocBranch(repositoryName, branchName, userName, userPassword)
}

static def buildBranchName(String version) {
    "v${version}"
}

/*****************
 * Git Utilities *
 *****************/
def configGlobalUserName(String userName) {
    sh "git config --global user.name \"${userName}\""
}

def configGlobalUserEmail(String userEmail) {
    sh "git config --global user.email \"${userEmail}\""
}

def cloneAsocRepository(String userName, String userPassword, String repositoryName) {
    sh "git clone https://${userName}:${userPassword}@github.rsa.lab.emc.com/asoc/${repositoryName}.git"
}

def checkoutNewBranch(String branchName) {
    sh "git checkout -b ${branchName}"
}

def commitAll(String message) {
    sh "git commit -a -m \"${message}\""
}

def createAnnotatedTag(String tagName, String message) {
    sh "git tag -a \"${tagName}\" -m \"${message}\""
}

def pushToOriginWithTags(String localBranchName, String remoteBranchName) {
    sh "git push origin refs/heads/${localBranchName}:refs/heads/${remoteBranchName} --tags"
}

def configGlobalRsaUserNameAndEmail(String userName) {
    configGlobalUserName(userName)
    configGlobalUserEmail("${userName}@rsa.com")
}

/*******************
 * Maven Utilities *
 *******************/
def setProjectVersion(String newVersion, boolean generateBackupPoms, String pomFile) {
    sh "mvn versions:set -DnewVersion=${newVersion} -DgenerateBackupPoms=${generateBackupPoms} -f ${pomFile}"
}

def setProjectPropertyVersion(
        String property, String newVersion, boolean allowSnapshots, boolean generateBackupPoms, String pomFile) {

    sh """\
        mvn versions:set-property\
        -Dproperty=${property} -DnewVersion=${newVersion}\
        -DallowSnapshots=${allowSnapshots} -DgenerateBackupPoms=${generateBackupPoms}\
        -f ${pomFile}\
    """
}

def testProject(String pomFile) {
    sh "mvn test -f ${pomFile}"
}

def deployArtifacts(String pomFile) {
    sh "mvn deploy -f ${pomFile}"
}

/******************
 * cURL Utilities *
 ******************/
def createAsocPullRequest(
        String title, String head, String base, String repositoryName, String userName, String userPassword) {

    sh(returnStdout: true, script: """\
        curl -d '{"title": "${title}", "head": "${head}", "base": "${base}"}'\
        -X POST ${rsaAsocGitHubApiUrl}${repositoryName}/pulls\
        -u ${userName}:${userPassword}\
    """).trim()
}

def createAsocReviewRequest(
        String reviewersAsString, String repositoryName, String number, String userName, String userPassword) {

    sh """\
        curl -d '{"reviewers": ${reviewersAsString}}'\
        -X POST ${rsaAsocGitHubApiUrl}${repositoryName}/pulls/${number}/requested_reviewers\
        -u ${userName}:${userPassword}\
    """
}

def mergeAsocPullRequest(String repositoryName, String number, String userName, String userPassword) {
    // The default merge method is "merge" (as opposed to "squash" or "rebase").
    sh """\
        curl -d '{}'\
        -X PUT ${rsaAsocGitHubApiUrl}${repositoryName}/pulls/${number}/merge\
        -u ${userName}:${userPassword}\
    """
}

def closeAsocPullRequest(String repositoryName, String number, String userName, String userPassword) {
    sh """\
        curl -d '{"state": "closed"}'\
        -X PATCH ${rsaAsocGitHubApiUrl}${repositoryName}/pulls/${number}\
        -u ${userName}:${userPassword}\
    """
}

def deleteAsocTag(String repositoryName, String tagName, String userName, String userPassword) {
    sh """\
        curl -d '{}'\
        -X DELETE ${rsaAsocGitHubApiUrl}${repositoryName}/git/refs/tags/${tagName}\
        -u ${userName}:${userPassword}\
    """
}

def deleteAsocBranch(String repositoryName, String branchName, String userName, String userPassword) {
    sh """\
        curl -d '{}'\
        -X DELETE ${rsaAsocGitHubApiUrl}${repositoryName}/git/refs/heads/${branchName}\
        -u ${userName}:${userPassword}\
    """
}

/******************
 * JSON Utilities *
 ******************/
static def toJsonArrayAsString(String[] array) {
    String jsonArrayAsString = "["

    for (int i = 0; i < array.length; ++i) {
        jsonArrayAsString += "\"${array[i]}\""
        jsonArrayAsString += i == array.length - 1 ? "]" : ", "
    }

    return jsonArrayAsString
}
