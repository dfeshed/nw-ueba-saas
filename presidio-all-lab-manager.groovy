pipeline {
    parameters {
        string(name: 'SPECIFIC_RPM_BUILD', defaultValue: '', description: 'specify the link to the RPMs e.q: http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/master/promoted/11978/11.4.0.0/RSA/')
        string(name: 'INTEGRATION_TEST_BRANCH_NAME', defaultValue: 'origin/master', description: '')
        string(name: 'MVN_TEST_OPTIONS', defaultValue: '-q -U -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
        string(name: 'SIDE_BRANCH_JOD_NUMBER', defaultValue: '', description: 'Write the "presidio-build-jars-and-packages" build number from which you want to install the PRMs')

        string(name: 'ADMIN_SERVER_8', defaultValue: 'nw-08-adm.hzlab.lab.emc.com', description: '')


        booleanParam(name: 'INSTALL_UEBA_UI_RPMS', defaultValue: true, description: '')
        booleanParam(name: 'ENV_CLEANUP', defaultValue: true, description: '')
        booleanParam(name: 'DATA_INJECTION', defaultValue: true, description: '')
        booleanParam(name: 'DATA_PROCESSING', defaultValue: true, description: '')
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: '')
        booleanParam(name: 'LIVE_STATE_ON', defaultValue: true, description: ' Leave the scheduler run at the end of the test.\\rThe UEBA will continue to collect data at the end of the test (on Live State)')
    }
    //tools { jdk env.JDK }
    agent { label env.NODE }
    environment {
        JAVA_HOME = "${env.JAVA_HOME}"
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        SCRIPTS_DIR = '/ueba-automation-projects/ueba-automation-framework/src/main/resources/scripts/'
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        REPOSITORY_NAME = "ueba-automation-projects"
        OLD_UEBA_RPMS = sh(script: 'rpm -qa | grep rsa-nw-presidio-core | cut -d\"-\" -f5', returnStdout: true).trim()
    }

    stages {


        stage('UEBA-UI RPMs Upgrade') {
            when {
                expression { return params.INSTALL_UEBA_UI_RPMS }
            }
            steps {
                script {
                    sh "echo ADMIN_SERVER_IP=${params.ADMIN_SERVER_8}"
                    sh "sshpass -p \"netwitness\" ssh root@${params.ADMIN_SERVER_8} -o StrictHostKeyChecking=no UserKnownHostsFile=/dev/null 'bash -s' < ${WORKSPACE}/scripts/presidio-ui-update.sh"
                }
            }



        }
    }

}


/******************************
 *   UEBA RPMs Installation   *
 ******************************/
def setBaseUrl(
        String rpmBuildPath = params.SPECIFIC_RPM_BUILD,
        String rpmVeriosn = env.VERSION,
        String stability = env.STABILITY
) {
    String baseUrl = "baseurl="
    String osBaseUrl = 'baseurl=http://libhq-ro.rsa.lab.emc.com/SA/Platform/ci/master/promoted/latest/11.4.0.0/OS/'
    if (rpmBuildPath != '') {
        baseUrl = baseUrl + rpmBuildPath
        println(baseUrl)
        VERSION = "0"
    } else {
        String[] versionArray = rpmVeriosn.split("\\.")
        FirstDir = versionArray[0] + "." + versionArray[1]
        SecondDir = FirstDir + "." + versionArray[2]
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/" + FirstDir + "/" + SecondDir + "/" + rpmVeriosn + "-" + stability + "/"
    }
    baseUrlValidation = baseUrl.drop(8)
    baseUrlresponsecode = sh(returnStdout: true, script: "curl -o /dev/null -s -w \"%{http_code}\\n\" ${baseUrlValidation}").trim()
    if (baseUrlresponsecode == '200') {
        sh "sudo sed -i \"s|.*baseurl=.*|${baseUrl}|g\" /etc/yum.repos.d/tier2-rsa-nw-upgrade.repo"
        sh "sudo sed -i \"s|.*baseurl=.*|${osBaseUrl}|g\" /etc/yum.repos.d/tier2-mirrors.repo"
        sh "sudo sed -i \"s|enabled=.*|enabled=0|g\" /etc/yum.repos.d/*.repo"
        sh "sudo sed -i \"s|enabled=.*|enabled=1|g\" /etc/yum.repos.d/tier2-*.repo"
        sh "sudo yum clean all"
        sh "sudo rm -rf /var/cache/yum"
    } else {
        error("RPM Repository is Invalid - ${baseUrlValidation}")
    }
}

def uebaInstallRPMs() {
    if (params.SIDE_BRANCH_JOD_NUMBER == '') {
        sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/install_upgrade_rpms.sh $VERSION $env.OLD_UEBA_RPMS"
    } else {
        sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/install_side_branch_rpms.sh $params.SIDE_BRANCH_JOD_NUMBER"
    }
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/Initiate-presidio-services.sh $VERSION $OLD_UEBA_RPMS"
}

def CleanEpHybridUebaDBs() {
    sh "cp ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/reset_ld_and_concentrator_hybrid_dbs.sh /home/presidio/"
    sh "sudo bash /home/presidio/reset_ld_and_concentrator_hybrid_dbs.sh"
    sh "rm -f /home/presidio/reset_ld_and_concentrator_hybrid_dbs.sh"
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/cleanup.sh $VERSION $OLD_UEBA_RPMS"
    if (params.INSTALL_UEBA_RPMS == false) {
        sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/Initiate-presidio-services.sh $VERSION $OLD_UEBA_RPMS"
    }
}

/**************************
 * Project Build Pipeline *
 **************************/
def buildIntegrationTestProject(
        String repositoryName = "ueba-automation-projects",
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String branchName = params.INTEGRATION_TEST_BRANCH_NAME) {
    sh "git config --global user.name \"${userName}\""
    sh "git clone https://${userName}:${userPassword}@github.rsa.lab.emc.com/feshed/ueba-automation-projects.git"
    dir(env.REPOSITORY_NAME) {
        sh "git checkout ${branchName}"
    }
}

def mvnCleanInstall() {
    dir(env.REPOSITORY_NAME) {
        sh "/usr/local/src/apache-maven/bin/mvn --fail-at-end -Dmaven.multiModuleProjectDirectory=presidio-integration-test -DskipTests -Duser.timezone=UTC -U clean install"
    }
}

def runEnd2EndTestAutomation() {
    dir(env.REPOSITORY_NAME) {
        println(env.REPOSITORY_NAME)
        sh "/usr/local/src/apache-maven/bin/mvn -B -f presidio-integration-e2e-test/pom.xml -U -Dmaven.test.failure.ignore=false -Duser.timezone=UTC test"
    }
}


def runSuiteXmlFile(String suiteXmlFile) {
    println(env.REPOSITORY_NAME)
    sh "echo JAVA_HOME=${env.JAVA_HOME}"
    dir(env.REPOSITORY_NAME) {
        sh "/usr/local/src/apache-maven/bin/mvn test -B --projects ueba-automation-test --also-make -DsuiteXmlFile=${suiteXmlFile} ${params.MVN_TEST_OPTIONS}"
    }
}
def startAirflowScheduler(){
    if (params.LIVE_STATE_ON) {
        sh "sudo systemctl start airflow-scheduler"
    }

}
def copyScripts() {
    sh "cp -f ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/env_properties_manager.sh /home/presidio/"
    sh "cp -f ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/presidio-ui-update.sh /home/presidio/"
    sh "sudo bash /home/presidio/env_properties_manager.sh --create"
}