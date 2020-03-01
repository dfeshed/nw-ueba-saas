
pipeline {
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'master', description: '')
        string(name: 'MVN_TEST_OPTIONS', defaultValue: '-q -o -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
        choice(name: 'IS_MONGO_PASSWORD_ENCRYPTED', choices: ['false','true'], description: '')

        string(name: 'S3_BUCKET', defaultValue: 'presidio-automation-data', description: '')
        string(name: 'S3_TENANT', defaultValue: 'acme', description: '')
        string(name: 'S3_APPLICATION', defaultValue: 'NetWitness', description: '')
        string(name: 'S3_ACCOUNT', defaultValue: '', description: 'Empty -> take last timestamp')

        choice(name: 'generator_format', choices: ['S3_JSON_GZIP','MONGO_ADAPTER'], description: '')
        choice(name: 'pre_processing_configuration_scenario', choices: ['E2E_S3','E2E_MONGO'], description: '')

        booleanParam(name: 'RESET_UEBA_DBS', defaultValue: true, description: '')
        booleanParam(name: 'INSTALL_UEBA_RPMS', defaultValue: false, description: '')
        booleanParam(name: 'DATA_INJECTION', defaultValue: false, description: '')
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
        S3_ACCOUNT = getAccountID()
        AWS_REGION = 'us-east-1'
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
                editApplicationProperties()
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
                runSuiteXmlFile('e2e/E2E_DataInjection.xml')
            }
        }

        stage('Data Processing') {
            when {
                expression { return params.DATA_PROCESSING }
            }
            steps {
                runSuiteXmlFile('e2e/E2E_MongoDataProcessing.xml')
            }
        }

        stage('Tests') {
            when {
                expression { return params.RUN_TESTS }
            }
            steps {
                runSuiteXmlFile('e2e/E2E_Tests.xml')
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

def editApplicationProperties() {
    def file = "/etc/netwitness/presidio/configserver/configurations/application.properties"
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}editPropertiesFile.sh $file aws.bucket.name ${env.S3_BUCKET}"
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}editPropertiesFile.sh $file aws.tenant ${env.S3_TENANT}"
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}editPropertiesFile.sh $file aws.account ${env.S3_ACCOUNT}"
    sh "bash ${env.WORKSPACE}${env.SCRIPTS_DIR}editPropertiesFile.sh $file aws.region ${env.AWS_REGION}"
}

def getAccountID() {
    withAWS(credentials: '5280fdc9-429c-4163-8328-fafbbccc75dc', region: env.AWS_REGION) {
        String pathToSearch = "s3://${params.S3_BUCKET}/${params.S3_TENANT}/${params.S3_APPLICATION}/"
        def accountsStr = sh(returnStdout: true, script: "aws s3 ls ${pathToSearch}").trim().replaceAll("PRE", "").replaceAll("\\s", "")
        println "cli string: " + accountsStr
        def accounts = accountsStr.split("/")
        println "collected array size: " + accounts.size()

        def timestamps = new ArrayList<Long>();
        for (String i : accounts) {
            timestamps.add(Long.valueOf(i))
        }

        println "latest timestamp found: " + timestamps.max()
        return timestamps.max()
    }
}

def runSuiteXmlFile(String suiteXmlFile) {
    sh 'pwd'
    sh "echo JAVA_HOME=${env.JAVA_HOME}"
    withAWS(credentials: '5280fdc9-429c-4163-8328-fafbbccc75dc', region: env.AWS_REGION) {
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