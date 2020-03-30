
pipeline {
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'master', description: '')
        string(name: 'MVN_TEST_OPTIONS', defaultValue: '-q -o -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
        string(name: 'S3_BUCKET', defaultValue: 'presidio-automation-data', description: '')
        string(name: 'S3_TENANT', defaultValue: 'acme', description: '')
        string(name: 'S3_ACCOUNT', defaultValue: '', description: 'Empty value -> current millis')
        string(name: 'generator_format', defaultValue: 'S3_JSON_GZIP', description: '')
        string(name: 'historical_days_back', defaultValue: '35', description: '')
        choice(name: 'NODE_LABEL', choices: ['master','UEBA01','UEBA02','UEBA03','UEBA04'], description: '')
        choice(name: 'JAVA_HOME', choices: ['/usr/lib/jvm/java-11-openjdk-11.0.5.10-0.amzn2.x86_64/','/usr/lib/jvm/java-11-openjdk-11.0.5.10-0.el7_7.x86_64'], description: '')
    }

    agent { label env.NODE_LABEL }

    environment {
        JAVA_HOME = "${params.JAVA_HOME}"
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        S3_ACCOUNT = getAccountID()
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
        sh "mvn test -B --projects ueba-automation-test --also-make -DsuiteXmlFile=${suiteXmlFile} ${params.MVN_TEST_OPTIONS} -Dgenerator_format=${params.generator_format}"
    }
}