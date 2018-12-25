pipeline {
    agent { label env.NODE }
    environment {
        BASEURL = "baseurl="
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
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
        error("RPM Location is Wrong: ${baseUrlValidation}")
    }
}

def uebaPreparingEnv (){
    schedulerActivity = sh(returnStdout: true, script: "systemctl is-active airflow-scheduler").trim()
    sh "rpm -qa | grep presidio"
    if (env.RUN_CLEANUP){
        sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/dbsCleanup.sh"
        sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/logsCleanup.sh"
    }
    sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/install_upgrade_rpms.sh $env.VERSION"
    if (!env.RUN_CLEANUP && schedulerActivity == 'active' ){
        sh "systemctl start airflow-scheduler"
        sh "systemctl start airflow-webserver"
    }
    sh "rpm -qa | grep presidio"
}

/**************************
 * Project Build Pipeline *
 **************************/
def buildIntegrationTestProject(
        //String repositoryName = "presidio-integration-test",
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW
        //String branchName = 'master'
        ) {
    sh "git config --global user.name \"${userName}\""
    sh "git clone https://${userName}:${userPassword}@github.rsa.lab.emc.com/asoc/presidio-integration-test.git"
    //dir(repositoryName) {
    //    sh "git checkout ${branchName}"
    //}
}
