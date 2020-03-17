pipeline {
    parameters {
        string(name: 'SPECIFIC_RPM_BUILD', defaultValue: '', description: 'specify the link to the RPMs e.q: http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/master/promoted/11978/11.4.0.0/RSA/')
        string(name: 'INTEGRATION_TEST_BRANCH_NAME', defaultValue: '', description: '')
        string(name: 'MVN_TEST_OPTIONS', defaultValue: '-q -U -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
        string(name: 'SIDE_BRANCH_JOD_NUMBER', defaultValue: '', description: 'Write the "presidio-build-jars-and-packages" build number from which you want to install the PRMs')
        booleanParam(name: 'RESET_UEBA_DBS', defaultValue: true, description: '')
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: '')
    }
    agent { label env.NODE_LABEL }
    //tools { jdk env.JDK }
    environment {
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        SCRIPTS_DIR = '/ueba-automation-projects/ueba-automation-framework/src/main/resources/scripts/'
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        REPOSITORY_NAME = "ueba-automation-projects"
        OLD_UEBA_RPMS = sh(script: 'rpm -qa | grep rsa-nw-presidio-core | cut -d\"-\" -f5', returnStdout: true).trim()
        VERSION = setVersion()
        INTEGRATION_TEST_BRANCH_NAME = setBranchForTheTests()
    }

    stages {
        stage('Project Clone') {
            steps {
                script { currentBuild.displayName = "#${BUILD_NUMBER} ${NODE_NAME}" }
                script { currentBuild.description = "${env.INTEGRATION_TEST_BRANCH_NAME}" }
                cleanWs()
                buildIntegrationTestProject()
            }
        }

        stage('Prepare') {
            when {
                expression { return params.RESET_UEBA_DBS }
            }
            steps {
                build job: 'presidio-integration-test-core', parameters: [
                        string(name: 'NODE_LABEL', value: env.NODE_LABEL),
                        string(name: 'VERSION', value: env.VERSION),
                        booleanParam(name: 'RESET_UEBA_DBS', value: true),
                        booleanParam(name: 'INSTALL_UEBA_RPMS', value: true),

                        booleanParam(name: 'INSTALL_UEBA_UI_RPMS', value: false),
                        booleanParam(name: 'DATA_INJECTION', value: false),
                        booleanParam(name: 'DATA_PROCESSING', value: false),
                        booleanParam(name: 'RUN_TESTS', value: false)
                ]
            }
        }

        stage('Tests') {
            when {
                expression { return params.RUN_TESTS }
            }
            steps {
                runSuiteXmlFile('ade/ADE_Test.xml')
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: '**/ueba-automation-test/target/surefire-reports/junitreports/*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/ueba-automation-test/target/log/processing/*.log, **/ueba-automation-test/target/environment.properties'
            emailext attachLog: true, body: '$DEFAULT_CONTENT', postsendScript: '$DEFAULT_POSTSEND_SCRIPT', presendScript: '$DEFAULT_PRESEND_SCRIPT',
                    subject: '$DEFAULT_SUBJECT. Branch: $INTEGRATION_TEST_BRANCH_NAME on $NODE_LABEL', to: 'Dmitry.Feshenko@rsa.com'
        }
    }
}
/******************************
 *   UEBA RPMs Installation   *
 ******************************/
def setBaseUrl(String rpmBuildPath = params.SPECIFIC_RPM_BUILD, String rpmVeriosn = env.VERSION, String stability = env.STABILITY) {

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

def runSuiteXmlFile(String suiteXmlFile) {
    println(env.REPOSITORY_NAME)
    sh "echo JAVA_HOME=${env.JAVA_HOME}"
    dir(env.REPOSITORY_NAME) {
        sh "/usr/local/src/apache-maven/bin/mvn test -B --projects ueba-automation-test --also-make -DsuiteXmlFile=${suiteXmlFile} ${params.MVN_TEST_OPTIONS}"
    }
}

def setVersion() {
    if (params.VERSION && !"${params.VERSION}".isEmpty()) {
        return params.VERSION
    }

    def versions = ["11.4.1.0", "11.5.0.0"]
    def currentMillis = System.currentTimeMillis()
    int days = (currentMillis * versions.size()) / (1000 * 60 * 60 * 24)
    int selectedIndex = days % versions.size()
    println("Version to be tested " + versions[selectedIndex].toString())
    return versions[selectedIndex].toString()
}

def setBranchForTheTests() {
    if (params.INTEGRATION_TEST_BRANCH_NAME && !"${params.INTEGRATION_TEST_BRANCH_NAME}".isEmpty()) {
        return params.INTEGRATION_TEST_BRANCH_NAME
    }

    if (env.VERSION && "${env.VERSION})".contains("11.4.")) {
        return "origin/release/11.4.1"
    } else {
        return "origin/master"
    }
}