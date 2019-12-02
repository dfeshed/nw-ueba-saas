rsaAsocGitHubApiUrl = "https://github.rsa.lab.emc.com/api/v3/repos/asoc/"
repositoryNameToPullRequestNumberMap = [:]

pipeline {
    agent {
        node {
            label 'el7 && java8'
        }
    }
    tools {
        jdk 'Java-11 EL7'
    }
    environment {
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
    }
    stages {
        stage('Version Promotion Pipeline Initialization') {
            steps {
                cleanWs()
                configGlobalRsaUserNameAndEmail("${env.RSA_BUILD_CREDENTIALS_USR}")

                script {
                    String[] arrayOfReviewers = env.REVIEWERS.replaceAll("\\s", "").split(",")
                    env.REVIEWERS = "["

                    for (int i = 0; i < arrayOfReviewers.length; ++i) {
                        env.REVIEWERS += "\"${arrayOfReviewers[i]}\""
                        env.REVIEWERS += i == arrayOfReviewers.length - 1 ? "]" : ", "
                    }
                }
            }
        }
        stage('Presidio Test Utils Version Promotion') {
            when { expression { return env.PROMOTE_PRESIDIO_TEST_UTILS == 'true' } }
            steps {
                promoteProjectVersion("presidio-test-utils", [
                        new MavenExecution(
                                pomFile: "pom.xml",
                                properties: [],
                                command: "mvn clean deploy -B -U -f pom.xml"
                        )
                ])
            }
            post {
                failure {
                    cleanRemoteRepository("presidio-test-utils")
                }
            }
        }
        stage('Presidio Core Version Promotion') {
            when { expression { return env.PROMOTE_PRESIDIO_CORE == 'true' } }
            steps {
                promoteProjectVersion("presidio-core", [
                        new MavenExecution(
                                pomFile: "fortscale/pom.xml",
                                properties: ["presidio.test.utils"],
                                command: "mvn clean deploy -B -U -f fortscale/pom.xml"
                        ),
                        new MavenExecution(
                                pomFile: "package/pom.xml",
                                properties: [],
                                command: null
                        )
                ])
            }
            post {
                failure {
                    cleanRemoteRepository("presidio-core")
                }
            }
        }
        stage('Presidio Flume Version Promotion') {
            when { expression { return env.PROMOTE_PRESIDIO_FLUME == 'true' } }
            steps {
                promoteProjectVersion("presidio-flume", [
                        new MavenExecution(
                                pomFile: "pom.xml",
                                properties: ["presidio.test.utils", "presidio.core.version"],
                                command: "mvn clean deploy -B -U -f pom.xml"
                        ),
                        new MavenExecution(
                                pomFile: "package/pom.xml",
                                properties: [],
                                command: null
                        )
                ])
            }
            post {
                failure {
                    cleanRemoteRepository("presidio-flume")
                }
            }
        }
        stage('Presidio Netwitness Version Promotion') {
            when { expression { return env.PROMOTE_PRESIDIO_NETWITNESS == 'true' } }
            steps {
                promoteProjectVersion("presidio-netwitness", [
                        new MavenExecution(
                                pomFile: "presidio-core-extension/pom.xml",
                                properties: ["flume.version"],
                                command: "mvn clean deploy -B -U -f presidio-core-extension/pom.xml"
                        ),
                        new MavenExecution(
                                pomFile: "package/pom.xml",
                                properties: [],
                                command: null
                        )
                ])
            }
            post {
                failure {
                    cleanRemoteRepository("presidio-netwitness")
                }
            }
        }
        stage('Presidio UI Version Promotion') {
            when { expression { return env.PROMOTE_PRESIDIO_UI == 'true' } }
            steps {
                promoteProjectVersion("presidio-ui", [
                        new MavenExecution(
                                pomFile: "pom.xml",
                                properties: ["presidio.core.version"],
                                command: """
                                    export SASS_BINARY_PATH=/mnt/libhq-SA/tools/node-sass/v4.9.0/linux-x64-46_binding.node &&
                                    export HTTP_PROXY=http://rsa-eng-proxy1:82 &&
                                    export HTTPS_PROXY=http://rsa-eng-proxy1:82 &&
                                    export NODE_TLS_REJECT_UNAUTHORIZED=0 &&
                                    export NO_PROXY="localhost,127.0.0.1,.emc.com" &&
                                    mvn clean deploy -B -U -f pom.xml
                                """
                        ),
                        new MavenExecution(
                                pomFile: "package/pom.xml",
                                properties: [],
                                command: null
                        )
                ])
            }
            post {
                failure {
                    cleanRemoteRepository("presidio-ui")
                }
            }
        }
        stage('Presidio Integration Test Version Promotion') {
            when { expression { return env.PROMOTE_PRESIDIO_INTEGRATION_TEST == 'true' } }
            steps {
                promoteProjectVersion("presidio-integration-test", [
                        new MavenExecution(
                                pomFile: "pom.xml",
                                properties: ["presidio.test.utils", "presidio.core.version", "flume.version"],
                                command: "mvn clean install -B -U -DskipTests -f pom.xml"
                        )
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

/******************************
 * Version Promotion Pipeline *
 ******************************/
def promoteProjectVersion(
        String repositoryName,
        List<MavenExecution> mavenExecutions,
        // Parameters with default values.
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String baseBranch = env.BASE_BRANCH,
        String version = env.VERSION,
        String addAnnotatedTag = env.ADD_ANNOTATED_TAG,
        String reviewers = env.REVIEWERS) {

    cloneAsocRepository(userName, userPassword, repositoryName)

    dir(repositoryName) {
        // Checkout locally the base branch.
        checkoutBranch(false, baseBranch)
        // Create and checkout locally the temporary side branch.
        String sideBranch = buildBranchName(version)
        checkoutBranch(true, sideBranch)

        mavenExecutions.each { mavenExecution ->
            // Promote the version of the project.
            setProjectVersion(version, false, mavenExecution.pomFile)

            // Promote the version of the project's properties.
            mavenExecution.properties.each { property ->
                setProjectPropertyVersion(property, version, true, false, mavenExecution.pomFile)
            }

            // Execute the Maven command.
            if (mavenExecution.command != null) {
                sh mavenExecution.command
            }
        }

        String message = "Promote project to version ${version} in branch ${baseBranch}"
        commitAll(message)
        if (addAnnotatedTag == 'true') createAnnotatedTag(sideBranch, message)
        pushToOriginWithTags(sideBranch, sideBranch)
        // Since the project was already built and tested, there is no need for the automated
        // PR-triggered Jenkins job to run, therefore "[skip ci]" is added to the PR body.
        String response = createAsocPullRequest(message, sideBranch, baseBranch, "[skip ci]", repositoryName, userName, userPassword)

        // Extract the pull request number from the response and save it.
        String pullRequestNumber = response.substring(response.indexOf("\"number\": "))
        pullRequestNumber = pullRequestNumber.substring(10, pullRequestNumber.indexOf(","))
        repositoryNameToPullRequestNumberMap[repositoryName] = pullRequestNumber

        createAsocReviewRequest(reviewers, repositoryName, pullRequestNumber, userName, userPassword)
        mergeAsocPullRequest(repositoryName, pullRequestNumber, userName, userPassword)
        deleteAsocBranch(repositoryName, sideBranch, userName, userPassword)
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

class MavenExecution {
    String pomFile
    List<String> properties
    String command
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

def checkoutBranch(boolean newBranch, String branchName) {
    sh "git checkout ${newBranch ? "-b" : ""} ${branchName}"
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

/******************
 * cURL Utilities *
 ******************/
def createAsocPullRequest(String title, String head, String base, String body, String repositoryName, String userName, String userPassword) {
    sh(returnStdout: true, script: """\
        curl -d '{"title": "${title}", "head": "refs/heads/${head}", "base": "refs/heads/${base}", "body": "${body}"}'\
        -X POST ${rsaAsocGitHubApiUrl}${repositoryName}/pulls\
        -u ${userName}:${userPassword}\
    """).trim()
}

def createAsocReviewRequest(
        String reviewers, String repositoryName, String number, String userName, String userPassword) {

    sh """\
        curl -d '{"reviewers": ${reviewers}}'\
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
