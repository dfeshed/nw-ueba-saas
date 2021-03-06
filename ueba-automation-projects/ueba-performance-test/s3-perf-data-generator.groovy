
pipeline {
    parameters {
        string(name: 'START_TIME', defaultValue: '2020-02-01T00:00:00.00Z', description: '')
        string(name: 'END_TIME', defaultValue: '2020-02-07T00:00:00.00Z', description: '')
        extendedChoice(defaultValue: 'TLS,FILE,ACTIVE_DIRECTORY,AUTHENTICATION,REGISTRY,PROCESS', description: '',
                multiSelectDelimiter: ',', name: 'SCHEMAS', quoteValue: false,
                saveJSONParameterToFile: false, type: 'PT_CHECKBOX',
                value: 'TLS,FILE,ACTIVE_DIRECTORY,AUTHENTICATION,REGISTRY,PROCESS', visibleItemCount: 6)

        string(name: 'S3_BUCKET', defaultValue: 'presido-performance-data', description: '')
        string(name: 'S3_TENANT', defaultValue: 'acme', description: '')
        string(name: 'S3_ACCOUNT', defaultValue: '', description: 'Empty value -> current millis')
        string(name: 'S3_APPLICATION', defaultValue: 'NetWitness', description: '')

        string(name: 'USERS_PROBABILITY_MULTIPLIER', defaultValue: '1', description: '')
        string(name: 'USERS_MULTIPLIER', defaultValue: '1', description: '')

        string(name: 'TLS_GROUPS_TO_CREATE', defaultValue: '199', description: '')
        string(name: 'TLS_EVENTS_PER_DAY_PER_GROUP', defaultValue: '93375', description: '')
        string(name: 'TLS_ALERTS_PROBABILITY', defaultValue: '0.001', description: '')

        string(name: 'SUREFIRE_ARG_LINE', defaultValue: '-Xms1g -Xmx60g', description: '')
        string(name: 'SCENARIOS_SPLIT_INTERVAL_HOURS', defaultValue: '0', description: 'put 0 to disable split')
        string(name: 'CHUNK_SIZE', defaultValue: '5000', description: '')
        choice(name: 'PARALLEL_SCENARIOS_INSERT', choices: ['true','false'], description: '')
        string(name: 'GENERATOR_FORMAT', defaultValue: 'S3_JSON_GZIP_CHUNKS', description: 'S3_JSON_GZIP_CHUNKS,CEF_HOURLY_FILE, CEF_DAILY_FILE')
        string(name: 'BRANCH_NAME', defaultValue: 'master', description: '')
        string(name: 'NODE_LABEL', defaultValue: 'perf-s3-node', description: '')
        string(name: 'MVN_OPTIONS', defaultValue: '-q -o -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
    }

    agent { label env.NODE_LABEL }

    environment {
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        S3_ACCOUNT = getAccountID()
    }

    stages {

        stage('Project Clone') {
            steps {
                sh 'pwd'
                sh 'whoami'
                script { currentBuild.displayName="#${BUILD_NUMBER} ${NODE_NAME}" }
                script { currentBuild.description = "${env.START_TIME} - ${env.END_TIME}\n${SCHEMAS}\n${S3_BUCKET}/${S3_TENANT}/${S3_ACCOUNT}/${S3_APPLICATION}" }
                cleanWs()
                git branch: params.BRANCH_NAME, credentialsId: '67bd792d-ad28-4ebc-bd04-bef8526c3389', url: 'git@github.com:netwitness/ueba-automation-projects.git'
            }
        }

        stage('Generate data') {
            steps {
                retry(3) {
                    runSuiteXmlFile('PerfLogsNoSpringGenTest.xml')
                }
            }
        }
    }
}

def getAccountID() {
    String account = params.S3_ACCOUNT
    if ( ! account.isEmpty()) {
        println "account from the Job"
        return account
    } else {
        def currentMillis = System.currentTimeMillis()
        println "account as a timestamp " + currentMillis
        return currentMillis
    }
}

def runSuiteXmlFile(String suiteXmlFile) {
    sh 'pwd'
    sh "echo ${env.JAVA_HOME}"
    withAWS(credentials: '5280fdc9-429c-4163-8328-fafbbccc75dc', region: 'us-east-1') {

        sh "mvn test -B --projects ueba-performance-test --also-make " +
                "-DsuiteXmlFile=${suiteXmlFile} " +
                "'-Dsurefire.argLine=${params.SUREFIRE_ARG_LINE}' " +
                "-Dschemas=${params.SCHEMAS} " +
                "-Dgenerator_format=${params.GENERATOR_FORMAT} " +
                "-Dstart_time=${params.START_TIME} " +
                "-Dend_time=${params.END_TIME} " +

                "-Dusers_probability_multiplier=${params.USERS_PROBABILITY_MULTIPLIER} " +
                "-Dusers_multiplier=${params.USERS_MULTIPLIER} " +

                "-Dtls_alerts_probability=${params.TLS_ALERTS_PROBABILITY} " +
                "-Dtls_groups_to_create=${params.TLS_GROUPS_TO_CREATE} " +
                "-Dtls_events_per_day_per_group=${params.TLS_EVENTS_PER_DAY_PER_GROUP} " +
                "${params.MVN_OPTIONS} "
    }
}