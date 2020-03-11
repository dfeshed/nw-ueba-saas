pipeline {
    parameters {
        booleanParam(name: 'NETWITNESS_DB_RESET', defaultValue: false, description: '')
        booleanParam(name: 'GENERATED_FILES_CLEANUP', defaultValue: false, description: '')
        booleanParam(name: 'UPLOADED_FILES_CLEANUP', defaultValue: false, description: '')
        booleanParam(name: 'RUN_GENERATOR', defaultValue: true, description: '')
        booleanParam(name: 'SPLIT_FILES', defaultValue: false, description: '')
        booleanParam(name: 'UPLOAD_TO_BROKER', defaultValue: false, description: '')

        string(name: 'FILES_DESTINATION_PATH', defaultValue: '/var/netwitness/s3_mount/perf_broker_cef', description: '')

        string(name: 'LOG_DECODER_IP', defaultValue: '172.24.229.74', description: '')
        string(name: 'BROKER_IP', defaultValue: '172.24.229.82', description: '')
        string(name: 'START_TIME', defaultValue: '2020-02-01T00:00:00.00Z', description: '')
        string(name: 'END_TIME', defaultValue: '2020-02-07T00:00:00.00Z', description: '')
        extendedChoice(defaultValue: 'TLS,FILE,ACTIVE_DIRECTORY,AUTHENTICATION,REGISTRY,PROCESS', description: '',
                multiSelectDelimiter: ',', name: 'SCHEMAS', quoteValue: false,
                saveJSONParameterToFile: false, type: 'PT_CHECKBOX',
                value: 'TLS,FILE,ACTIVE_DIRECTORY,AUTHENTICATION,REGISTRY,PROCESS', visibleItemCount: 6)

        string(name: 'USERS_PROBABILITY_MULTIPLIER', defaultValue: '1', description: '')
        string(name: 'USERS_MULTIPLIER', defaultValue: '1', description: '')

        string(name: 'TLS_GROUPS_TO_CREATE', defaultValue: '199', description: '')
        string(name: 'TLS_EVENTS_PER_DAY_PER_GROUP', defaultValue: '93375', description: '')
        string(name: 'TLS_ALERTS_PROBABILITY', defaultValue: '0.001', description: '')

        string(name: 'FILE_SPLIT_SIZE', defaultValue: '447000000', description: 'SPLIT_FILES stage parameter. Size bytes per output file')
        string(name: 'DELAY_BETWEEN_FILES_INSERT_SEC', defaultValue: '60', description: 'Delay between files upload to the LogDecoder')

        string(name: 'SUREFIRE_ARG_LINE', defaultValue: '-Xms1g -Xmx5g', description: '')
        string(name: 'CHUNK_SIZE', defaultValue: '10000', description: '')
        choice(name: 'PARALLEL_SCENARIOS_INSERT', choices: ['true', 'false'], description: '')

        choice(name: 'GENERATOR_FORMAT', choices: ['CEF_HOURLY_FILE', 'CEF_DAILY_FILE'], description: '')
        string(name: 'BRANCH_NAME', defaultValue: 'master', description: '')
        string(name: 'NODE_LABEL', defaultValue: 'log-hybrid-74', description: '')
        string(name: 'MVN_OPTIONS', defaultValue: '-q -o -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
    }

    agent { label env.NODE_LABEL }

    environment {
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        PERF_SCRIPTS_DIR = '/ueba-performance-test/src/test/resources/scripts/'
        FRAMEWORK_SCRIPTS_DIR = '/ueba-automation-framework/src/main/resources/scripts/'

        PERF_GEN_TARGET_PATH = "${env.WORKSPACE}/ueba-performance-test/target/netwitness_events_gen/*"
        PERF_GEN_GENERATED_PATH = "${FILES_DESTINATION_PATH}/generated"
        PERF_GEN_UPLOADED_PATH = "${FILES_DESTINATION_PATH}/uploaded"
    }

    stages {

        stage('Project Clone') {
            steps {
                sh 'pwd'
                sh 'whoami'
                script { currentBuild.displayName = "#${BUILD_NUMBER} ${NODE_NAME}" }
                script { currentBuild.description = "${env.START_TIME} - ${env.END_TIME}" }
                cleanWs()
                git branch: params.BRANCH_NAME, credentialsId: '67bd792d-ad28-4ebc-bd04-bef8526c3389', url: 'git@github.com:netwitness/ueba-automation-projects.git'
            }
        }

        stage('Reset Broker and LogDecoder DBs') {
            when {
                expression { return params.NETWITNESS_DB_RESET }
            }
            steps {
                sh "${env.WORKSPACE}${env.FRAMEWORK_SCRIPTS_DIR}deployment/reset_ld_and_concentrator_hybrid_dbs.sh ${params.LOG_DECODER_IP} ${params.BROKER_IP}"
            }
        }

        stage('Clean previously generated files') {
            when {
                expression { return params.GENERATED_FILES_CLEANUP }
            }
            steps {
                println "Cleaning:  ${PERF_GEN_GENERATED_PATH}"
                sh "[ -d ${PERF_GEN_GENERATED_PATH} ] && rm -rf ${PERF_GEN_GENERATED_PATH}"
            }
        }

        stage('Clean previously uploaded files') {
            when {
                expression { return params.UPLOADED_FILES_CLEANUP }
            }
            steps {
                println "Cleaning:  ${PERF_GEN_UPLOADED_PATH}"
                sh "[ -d ${PERF_GEN_UPLOADED_PATH} ] && rm -rf ${PERF_GEN_UPLOADED_PATH}"
            }
        }

        stage('Generate data') {
            when {
                expression { return params.RUN_GENERATOR }
            }
            steps {
                runSuiteXmlFile('PerfLogsNoSpringGenTest.xml')

                sh "echo \"moving files to ${PERF_GEN_GENERATED_PATH}\""
                sh "[ -d ${PERF_GEN_GENERATED_PATH} ] || mkdir -p ${PERF_GEN_GENERATED_PATH}"
                sh "[ -d ${PERF_GEN_UPLOADED_PATH} ] || mkdir -p ${PERF_GEN_UPLOADED_PATH}"
                sh "mv ${PERF_GEN_TARGET_PATH} ${PERF_GEN_GENERATED_PATH}"
            }
        }

        stage('Split files') {
            when {
                expression { return params.SPLIT_FILES }
            }
            steps {
                sh "sh ${env.WORKSPACE}${env.PERF_SCRIPTS_DIR}split_files.sh $FILE_SPLIT_SIZE $FILES_DESTINATION_PATH"
            }
        }

        stage('Insert to Broker') {
            when {
                expression { return params.UPLOAD_TO_BROKER }
            }
            steps {
                sh "sh ${env.WORKSPACE}${env.PERF_SCRIPTS_DIR}insert_data.sh $DELAY_BETWEEN_FILES_INSERT_SEC $FILES_DESTINATION_PATH"
            }
        }
    }
}

def runSuiteXmlFile(String suiteXmlFile) {
    sh 'pwd'
    sh "echo ${env.JAVA_HOME}"
    sh "echo whoami"

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