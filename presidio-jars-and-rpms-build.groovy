pipeline {
    agent {
        node {label 'el7 && java8'}
    }
    options { timestamps () }
    environment {
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        SLUGIFY_USES_TEXT_UNIDECODE = 'yes'
        SASS_BINARY_PATH='/mnt/libhq-SA/tools/node-sass/v4.9.0/linux-x64-46_binding.node'
        HTTP_PROXY='http://emc-proxy1:82'
        HTTPS_PROXY='http://emc-proxy1:82'
        NODE_TLS_REJECT_UNAUTHORIZED=0
        NO_PROXY="localhost,127.0.0.1,.emc.com"
    }
    stages {
        stage('Presidio JARs and RPMs Build Pipeline Initialization') {
            steps {
                cleanWs()
                configGlobalRsaUserNameAndEmail("${env.RSA_BUILD_CREDENTIALS_USR}")
            }
        }
        stage('Presidio Test Utils JARs Build') {
            when { expression { return env.BUILD_PRESIDIO_TEST_UTILS == 'true' } }
            steps { buildProject("presidio-test-utils", "pom.xml", true, false) }
        }
        stage('Presidio Core JARs Build') {
            when { expression { return env.BUILD_PRESIDIO_CORE == 'true' } }
            steps { buildProject("presidio-core", "fortscale/pom.xml", true, false) }
        }
        stage('Presidio Core RPMs Build') {
            when { expression { return env.RUN_CORE_PACKAGES == 'true' } }
            steps { buildPackages("presidio-core", "package/pom.xml", true, false, true) }
        }
        stage('Trigger presidio-integration-test-ADE-master, presidio-integration-test-adapter-master, presidio-integration-test-input-master') {
            when { expression { return env.RUN_CORE_PACKAGES == 'true' && (env.BRANCH_NAME == "origin/master" || env.BRANCH_NAME.contains("/release/")) } }
            steps {
                build job: 'presidio-integration-test-ADE-master', parameters: [
                        [$class: 'StringParameterValue', name: 'STABILITY', value: env.STABILITY],
                        [$class: 'StringParameterValue', name: 'VERSION', value: env.VERSION],
                        [$class: 'StringParameterValue', name: 'BUILD_BRANCH', value: env.BRANCH_NAME]
                ], wait: false
                build job: 'presidio-integration-test-adapter-master', parameters: [
                        [$class: 'StringParameterValue', name: 'STABILITY', value: env.STABILITY],
                        [$class: 'StringParameterValue', name: 'VERSION', value: env.VERSION],
                        [$class: 'StringParameterValue', name: 'BUILD_BRANCH', value: env.BRANCH_NAME]
                ], wait: false
                build job: 'presidio-integration-test-input-master', parameters: [
                        [$class: 'StringParameterValue', name: 'STABILITY', value: env.STABILITY],
                        [$class: 'StringParameterValue', name: 'VERSION', value: env.VERSION],
                        [$class: 'StringParameterValue', name: 'BUILD_BRANCH', value: env.BRANCH_NAME]
                ] , wait: false
            }
        }
        stage('Presidio Flume JARs Build') {
            when { expression { return env.BUILD_PRESIDIO_FLUME == 'true' } }
            steps { buildProject("presidio-flume", "pom.xml", true, false) }
        }
        stage('Presidio Flume RPMs Build') {
            when { expression { return env.RUN_FLUME_PACKAGES == 'true' } }
            steps { buildPackages("presidio-flume", "package/pom.xml", true, false, false) }
        }
        stage('Presidio Netwitness JARs Build') {
            when { expression { return env.BUILD_PRESIDIO_NETWITNESS == 'true' } }
            steps { buildProject("presidio-netwitness", "presidio-core-extension/pom.xml", true, false) }
        }
        stage('Presidio Netwitness RPMs Build') {
            when { expression { return env.RUN_NW_PACKAGES == 'true' } }
            steps {buildPackages("presidio-netwitness", "package/pom.xml", true, false, true) }
        }
        stage('Presidio UI JARs Build') {
            when { expression { return env.BUILD_PRESIDIO_UI == 'true' } }
            steps { buildProject("presidio-ui", "pom.xml", true, false) }
        }
        stage('Presidio UI RPMs Build') {
            when { expression { return env.RUN_PRESIDIO_UI_PACKAGES == 'true' } }
            steps { buildPackages("presidio-ui", "package/pom.xml", true, false, false) }
        }
    }
    post {
        always {
            archivingJARsAndRPMs()
            cleanWs()
        }
    }
}

/**************************
 * Project Build Pipeline *
 **************************/
def buildProject(
        String repositoryName,
        String pomFile,
        boolean updateSnapshots,
        boolean debug,
        // Parameters with default values.
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String branchName = env.BRANCH_NAME,
        String deploy = env.DEPLOY_JARS) {

    cloneAsocRepository(userName, userPassword, repositoryName)

    dir(repositoryName) {
        checkoutBranch(branchName)
        mvnCleanInstall(deploy == 'true', pomFile, updateSnapshots, debug)
    }
}

/**************************
 * Packages Build Pipeline *
 **************************/
def buildPackages(
        String repositoryName,
        String pomFile,
        boolean updateSnapshots,
        boolean debug,
        boolean preStep,

        // Parameters with default values.
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String stability = env.STABILITY,
        String version = env.VERSION,
        String branchName = env.BRANCH_NAME,
        String deploy = env.DEPLOY_PACKAGES) {

    if (!fileExists(repositoryName)) {
        cloneAsocRepository(userName, userPassword, repositoryName)
    }

    dir(repositoryName) {
        checkoutBranch(branchName)
        if(env.EXTRACT_STABILITY_AND_VERSIOM_FROM_POM == 'true' ){
            (version, stability) = extractVersionAndStabilityFromPom(pomFile)
        }
        mvnCleanPackage(deploy, pomFile, stability, version, updateSnapshots, debug, preStep)
    }
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

def configGlobalRsaUserNameAndEmail(String userName) {
    configGlobalUserName(userName)
    configGlobalUserEmail("${userName}@rsa.com")
}

def cloneAsocRepository(String userName, String userPassword, String repositoryName) {
    sh "git clone https://${userName}:${userPassword}@github.rsa.lab.emc.com/asoc/${repositoryName}.git"
}

def checkoutBranch(String branchName) {
    sh "git checkout ${branchName}"
}

/*******************
 * Maven Utilities *
 *******************/
def mvnCleanInstall(boolean deploy, String pomFile, boolean updateSnapshots, boolean debug) {
    sh "mvn clean install ${deploy ? "deploy" : ""} -f ${pomFile} ${updateSnapshots ? "-U" : ""} ${debug ? "-X" : ""}"
}

def mvnCleanPackage(String deploy, String pomFile, String stability, String version, boolean updateSnapshots, boolean debug, boolean preStep) {
    if(preStep){
        sh "cp .pydistutils.cfg ~/.pydistutils.cfg"
    }
    sh "mvn -B -f ${pomFile} -Dbuild.stability=${stability.charAt(0)} -Dbuild.version=${version} -Dpublish=${deploy} clean package ${updateSnapshots ?  "-U" : ""} ${debug ? "-X" : ""} "
}

def extractVersionAndStabilityFromPom(pomFile){
    sh "echo Extracting version and stability from ${pomFile}"
    String version = findVersionInPom(pomFile)
    def versionSplitted = version.split(/\.|-/)
    version = versionSplitted[0] + "." + versionSplitted[1] + "." + versionSplitted[2] + "." + versionSplitted[3]
    String stability = versionSplitted.last().toLowerCase() == "snapshot" ? "1 - dev": "5 - gold"
    sh "echo Version: ${version}"
    sh "echo Stability: ${stability}"

    return [version, stability]
}

String findVersionInPom(String pomPath){
    def matcher = readFile(pomPath) =~ '<version>(.+?)</version>'
    def version = matcher ? matcher[0][1] : null
    if(version == null){
        error 'Couldn\'t extract pom version'
    }
    return version
}

def archivingJARsAndRPMs(){
    sh "cd ${env.WORKSPACE}"
    sh 'mkdir RPMs'
    sh 'mkdir JARs'
    sh 'find . -regex ".*/presidio-[^/]*.jar" -exec cp {} JARs \\;'
    sh 'find . -regex ".*-presidio-.*.rpm" -exec cp {} RPMs \\;'
    archiveArtifacts artifacts: 'JARs/**', allowEmptyArchive: true
    archiveArtifacts artifacts: 'RPMs/**', allowEmptyArchive: true
}