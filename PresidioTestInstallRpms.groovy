pipeline {
    agent { label env.NODE }
    environment {
        BASEURL = "baseurl="
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        OWB_ALLOW_NON_FIPS = "on"
    }

    stages {
        stage('presidio-integration-test Project Clone') {
            steps {
                println("Running on node- " + env.NODE)
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
        FirstDir=versionArray[0] + "." + versionArray[1]
        SecondDir= FirstDir + "." + versionArray[2]
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/" + FirstDir + "/" + SecondDir +  "/" + rpmVeriosn  + "-" + stability + "/"
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
}


def uebaPreparingEnv (){
    runCleanup = env.RUN_CLEANUP
    schedulerActivity = sh(returnStdout: true, script: 'systemctl is-active airflow-scheduler || exit 0').trim()
    if (runCleanup == 'true'){
        sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/cleanup.sh $env.VERSION"
    }
    sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/install_upgrade_rpms.sh $env.VERSION"
    sh "bash ${env.WORKSPACE}/presidio-integration-test/presidio-integration-common/src/main/resources/Initiate-presidio-services.sh $env.VERSION"
    if (runCleanup == 'false' && schedulerActivity == 'active' ){
       sleep 30
       sh "sudo systemctl start airflow-scheduler"
       sh "sudo systemctl start airflow-webserver"
    }
    println ('Presidio RPMs After The Upgrade')
    sh "rpm -qa | grep presidio"
}

/**************************
 * Project Build Pipeline *
 **************************/
def buildIntegrationTestProject(
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW
        ) {
    sh "git config --global user.name \"${userName}\""
    sh "git clone https://${userName}:${userPassword}@github.rsa.lab.emc.com/asoc/presidio-integration-test.git"
}
