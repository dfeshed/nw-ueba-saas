pipeline {
    parameters {

        string(name: 'BUILD_BRANCH', defaultValue: 'origin/master', description: '')
        string(name: 'MVN_OPTIONS', defaultValue: '-q -U -DsuiteXmlFile=src/test/resources/PerformanceStabilityLogGenTest.xml ' +
                '-Dprobability_multiplier=0.001 -Dusers_multiplier=11', description: '')

        booleanParam(name: 'CLEAN_FILES', defaultValue: false, description: '')
        booleanParam(name: 'CREATE_FILES', defaultValue: false, description: '')
        booleanParam(name: 'SPLIT_FILES', defaultValue: false, description: '')
        booleanParam(name: 'UPLOAD_TO_BROKER', defaultValue: false, description: '')
    }

    agent { label env.NODE }

    environment {
        JAVA_HOME = "${env.JAVA_HOME}"
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        REPOSITORY_NAME = "ueba-automation-projects"
        SCRIPTS_DIR = '/ueba-automation-projects/ueba-performance-test/src/test/resources/scripts/'
        PERF_GEN_TARGET_PATH="${env.WORKSPACE}/ueba-automation-projects/ueba-performance-test/target/netwitness_events_gen/*"
        PERF_GEN_DATA_PATH="/var/netwitness/presidio/perf_data"
        PERF_GEN_GENERATED_PATH="/var/netwitness/presidio/perf_data/generated"
        PERF_GEN_DONE_PATH="/var/netwitness/presidio/perf_data/inserted"
    }

    stages {

        stage('Clean Files') {
            when {
                expression { return params.CLEAN_FILES }
            }
            steps {
                sh "rm -rf ${PERF_GEN_DATA_PATH}"
            }
        }

        stage('Project Clone') {
            steps {
                sh "ps -ef | grep 'maven' | grep -v grep | awk '{print \$2}' | xargs -r kill -9"
                sh "ps -ef | grep 'NwLogPlayer' | grep -v grep | awk '{print \$2}' | xargs -r kill -9"

                script { currentBuild.displayName="#${BUILD_NUMBER} ${NODE_NAME}" }
                script { currentBuild.description = "${env.START_TIME} - ${env.END_TIME}" }

                cleanWs()
                buildIntegrationTestProject()
            }
        }
        stage('Create files') {
            when {
                expression { return params.CREATE_FILES }
            }
            steps {
                runMaven()
                sh "echo \"moving files to ${PERF_GEN_GENERATED_PATH}\""
                sh "mkdir -p ${PERF_GEN_GENERATED_PATH}"
                sh "mkdir -p ${PERF_GEN_DONE_PATH}"
                sh "mv ${PERF_GEN_TARGET_PATH} ${PERF_GEN_GENERATED_PATH}"
            }
        }
        stage('Split files') {
            when {
                expression { return params.SPLIT_FILES }
            }
            steps {
                sh "sh ${env.WORKSPACE}${env.SCRIPTS_DIR}split_files.sh"
            }
        }
        stage('Insert to Broker') {
            when {
                expression { return params.UPLOAD_TO_BROKER }
            }
            steps {
                sh "sh ${env.WORKSPACE}${env.SCRIPTS_DIR}insert_data.sh"
            }
        }
    }
}



def runMaven() {
    println(env.REPOSITORY_NAME)
    sh "echo JAVA_HOME=${env.JAVA_HOME}"
    dir(env.REPOSITORY_NAME) {
        sh "mvn test -B --projects ueba-performance-test --also-make -Dschemas=${params.SCHEMAS} " +
                "-Dstart_time=${params.START_TIME} -Dend_time=${params.END_TIME} " +
                "-Dtls_alerts_probability=${params.tls_alerts_probability} -Dtls_groups_to_create=${params.tls_groups_to_create} " +
                "-Dtls_events_per_day_per_group=${params.tls_events_per_day_per_group} ${params.MVN_OPTIONS} " +
                "-Dmaven.test.failure.ignore=false -Duser.timezone=UTC"
    }
}


/**************************
 * Project Build Pipeline *
 **************************/
def buildIntegrationTestProject(
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String branchName = params.BUILD_BRANCH) {

    String URL="https://${userName}:${userPassword}@github.rsa.lab.emc.com/asoc/ueba-automation-projects.git"

    sh "git config --global user.name \"${userName}\""
    sh "if [[ ! -d \"${env.REPOSITORY_NAME}\" ]] ; then git clone ${URL} ; else cd \"ueba-automation-projects\" && git pull $URL ; fi"
    dir(env.REPOSITORY_NAME) { sh "git checkout ${branchName}" }
}