pipeline {
    parameters {
        string(name: 'generator_format', defaultValue: 'MONGO_ADAPTER', description: '')
        string(name: 'pre_processing_configuration_scenario', defaultValue: 'CORE_MONGO', description: '')

        string(name: 'SPECIFIC_RPM_BUILD', defaultValue: '', description: 'specify the link to the RPMs e.q: http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/master/promoted/11978/11.4.0.0/RSA/')
        string(name: 'INTEGRATION_TEST_BRANCH_NAME', defaultValue: 'origin/master', description: '')
        string(name: 'MVN_TEST_OPTIONS', defaultValue: '-q -U -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
        string(name: 'SIDE_BRANCH_JOD_NUMBER', defaultValue: '', description: 'Write the "presidio-build-jars-and-packages" build number from which you want to install the PRMs')
        booleanParam(name: 'RESET_UEBA_DBS', defaultValue: true, description: '')
        booleanParam(name: 'INSTALL_UEBA_RPMS', defaultValue: true, description: '')
        booleanParam(name: 'INSTALL_UEBA_UI_RPMS', defaultValue: true, description: '')
        booleanParam(name: 'DATA_INJECTION', defaultValue: true, description: '')
        booleanParam(name: 'DATA_PROCESSING', defaultValue: true, description: '')
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: '')
        //choice(name: 'STABILITY', choices: ['dev (default)','beta','alpha','rc','gold'], description: 'RPMs stability type')
        //choice(name: 'VERSION', choices: ['11.4.0.0','11.3.0.0','11.3.1.0','11.2.1.0'], description: 'RPMs version')
        //choice(name: 'NODE_LABLE', choices: ['','','nw-hz-03-ueba','nw-hz-04-ueba','nw-hz-05-ueba','nw-hz-06-ueba','nw-hz-07-ueba'], description: '')
    }
    agent { label env.NODE_LABLE }
    //tools { jdk env.JDK }
    environment {
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        SCRIPTS_DIR = '/ueba-automation-projects/ueba-automation-framework/src/main/resources/scripts/'
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        REPOSITORY_NAME = "ueba-automation-projects"
        OLD_UEBA_RPMS = sh(script: 'rpm -qa | grep rsa-nw-presidio-core | cut -d\"-\" -f5', returnStdout: true).trim()
    }

    stages {
        stage('presidio-integration-test Project Clone') {
            steps {
                script { currentBuild.displayName="#${BUILD_NUMBER} ${NODE_NAME}" }
                script { currentBuild.description = "${env.INTEGRATION_TEST_BRANCH_NAME}" }
                cleanWs()
                buildIntegrationTestProject()
                copyScripts()
            }
        }
        stage('Reset UEBA DBs') {
            when {
                expression { return params.RESET_UEBA_DBS }
            }
            steps {
                cleanUebaDBs()
            }
        }
        stage('Install UEBA RPMs') {
            when {
                expression { return params.INSTALL_UEBA_RPMS }
            }
            steps {
                setBaseUrl()
                uebaInstallRPMs()
            }
        }
        stage('UEBA-UI RPMs Upgrade') {
            environment {
                ADMIN_SERVER_IP = sh (script: 'sh /home/presidio/env_properties_manager.sh --get admin-server', returnStdout: true).trim()
            }
            when {
                expression { return params.INSTALL_UEBA_UI_RPMS }
            }
            steps {
                script {
                    sh "echo ADMIN_SERVER_IP=${env.ADMIN_SERVER_IP}"
                    sh "sshpass -p \"netwitness\" ssh root@${env.ADMIN_SERVER_IP} -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null 'bash -s' < /home/presidio/presidio-ui-update.sh"
                }
            }
        }

        stage('Data Injection') {
            when {
                expression { return params.DATA_INJECTION }
            }
            steps {
                runSuiteXmlFile('core/CoreDataInjection.xml')
            }
        }

        stage('Data Processing') {
            when {
                expression { return params.DATA_PROCESSING }
            }
            steps {
                runSuiteXmlFile('core/CoreDataProcessing.xml')
            }
        }

        stage('Tests') {
            when {
                expression { return params.RUN_TESTS }
            }
            steps {
                runSuiteXmlFile('core/CoreTests.xml')
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: '**/ueba-automation-test/target/surefire-reports/junitreports/*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/ueba-automation-test/target/log/processing/*.log, **/ueba-automation-test/target/environment.properties'
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
    String nwOsBaseUrl = ""
    String nwRsaBaseUrl = ""
    String baseUrl = "baseurl="
    String osBaseUrl = 'baseurl=http://libhq-ro.rsa.lab.emc.com/SA/Platform/ci/master/promoted/latest/11.5.0.0/OS/'
    if (env.VERSION == '11.4.0.0') {
        nwOsBaseUrl = 'baseurl=http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/maintenance/11.4/promoted/14000/11.4.0.0/OS/'
        nwRsaBaseUrl = 'baseurl=http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/maintenance/11.4/promoted/14000/11.4.0.0/RSA/'
    }
    else{
        nwOsBaseUrl = 'baseurl=http://libhq-ro.rsa.lab.emc.com/SA/Platform/ci/master/promoted/latest/11.5.0.0/OS/'
        nwRsaBaseUrl = 'baseurl=http://libhq-ro.rsa.lab.emc.com/SA/Platform/ci/master/promoted/latest/11.5.0.0/RSA/'
    }
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
        sh "sudo sed -i \"s|.*baseurl=.*|${nwOsBaseUrl}|g\" /etc/yum.repos.d/nw-os-base.repo"
        sh "sudo sed -i \"s|.*baseurl=.*|${nwRsaBaseUrl}|g\" /etc/yum.repos.d/nw-rsa-base.repo"
        sh "sudo sed -i \"s|enabled=.*|enabled=1|g\" /etc/yum.repos.d/*.repo"
        sh "OWB_ALLOW_NON_FIPS=on sudo yum clean all"
        sh "sudo rm -rf /var/cache/yum"
    } else {
        error("RPM Repository is Invalid - ${baseUrlValidation}")
    }
}

def cleanUebaDBs() {
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/cleanup.sh $VERSION $env.OLD_UEBA_RPMS"
    if (params.INSTALL_UEBA_RPMS == false) {
        sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/Initiate-presidio-services.sh $VERSION $env.OLD_UEBA_RPMS"
    }
}

def uebaInstallRPMs() {
    if (params.SIDE_BRANCH_JOD_NUMBER == '') {
        sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/install_upgrade_rpms.sh $VERSION $env.OLD_UEBA_RPMS"
    }
    else {
        sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/install_side_branch_rpms.sh $params.SIDE_BRANCH_JOD_NUMBER"
    }
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/Initiate-presidio-services.sh $VERSION $env.OLD_UEBA_RPMS"
}

/**************************
 * Project Build Pipeline *  https://github.rsa.lab.emc.com/asoc/ueba-automation-projects.git
 **************************/
def buildIntegrationTestProject(
        String repositoryName = "ueba-automation-projects",
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String branchName = env.INTEGRATION_TEST_BRANCH_NAME) {
    sh "git config --global user.name \"${userName}\""
    sh "git clone https://${userName}:${userPassword}@github.rsa.lab.emc.com/asoc/ueba-automation-projects.git"
    dir(env.REPOSITORY_NAME) {
        sh "git checkout ${branchName}"
    }
}

def mvnCleanInstall() {
    dir(env.REPOSITORY_NAME) {
        sh "/usr/local/src/apache-maven/bin/mvn --fail-at-end -Dmaven.multiModuleProjectDirectory=${env.REPOSITORY_NAME} -DskipTests -Duser.timezone=UTC -U clean install"
    }
}

def runSuiteXmlFile(String suiteXmlFile) {
    println(env.REPOSITORY_NAME)
    sh "echo JAVA_HOME=${env.JAVA_HOME}"
    dir(env.REPOSITORY_NAME) {
        sh "/usr/local/src/apache-maven/bin/mvn test -B --projects ueba-automation-test --also-make -DsuiteXmlFile=${suiteXmlFile} ${params.MVN_TEST_OPTIONS} -Dgenerator_format=${params.generator_format} -Dpre_processing_configuration_scenario=${params.pre_processing_configuration_scenario}"
    }
}

def copyScripts() {
    sh "cp -f ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/env_properties_manager.sh /home/presidio/"
    sh "cp -f ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/presidio-ui-update.sh /home/presidio/"
    sh "sudo bash /home/presidio/env_properties_manager.sh --create"
}
