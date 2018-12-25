pipeline {
    agent { label env.NODE }
    environment {
        BASEURL = "baseurl="
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
    }
    parameters {
        choice(name: 'STABILITY', choices: ['dev', 'beta', 'alpha', 'rc', 'gold'], description: 'Select RPMs Stability Type')
        choice(name: 'VERSION', choices: ['11.3.0.0', '11.2.1.0'], description: 'Select RPMs version')
        string(name: 'SPECIFIC_RPM_BUILD', defaultValue: '', description: 'Insert the location of the Presidio RPMs you want to install. (No ssl use http)')
        booleanParam(name: 'RUN_CLEANUP', defaultValue: false, description: 'Run Presidio Cleanup and Logs Cleanup')
    }
    stages {
        stage('presidio-integration-test Project Clone') {
            steps {
                cleanWs()
                buildIntegrationTestProject()
            }
        }
        stage('UEBA Cleanup and RPMs Upgrade') {
            steps {
                script {
                    setBaseUrl ()
                    uebaPreparingEnv()
                }
            }
        }
    }
}

/******************************
 *   UEBA RPMs Installation   *
 ******************************/
def setBaseUrl (
        String rpmBuildPath = env.SPECIFIC_RPM_BUILD,
        String rpmVeriosn = env.VERSION,
        String stability = env.STABILITY
){
    String baseUrl = "baseurl="
    if (rpmBuildPath != '') {
        baseUrl = baseUrl + rpmBuildPath
        println (baseUrl)
    } else if (rpmVeriosn == '11.2.1.0'){
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.2/11.2.1/11.2.1.0-" + stability + "/"
        println (baseUrl)
    } else if (rpmVeriosn == '11.3.0.0'){
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.3/11.3.0/11.3.0.0-" + stability + "/"
        println (baseUrl)
    }
    baseUrlValidation = baseUrl.drop(8)
    baseUrlresponsecode = sh(returnStdout: true, script: "curl -o /dev/null -s -w \"%{http_code}\\n\" ${baseUrlValidation}").trim()
    if (baseUrlresponsecode == '200'){
        sh "sudo sed -i \"s|.*baseurl=.*|${baseUrl}|g\" /etc/yum.repos.d/tier2-rsa-nw-upgrade.repo"
    }
    else {
        error("RPM Location is Wrong- ${baseUrlValidation}")
    }
}

def uebaPreparingEnv (){
    if (RUN_CLEANUP){
        sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/dbsCleanup.sh"
        sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/logsCleanup.sh"
    }
    sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/install_upgrade_rpms.sh $env.VERSION"
}

/**************************
 * Project Build Pipeline *
 **************************/
def buildIntegrationTestProject(
        String repositoryName = "presidio-integration-test",
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String branchName = env.INTEGRATION_TEST_BRANCH_NAME) {
    sh "git config --global user.name \"${userName}\""
    sh "git clone https://${userName}:${userPassword}@github.rsa.lab.emc.com/asoc/presidio-integration-test.git"
    dir(repositoryName) {
        sh "git checkout ${branchName}"
    }
}

def mvnCleanInstall(){
    sh "cd ${env.WORKSPACE}/presidio-integration-test/"
    sh "mvn --fail-at-end -Dmaven.multiModuleProjectDirectory=presidio-integration-test -DskipTests -Duser.timezone=UTC -U clean install"
}

def runEnd2EndTestAutomation(){
    sh "cd ${env.WORKSPACE}/presidio-integration-test/"
    sh "mvn -Dmaven.multiModuleProjectDirectory=presidio-integration-test/presidio-integration-e2e-test/pom.xml -U -Dmaven.test.failure.ignore=false -Duser.timezone=UTC test"
}