pipeline {
    parameters {
        string(name: 'SPECIFIC_RPM_BUILD', defaultValue: '', description: 'specify the link to the RPMs e.q: http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/master/promoted/11978/11.4.0.0/RSA/')
        string(name: 'INTEGRATION_TEST_BRANCH_NAME', defaultValue: 'origin/master', description: '')
        string(name: 'MVN_TEST_OPTIONS', defaultValue: '-q -U -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
        string(name: 'SIDE_BRANCH_JOD_NUMBER', defaultValue: '', description: 'Write the "presidio-build-jars-and-packages" build number from which you want to install the PRMs')
        booleanParam(name: 'RESET_LOG_HYBRID', defaultValue: false, description: '')
        booleanParam(name: 'RESET_NETWORK_HYBRID', defaultValue: false, description: '')
        booleanParam(name: 'START_BROKER', defaultValue: false, description: '')
        booleanParam(name: 'RESET_PRESIDIO', defaultValue: false, description: '')
        booleanParam(name: 'INSTALL_UEBA_RPMS', defaultValue: false, description: '')
    }

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
        stage('Project Clone') {
            steps {
                script { currentBuild.displayName="#${BUILD_NUMBER} ${NODE_NAME}" }
                script { currentBuild.description = "${env.INTEGRATION_TEST_BRANCH_NAME}" }
                cleanWs()
                buildIntegrationTestProject()
                copyScripts()
            }
        }
        stage('Reset LogHybrid') {
            when {
                expression { return params.RESET_LOG_HYBRID }
            }
            steps {
                sh "bash /home/presidio/reset_ld_and_concentrator_hybrid_dbs.sh ${LOG_HYBRID_HOST} skip"
            }
        }
        stage('Reset NetworkHybrid') {
            when {
                expression { return params.RESET_NETWORK_HYBRID }
            }
            steps {
                sh "bash /home/presidio/reset_ld_and_concentrator_hybrid_dbs.sh ${NETWORK_HYBRID_HOST} skip"
            }
        }
        stage('Start Broker') {
            when {
                expression { return params.START_BROKER }
            }
            steps {
                sh "bash /home/presidio/reset_ld_and_concentrator_hybrid_dbs.sh skip ${BROKER_HOST}"
            }
        }
        stage('UEBA - RPMs Upgrade') {
            when {
                expression { return params.INSTALL_UEBA_RPMS }
            }
            steps {
                script {
                    setBaseUrl()
                    uebaInstallRPMs()
                }
            }
        }
       stage('UEBA - Reset Presidio') {
            when {
                expression { return params.RESET_PRESIDIO }
            }
            steps {
                script {
                    ResetPresidio()
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
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}setBrokerInputConfiguration.sh"
    sh "sudo systemctl start airflow-scheduler"
    
}

def ResetPresidio() {
    echo "Going to reset UEBA"
    sh "curl -k -I -u admin:netwitness https://${UEBA_HOST}/admin/airflow/trigger?dag_id=reset_presidio"
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
    dir(env.REPOSITORY_NAME) { sh "git checkout ${branchName}" }
}


def copyScripts() {
    sh "\\cp ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/reset_ld_and_concentrator_hybrid_dbs.sh /home/presidio/"
    sh "cp -f ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/env_properties_manager.sh /home/presidio/"
    sh "sudo bash /home/presidio/env_properties_manager.sh --create"
}
