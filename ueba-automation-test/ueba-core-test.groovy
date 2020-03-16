pipeline {
    options {
        timeout(time: 90, unit: 'MINUTES')
    }

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'master', description: '')
        string(name: 'MVN_TEST_OPTIONS', defaultValue: '-q -o -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
        choice(name: 'IS_MONGO_PASSWORD_ENCRYPTED', choices: ['false', 'true'], description: '')

        string(name: 'S3_BUCKET', defaultValue: 'presidio-automation-data', description: '')
        string(name: 'S3_TENANT', defaultValue: 'acme', description: '')
        string(name: 'S3_APPLICATION', defaultValue: 'NetWitness', description: '')
        string(name: 'S3_ACCOUNT', defaultValue: '', description: 'Empty -> take last timestamp')

        choice(name: 'generator_format', choices: ['S3_JSON_GZIP', 'MONGO_ADAPTER'], description: '')
        choice(name: 'pre_processing_configuration_scenario', choices: ['CORE_S3', 'CORE_MONGO'], description: '')

        booleanParam(name: 'START_STOP_EC2_INSTANCE', defaultValue: true, description: '')
        booleanParam(name: 'RESET_UEBA_DBS', defaultValue: true, description: '')
        booleanParam(name: 'INSTALL_UEBA_RPMS', defaultValue: true, description: '')
        booleanParam(name: 'DATA_PROCESSING', defaultValue: true, description: '')
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: '')
        choice(name: 'VERSION', choices: ['11.4.0.0', '11.5.0.0'], description: 'RPMs version')
    }

    agent { label 'master' }

    environment {
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        SCRIPTS_DIR = '/ueba-automation-framework/src/main/resources/scripts/'
        S3_ACCOUNT = getAccountID()
        AWS_REGION = 'us-east-1'
    }

    stages {

        stage('Start UEBA VMs') {
            when { expression { return params.START_STOP_EC2_INSTANCE } }

            steps {
                build job: 'ueba-nodes-actions', parameters: [
                        string(name: 'NODE_LABEL', value: env.NODE_LABEL),
                        string(name: 'ACTION', value: 'start')
                ]
            }
        }

        stage('Project Clone') {
            agent { label env.NODE_LABEL }

            steps {
                sh 'pwd'
                sh 'whoami'
                script { currentBuild.displayName = "#${BUILD_NUMBER} ${NODE_NAME}" }
                script { currentBuild.description = "${params.BRANCH_NAME}" }
                cleanWs()
                git branch: params.BRANCH_NAME, credentialsId: '67bd792d-ad28-4ebc-bd04-bef8526c3389', url: 'git@github.com:netwitness/ueba-automation-projects.git'
                editApplicationProperties()
            }
        }

        stage('Reset UEBA DBs') {
            agent { label env.NODE_LABEL }
            when { expression { return params.RESET_UEBA_DBS } }

            steps {
                cleanUebaDBs()
            }
        }


        stage('Install UEBA RPMs') {
            agent { label env.NODE_LABEL }
            when { expression { return params.INSTALL_UEBA_RPMS } }

            steps {
                script {
                    println ' ********** Going to upgrade UEBA RPMs **********'
                    def rpms_app = ['rsa-nw-presidio-airflow',
                                    'rsa-nw-presidio-configserver',
                                    'rsa-nw-presidio-core',
                                    'rsa-nw-presidio-elasticsearch-init',
                                    'rsa-nw-presidio-ext-netwitness',
                                    'rsa-nw-presidio-flume',
                                    'rsa-nw-presidio-manager',
                                    'rsa-nw-presidio-output',
                                    'rsa-nw-presidio-ui']

                    for (String item : rpms_app) {
                        println item
                        sh "OWB_ALLOW_NON_FIPS=on && sudo yum -y update $item"
                    }
                    println ' ********** UEBA RPMs upgrade finished **********'
                }
            }
        }

        stage('Initiates Airflow') {
            agent { label env.NODE_LABEL }
            when { expression { return params.INSTALL_UEBA_RPMS } }

            steps {
                sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/Initiate-presidio-app-services.sh"
            }
        }

        stage('Data Injection') {
            agent { label env.NODE_LABEL }
            when {
                expression { return params.DATA_INJECTION }
            }
            steps {
                runSuiteXmlFile('core/CoreDataInjection.xml')
            }
        }

        stage('Data Processing') {
            agent { label env.NODE_LABEL }
            when { expression { return params.DATA_PROCESSING } }

            steps {
                runSuiteXmlFile('core/CoreDataProcessing.xml')
            }
        }

        stage('Tests') {
            agent { label env.NODE_LABEL }
            when { expression { return params.RUN_TESTS } }

            steps {
                runSuiteXmlFile('core/CoreTests.xml')
            }
        }

    }

    post {
        always {
            node(env.NODE_LABEL) {
                script {
                    junit allowEmptyResults: true, testResults: '**/ueba-automation-test/target/surefire-reports/junitreports/*.xml'
                    archiveArtifacts allowEmptyArchive: true, artifacts: '**/ueba-automation-test/target/log/processing/*.log'

                    if (params.START_STOP_EC2_INSTANCE)
                        build job: 'ueba-nodes-actions', parameters: [
                                string(name: 'NODE_LABEL', value: env.NODE_LABEL),
                                string(name: 'ACTION', value: 'stop')
                        ]
                }
            }
        }
    }
}

def editApplicationProperties() {
    def file = "/etc/netwitness/presidio/configserver/configurations/application.properties"
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}editPropertiesFile.sh $file aws.bucket.name ${env.S3_BUCKET}"
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}editPropertiesFile.sh $file aws.tenant ${env.S3_TENANT}"
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}editPropertiesFile.sh $file aws.account ${env.S3_ACCOUNT}"
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}editPropertiesFile.sh $file aws.region ${env.AWS_REGION}"
}

def getAccountID() {
    if (!"${params.S3_ACCOUNT}".isEmpty()) {
        return params.S3_ACCOUNT
    } else {
        withAWS(credentials: '5280fdc9-429c-4163-8328-fafbbccc75dc', region: env.AWS_REGION) {
            files = s3FindFiles(bucket: "${params.S3_BUCKET}", path: "${params.S3_TENANT}/${params.S3_APPLICATION}", glob: "*")
            println 'Folders found:'
            for (file in files) {
                println file.name
            }

            def timestamps = new ArrayList<Long>();
            for (file in files) {
                timestamps.add(Long.valueOf(file.name))
            }
            println "latest timestamp found: " + timestamps.max()
            return timestamps.max()
        }
    }
}

def runSuiteXmlFile(String suiteXmlFile) {
    sh 'pwd'
    sh "echo JAVA_HOME=${env.JAVA_HOME}"
    withAWS(credentials: '5280fdc9-429c-4163-8328-fafbbccc75dc', region: 'us-east-1') {
        sh "mvn test -B --projects ueba-automation-test --also-make -DsuiteXmlFile=${suiteXmlFile} ${params.MVN_TEST_OPTIONS} -Dgenerator_format=${params.generator_format} -Dpre_processing_configuration_scenario=${pre_processing_configuration_scenario}"
    }
}

def cleanUebaDBs() {
    println "Going to resolve DB Host"
    def dbIpSearch = sh(script: "curl http://localhost:8888/application-null.properties -s | grep mongo.db.host.name", returnStdout: true).trim() as String
    def dbIp = dbIpSearch.split()[1]
    println dbIp
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/cleanup_app.sh $VERSION $env.OLD_UEBA_RPMS"
    sh(script: "sshpass -p \"netwitness\" ssh root@${dbIp} -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null 'bash -s' < ${env.WORKSPACE}${env.SCRIPTS_DIR}deployment/cleanup_db.sh $VERSION $env.OLD_UEBA_RPM", returnStatus: true)
}