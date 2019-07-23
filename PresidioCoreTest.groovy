pipeline {
    parameters {
        string(name: 'SPECIFIC_RPM_BUILD', defaultValue: '', description: 'specify the link to the RPMs e.q: http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/master/promoted/11978/11.4.0.0/RSA/')
        string(name: 'INTEGRATION_TEST_BRANCH_NAME', defaultValue: 'origin/master', description: '')
        string(name: 'MVN_TEST_OPTIONS', defaultValue: '-U -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
        booleanParam(name: 'RUN_ONLY_TESTS', defaultValue: false, description: '')
        booleanParam(name: 'RESET_UEBA_DBS', defaultValue: true, description: '')
        booleanParam(name: 'INSTALL_UEBA_RPMS', defaultValue: true, description: '')
        booleanParam(name: 'DATA_INJECTION', defaultValue: true, description: '')
        booleanParam(name: 'DATA_PROCESSING', defaultValue: true, description: '')
        booleanParam(name: 'TEST_AUTOMATION', defaultValue: true, description: '')
        //choice(name: 'STABILITY', choices: ['dev (default)','beta','alpha','rc','gold'], description: 'RPMs stability type')
        //choice(name: 'VERSION', choices: ['11.4.0.0','11.3.0.0','11.3.1.0','11.2.1.0'], description: 'RPMs version')
        //choice(name: 'NODE_LABLE', choices: ['','','nw-hz-03-ueba','nw-hz-04-ueba','nw-hz-05-ueba','nw-hz-06-ueba','nw-hz-07-ueba'], description: '')
    }
    agent { label env.NODE_LABLE }
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
                cleanWs()
                buildIntegrationTestProject()
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
//        stage('Project Initialization') {
//            steps {
//                mvnCleanInstall()
//            }
//        }

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

        stage('Test automation') {
            when {
                expression { return params.TEST_AUTOMATION }
            }
            steps {
                runSuiteXmlFile('core/CoreTests.xml')
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
    String osBaseUrl = 'baseurl=http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/master/promoted/latest/11.4.0.0/OS/'
    if (rpmBuildPath != '') {
        baseUrl = baseUrl + rpmBuildPath
        println(baseUrl)
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

def cleanUebaDBs() {
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/cleanup.sh $env.VERSION $env.OLD_UEBA_RPMS"
    if (params.INSTALL_UEBA_RPMS == false) {
        sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/Initiate-presidio-services.sh $env.VERSION $env.OLD_UEBA_RPMS"
    }
}

def uebaInstallRPMs() {
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/install_upgrade_rpms.sh $env.VERSION $env.OLD_UEBA_RPMS"
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/Initiate-presidio-services.sh $env.VERSION $env.OLD_UEBA_RPMS"
}

/**************************
 * Project Build Pipeline *  https://github.rsa.lab.emc.com/feshed/ueba-automation-projects.git
 **************************/
def buildIntegrationTestProject(
        String repositoryName = "ueba-automation-projects",
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String branchName = env.INTEGRATION_TEST_BRANCH_NAME) {
    sh "git config --global user.name \"${userName}\""
    sh "git clone https://${userName}:${userPassword}@github.rsa.lab.emc.com/feshed/ueba-automation-projects.git"
    dir(env.REPOSITORY_NAME) {
        sh "git checkout ${branchName}"
    }
}

def mvnCleanInstall() {
    dir(env.REPOSITORY_NAME) {
        sh "mvn --fail-at-end -Dmaven.multiModuleProjectDirectory=${env.REPOSITORY_NAME} -DskipTests -Duser.timezone=UTC -U clean install"
    }
}

def runSuiteXmlFile(String suiteXmlFile) {
    println(env.REPOSITORY_NAME)
    dir(env.REPOSITORY_NAME) {
        sh "mvn test -B --projects ueba-automation-test --also-make -DsuiteXmlFile=${suiteXmlFile} ${params.MVN_TEST_OPTIONS}"
    }
}
