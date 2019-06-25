pipeline {
    agent {
        node {label 'el7 && java8'}
    }
    environment {
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        SLUGIFY_USES_TEXT_UNIDECODE = 'yes'
    }
    stages {
        stage('Project Build Pipeline Initialization') {
            steps {
                cleanWs()
                configGlobalRsaUserNameAndEmail("${env.RSA_BUILD_CREDENTIALS_USR}")
            }
        }
        stage('Presidio Test Utils Project Build') {
            when { expression { return env.BUILD_PRESIDIO_TEST_UTILS == 'true' } }
            steps { buildProject("presidio-test-utils", "pom.xml", true, false) }
        }
        stage('Presidio Core Project Build') {
            when { expression { return env.BUILD_PRESIDIO_CORE == 'true' } }
            steps { buildProject("presidio-core", "fortscale/pom.xml", true, false) }
        }
        stage('Presidio Core Package Build') {
            when { expression { return env.RUN_CORE_PACKAGES == 'true' } }
            steps { buildPackages("presidio-core", "package/pom.xml", true, false) }
        }
        stage('Presidio Flume Project Build') {
            when { expression { return env.BUILD_PRESIDIO_FLUME == 'true' } }
            steps { buildProject("presidio-flume", "pom.xml", true, false) }
        }
        stage('Presidio Flume Package Build') {
            when { expression { return env.RUN_FLUME_PACKAGES == 'true' } }
            steps { buildPackages("presidio-flume", "package/pom.xml", true, false) }
        }
        stage('Presidio Netwitness Project Build') {
            when { expression { return env.BUILD_PRESIDIO_NETWITNESS == 'true' } }
            steps { buildProject("presidio-netwitness", "presidio-core-extension/pom.xml", true, false) }
        }
        stage('Presidio Netwitness Package Build') {
            when { expression { return env.RUN_NW_PACKAGES == 'true' } }
            steps { buildPackages("presidio-netwitness", "package/pom.xml", true, false) }
        }
        stage('Presidio UI Project Build') {
            when { expression { return env.BUILD_PRESIDIO_UI == 'true' } }
            steps { buildProject("presidio-ui", "pom.xml", true, false) }
        }
        stage('Presidio UI Package Build') {
            when { expression { return env.RUN_PRESIDIO_UI_PACKAGES == 'true' } }
            steps { buildPackages("presidio-ui", "package/pom.xml", true, false) }
        }
        stage('print ~/.m2/repository') {
            steps { sh "find ~/.m2/repository -name \"*\" -print" }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: '**/presidio-*.jar', allowEmptyArchive: true
            archiveArtifacts artifacts: '**/*.rpm', allowEmptyArchive: true
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
        mvnCleanPackages(deploy, pomFile, stability, version, updateSnapshots, debug)
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

def mvnCleanPackages(String deploy, String pomFile, String stability, String version, boolean updateSnapshots, boolean debug) {
    sh "mvn -B -f ${pomFile} -Dbuild.stability=${stability.charAt(0)} -Dbuild.version=${version} -Dpublish=${deploy} clean package ${updateSnapshots ?  "-U" : ""} ${debug ? "-X" : ""} "
}
