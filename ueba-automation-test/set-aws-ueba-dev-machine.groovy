
pipeline {
    parameters {
        string(name: 'NODE_LABEL', defaultValue: '', description: '')

        booleanParam(name: 'S3_DOWNLOAD', defaultValue: true, description: '')
        booleanParam(name: 'INSTALL_MAVEN', defaultValue: true, description: '')
        booleanParam(name: 'INSTALL_GIT', defaultValue: true, description: '')
        booleanParam(name: 'INSTALL_M2', defaultValue: true, description: '')
        booleanParam(name: 'ENABLE_DATADOG', defaultValue: true, description: '')
    }

    environment {
        BUCKET_NAME="presidio-repo.rsa.com"
        BUCKET_PATH="presidio-test-utils"
        HOME_DIR="/home/presidio"
        DOWNLOADS_DIR = "${HOME_DIR}/download"
        BIN_DIR = "${HOME_DIR}/bin"
    }

    agent { label 'master' }

    stages {

        stage('Download Files') {
            agent {label env.NODE_LABEL}

            when { expression { return params.S3_DOWNLOAD } }

            steps {
                sh 'pwd'
                sh 'whoami'
                script { currentBuild.displayName="#${BUILD_NUMBER} ${NODE_NAME}" }
                cleanWs()
                sh "[ -d ${DOWNLOADS_DIR} ] || mkdir -p ${DOWNLOADS_DIR}"
                sh "[ -d ${BIN_DIR} ] || mkdir -p ${BIN_DIR}"
                withAWS(credentials: '5280fdc9-429c-4163-8328-fafbbccc75dc', region: 'us-east-1') {
                    s3Download(file:'', bucket:"${env.BUCKET_NAME}", path:"${BUCKET_PATH}/",force:true)
                }
                sh "mv -f ${WORKSPACE}/${BUCKET_PATH}/* ${DOWNLOADS_DIR}/"
            }
        }

        stage('Install Maven') {
            agent {label env.NODE_LABEL}

            when { expression { return params.INSTALL_MAVEN } }

            steps {
                sh "cd ${DOWNLOADS_DIR} && tar -xf apache-maven-3.6.2-bin.tar.gz"
                sh "[ -f ${BIN_DIR}/mvn ] ||  ln -s ${DOWNLOADS_DIR}/apache-maven-3.6.2/bin/mvn ${BIN_DIR}"
                sh 'echo $PATH'
                sh "mvn -version"
            }
        }

        stage('Install Git') {
            agent {label env.NODE_LABEL}

            when { expression { return params.INSTALL_GIT } }

            steps {
                sh "git --version || sudo yum install -y git"
                sh "git --version"
            }
        }

        stage('Update M2') {
            agent {label env.NODE_LABEL}

            when { expression { return params.INSTALL_M2 } }

            steps {
                sh "cd ${DOWNLOADS_DIR} && tar -xf m2.tar.gz"
                sh "[ -d ${HOME_DIR}/.m2 ] || mkdir -p ${HOME_DIR}/.m2"
                sh "rm -rf ${HOME_DIR}/.m2/*"
                sh "cd ${DOWNLOADS_DIR} && mv repository ${HOME_DIR}/.m2"
            }
        }

    }
}