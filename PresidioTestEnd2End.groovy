pipeline {
        agent { label env.NODE }
        environment {
            BASEURL = "baseurl="
            // The credentials (name + password) associated with the RSA build user.
            RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        }
        stages {
            stage('presidio-integration-test Project Build Pipeline Initialization') {
                steps {
                    cleanWs()
                    buildProject()
                }
            }
            stage('Upgrade UEBA RPMs') {
                steps {
                    script {
                        setBaseUrl ()
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
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.2/11.2.1/11.2.1.0-" + stability
        println (baseUrl)
    } else if (rpmVeriosn == '11.3.0.0'){
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.3/11.3.0/11.3.0.0-" + stability
        println (baseUrl)
    }
    sh "sudo sed -i \"s|.*baseurl=.*|${baseUrl}|g\" /etc/yum.repos.d/tier2-rsa-nw-upgrade.repo"
    sh "cat /etc/yum.repos.d/tier2-rsa-nw-upgrade.repo"
}

/**************************
 * Project Build Pipeline *
 **************************/
def buildProject(
        String repositoryName = "presidio-integration-test",
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String branchName = env.BRANCH_NAME) {
    sh "git config --global user.name \"${userName}\""
    sh "git clone https://${userName}:${userPassword}@github.rsa.lab.emc.com/asoc/presidio-integration-test.git"
    dir(repositoryName) {
        sh "git checkout ${branchName}"
        sh "mvn --fail-at-end -Dmaven.multiModuleProjectDirectory=presidio-integration-test -DskipTests -Duser.timezone=UTC -U clean install"
    }
}


