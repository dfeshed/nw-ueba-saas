pipeline {
    parameters {
        string(name: 'SPECIFIC_RPM_BUILD', defaultValue: '', description: 'specify the link to the RPMs e.q: http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/master/promoted/11978/11.4.0.0/RSA/')
        string(name: 'INTEGRATION_TEST_BRANCH_NAME', defaultValue: 'origin/master', description: '')
        string(name: 'MVN_TEST_OPTIONS', defaultValue: '-q -U -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
        string(name: 'SIDE_BRANCH_JOD_NUMBER', defaultValue: '', description: 'Write the "presidio-build-jars-and-packages" build number from which you want to install the PRMs')
        booleanParam(name: 'INSTALL_UEBA_RPMS', defaultValue: true, description: '')
        booleanParam(name: 'ENV_CLEANUP', defaultValue: true, description: '')
        booleanParam(name: 'DATA_INJECTION', defaultValue: true, description: '')
        booleanParam(name: 'DATA_PROCESSING', defaultValue: true, description: '')
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: '')
    }
    //tools { jdk env.JDK }
    agent { label env.NODE }
    environment {
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        SCRIPTS_DIR = '/ueba-automation-projects/ueba-automation-framework/src/main/resources/scripts/'
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        REPOSITORY_NAME = "ueba-automation-projects"
        OLD_UEBA_RPMS = sh(script: 'rpm -qa | grep rsa-nw-presidio-core | cut -d\"-\" -f5', returnStdout: true).trim()
    }

    stages {
        stage('Project Clone') {
            steps {
                script { currentBuild.displayName="#${BUILD_NUMBER} ${NODE_NAME}" }
                script { currentBuild.description = "${env.INTEGRATION_TEST_BRANCH_NAME}" }
                cleanWs()
                buildIntegrationTestProject()
                setBaseUrl()
                copyScripts()
            }
        }
        stage('Reset DBs LogHybrid and UEBA') {
            when {
                expression { return params.ENV_CLEANUP }
            }
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

        stage('Data Injection') {
            when {
                expression { return params.DATA_INJECTION }
            }
            steps {
                runSuiteXmlFile('e2e/E2E_DataInjection.xml')
            }
        }

        stage('Configurations and Processing') {
            when {
                expression { return params.DATA_PROCESSING }
            }
            steps {
                transformerConf()
                runSuiteXmlFile('e2e/E2E_ConfigAndDataProcessing.xml')
            }
        }

        stage('Run E2E Tests') {
            when {
                expression { return params.RUN_TESTS }
            }
            steps {
                runSuiteXmlFile('e2e/E2E_Tests.xml')
            }
        }
    }

    post {
        always {
            junit '**/ueba-automation-test/target/surefire-reports/junitreports/*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/ueba-automation-test/target/log/*.log, **/ueba-automation-test/target/environment.properties'
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
    String osBaseUrl = 'baseurl=http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/master/promoted/latest/11.4.0.0/OS/'
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
        sh "mvn --fail-at-end -Dmaven.multiModuleProjectDirectory=presidio-integration-test -DskipTests -Duser.timezone=UTC -U clean install"
    }
}

def runEnd2EndTestAutomation() {
    dir(env.REPOSITORY_NAME) {
        println(env.REPOSITORY_NAME)
        sh "mvn -B -f presidio-integration-e2e-test/pom.xml -U -Dmaven.test.failure.ignore=false -Duser.timezone=UTC test"
    }
}


def runSuiteXmlFile(String suiteXmlFile) {
    println(env.REPOSITORY_NAME)
    sh "echo JAVA_HOME=${env.JAVA_HOME}"
    dir(env.REPOSITORY_NAME) {
        sh "mvn test -B --projects ueba-automation-test --also-make -DsuiteXmlFile=${suiteXmlFile} ${params.MVN_TEST_OPTIONS}"
    }
}

def copyScripts() {
    sh "cp -f ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/env_properties_manager.sh /home/presidio/"
    sh "sudo bash /home/presidio/env_properties_manager.sh --create"
}

def transformerConf() {
    sh "echo workaround for TLS netname array"
    sh "sed -i 's|(.+) src|^((?!other).+) src|g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json"
    sh "sed -i 's|(.+) dst|^((?!other).+) dst|g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json"
}

