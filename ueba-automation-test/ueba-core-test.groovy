
pipeline {
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'master', description: '')
        string(name: 'MVN_TEST_OPTIONS', defaultValue: '-q -o -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')

        string(name: 'S3_BUCKET', defaultValue: 'presidio-automation-data', description: '')
        string(name: 'S3_TENANT', defaultValue: 'acme', description: '')
        string(name: 'S3_ACCOUNT', defaultValue: '123456789012', description: '')

        string(name: 'generator_format', defaultValue: 'MONGO_ADAPTER', description: '')
        string(name: 'pre_processing_configuration_scenario', defaultValue: 'CORE_MONGO', description: '')

        booleanParam(name: 'RESET_UEBA_DBS', defaultValue: true, description: '')
        booleanParam(name: 'INSTALL_UEBA_RPMS', defaultValue: false, description: '')
        booleanParam(name: 'DATA_INJECTION', defaultValue: true, description: '')
        booleanParam(name: 'DATA_PROCESSING', defaultValue: true, description: '')
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: '')
        choice(name: 'NODE_LABEL', choices: ['UEBA01','UEBA02','UEBA03','UEBA04'], description: '')
        choice(name: 'VERSION', choices: ['11.4.0.0','11.5.0.0'], description: 'RPMs version')
    }

    agent { label env.NODE_LABEL }

    environment {
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-11.0.5.10-0.el7_7.x86_64'
        OLD_UEBA_RPMS = sh(script: 'rpm -qa | grep rsa-nw-presidio-core | cut -d\"-\" -f5', returnStdout: true).trim()
        SCRIPTS_DIR = '/ueba-automation-framework/src/main/resources/scripts/'
    }

    stages {

        stage('Project Clone') {
            steps {
                sh 'pwd'
                sh 'whoami'
                script { currentBuild.displayName="#${BUILD_NUMBER} ${NODE_NAME}" }
                script { currentBuild.description = "${params.BRANCH_NAME}" }
                cleanWs()
                git branch: params.BRANCH_NAME, credentialsId: '67bd792d-ad28-4ebc-bd04-bef8526c3389', url: 'git@github.com:netwitness/ueba-automation-projects.git'
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
                sh "echo 'add install ueba rpms step'"
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
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/ueba-automation-test/target/log/processing/*.log'
        }
    }
}


def runSuiteXmlFile(String suiteXmlFile) {
    sh 'pwd'
    sh "echo JAVA_HOME=${env.JAVA_HOME}"
    sh "mvn test -B --projects ueba-automation-test --also-make -DsuiteXmlFile=${suiteXmlFile} ${params.MVN_TEST_OPTIONS}"
}

def cleanUebaDBs() {
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/cleanup.sh $VERSION $env.OLD_UEBA_RPMS"
    if (params.INSTALL_UEBA_RPMS == false) {
        sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/Initiate-presidio-services.sh $VERSION $env.OLD_UEBA_RPMS"
    }
}