pipeline {
    agent { label env.NODE }
    environment {
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        REPOSITORY_NAME = "presidio-integration-test"
    }

    stages {
        stage('presidio-integration-test Project Clone') {
            steps {
                println("Running on node- " + env.NODE)
                cleanWs()
                buildIntegrationTestProject()
                setBaseUrl()
            }
        }
        stage('Reset DBs LogHybrid and UEBA') {
            steps {
                CleanEpHybridUebaDBs()
            }
        }
        stage('UEBA - RPMs Upgrade') {
            when {
                expression { return params.INSTALL_UEBA_RPMS }
            }
            steps {
                script {
                    uebaInstallRPMs()
                }
            }
        }
        stage('presidio-integration-test Project Build Pipeline Initialization') {
            steps {
                script {
                    mvnCleanInstall()
                }
            }
        }
        stage('End 2 End Test Automation') {
            steps {
                script {
                    runEnd2EndTestAutomation()
                }
            }
        }
    }
}
old_UebaRpmsVresion = ""
/******************************
 *   UEBA RPMs Installation   *
 ******************************/
def setBaseUrl(
        String rpmBuildPath = env.SPECIFIC_RPM_BUILD,
        String rpmVeriosn = env.VERSION,
        String stability = env.STABILITY
) {
    String baseUrl = "baseurl="
    if (rpmBuildPath != '') {
        baseUrl = baseUrl + rpmBuildPath
        println(baseUrl)
    } else {
        String[] versionArray = rpmVeriosn.split("\\.")
        FirstDir = versionArray[0] + "." + versionArray[1]
        SecondDir = FirstDir + "." + versionArray[2]
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/" + FirstDir + "/" + SecondDir + "/" + rpmVeriosn + "-" + stability + "/"
        osBaseUrl = 'baseurl=http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/master/promoted/latest/11.4.0.0/OS/'
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
    oldUebaRpmsVresion = sh (script: 'rpm -qa | grep rsa-nw-presidio-core | cut -d\"-\" -f5', returnStdout: true).trim()
}

def uebaInstallRPMs() {
    sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/install_upgrade_rpms.sh $env.VERSION $env.oldUebaRpmsVresion"
    sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/Initiate-presidio-services.sh $env.VERSION $env.oldUebaRpmsVresion"

}

def CleanEpHybridUebaDBs() {
    sh "echo3  $env.oldUebaRpmsVresion"
    sh "echo 2 ${oldUebaRpmsVresion}"
    sh "cp ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/reset_ld_and_concentrator_hybrid_dbs.sh /home/presidio/"
    sh "sudo bash /home/presidio/reset_ld_and_concentrator_hybrid_dbs.sh"
    sh "rm -f /home/presidio/reset_ld_and_concentrator_hybrid_dbs.sh"
    sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/cleanup.sh $env.VERSION $env.oldUebaRpmsVresion"
    if (params.INSTALL_UEBA_RPMS == false) {
        sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/Initiate-presidio-services.sh $env.VERSION $env.oldUebaRpmsVresion"
    }
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
    dir(env.REPOSITORY_NAME) {
        sh "git checkout ${branchName}"
    }
}

def mvnCleanInstall() {
    dir(env.REPOSITORY_NAME) {
        sh "mvn --fail-at-end -Dmaven.multiModuleProjectDirectory=presidio-integration-test -DskipTests -Duser.timezone=UTC -U clean install"
    }
}

def runEnd2EndTestAutomation() {
    dir(env.REPOSITORY_NAME) {
        println(env.REPOSITORY_NAME)
        sh "mvn -B -f presidio-integration-e2e-test/pom.xml -U -Dmaven.test.failure.ignore=false -Duser.timezone=UTC test"
    }
}