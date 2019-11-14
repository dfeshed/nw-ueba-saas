pipeline {
    parameters {

        string(name: 'BUILD_BRANCH', defaultValue: 'origin/master', description: '')
        string(name: 'MVN_OPTIONS', defaultValue: '-Dmaven.test.failure.ignore=false \n' +
                '-Duser.timezone=UTC -Dprobability_multiplier=0.001 -Dusers_multiplier=11', description: '')

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
        SCRIPTS_DIR = '/ueba-automation-projects/presidio-integration-performance-test/src/main/resources/scripts/'
    }

    stages {

        stage('Clean Workspace') {
            when {
                expression { return params.CLEAN_FILES }
            }
            steps {
                cleanWs()
            }
        }

        stage('Project Clone') {
            steps {
                script { currentBuild.displayName="#${BUILD_NUMBER} ${NODE_NAME}" }
                script { currentBuild.description = "${env.BUILD_BRANCH}" }
                buildIntegrationTestProject()
            }
        }
        stage('Create files') {
            when {
                expression { return params.CREATE_FILES }
            }
            steps {
                runMaven()
            }
        }
        stage('Split files') {
            when {
                expression { return params.SPLIT_FILES }
            }
            steps {
                sh "${env.WORKSPACE}${env.SCRIPTS_DIR}split_files.sh"
            }
        }
        stage('Insert to Broker') {
            when {
                expression { return params.UPLOAD_TO_BROKER }
            }
            steps {
                sh "${env.WORKSPACE}${env.SCRIPTS_DIR}insert_data.sh"
            }
        }
    }
}



def runMaven() {
    println(env.REPOSITORY_NAME)
    sh "echo JAVA_HOME=${env.JAVA_HOME}"
    dir(env.REPOSITORY_NAME) {
        sh "/usr/local/src/apache-maven/bin/mvn test -B --projects presidio-integration-performance-test --also-make " +
                "-Dtls_alerts_probability=${params.tls_alerts_probability} -Dtls_groups_to_create=${params.tls_groups_to_create} " +
                "-Dtls_events_per_day_per_group=${params.tls_events_per_day_per_group} ${params.MVN_TEST_OPTIONS}"
    }
}


/**************************
 * Project Build Pipeline *
 **************************/
def buildIntegrationTestProject(
        String userName = env.RSA_BUILD_CREDENTIALS_USR,
        String userPassword = env.RSA_BUILD_CREDENTIALS_PSW,
        String branchName = params.BUILD_BRANCH) {
    sh "git config --global user.name \"${userName}\""
    sh "git clone https://${userName}:${userPassword}@github.rsa.lab.emc.com/feshed/ueba-automation-projects.git"
    dir(env.REPOSITORY_NAME) { sh "git checkout ${branchName}" }
}
