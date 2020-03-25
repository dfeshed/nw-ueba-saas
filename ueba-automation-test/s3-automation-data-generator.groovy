
pipeline {
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'master', description: '')
        string(name: 'MVN_TEST_OPTIONS', defaultValue: '-q -o -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
        string(name: 'S3_BUCKET', defaultValue: 'presidio-automation-data', description: '')
        string(name: 'S3_TENANT', defaultValue: '', description: 'Empty value - put current millis')
        string(name: 'S3_ACCOUNT', defaultValue: 'aws-account', description: '')

        string(name: 'generator_format', defaultValue: 'S3_JSON_GZIP', description: '')
        choice(name: 'NODE_LABEL', choices: ['master','UEBA01','UEBA02','UEBA03','UEBA04'], description: '')
    }

    agent { label env.NODE_LABEL }

    environment {
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        S3_TENANT = getMillisOrParams()
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

        stage('Generate data') {
            steps {
                runSuiteXmlFile('aws/S3_DataInjection.xml')
            }
        }
    }
}

def getMillisOrParams() {
    String defaultVal = params.S3_TENANT
    if ( ! defaultVal.isEmpty()) {
        println "defaultVal from the Job"
        return defaultVal
    } else {
        def currentMillis = System.currentTimeMillis()
        println "defaultVal as a timestamp " + currentMillis
        return currentMillis
    }
}

def runSuiteXmlFile(String suiteXmlFile) {
    sh 'pwd'
    sh "echo ${env.JAVA_HOME}"
    withAWS(credentials: '5280fdc9-429c-4163-8328-fafbbccc75dc', region: 'us-east-1') {
        sh "mvn test -B --projects ueba-automation-test --also-make -DsuiteXmlFile=${suiteXmlFile} ${params.MVN_TEST_OPTIONS} -Dgenerator_format=${params.generator_format}"
    }
}