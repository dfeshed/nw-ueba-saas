
pipeline {
    parameters {
        string(name: 'NODE_LABEL', defaultValue: '', description: '')
        string(name: 'MVN_OPTIONS', defaultValue: '-q -o -Dmaven.test.failure.ignore=false -Duser.timezone=UTC', description: '')
    }

    environment {
        BUCKET_NAME="presidio-repo.rsa.com"
        BUCKET_PATH="presidio-test-utils"
        HOME_DIR="/home/presidio"
        DOWNLOADS_DIR = "${HOME_DIR}/download"
        BIN_DIR = "${HOME_DIR}/bin"
    }


    agent {
        label env.NODE_LABEL
    }

    stages {

        stage('Download Files') {
            steps {
                sh 'pwd'
                sh 'whoami'
                script { currentBuild.displayName="#${BUILD_NUMBER} ${NODE_NAME}" }
                script { currentBuild.description = "${params.BRANCH_NAME}" }
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
            steps {
                sh "cd ${DOWNLOADS_DIR} && tar -xf apache-maven-3.6.2-bin.tar.gz"
                sh "[ -f ${BIN_DIR}/mvn ] ||  ln -s ${DOWNLOADS_DIR}/apache-maven-3.6.2/bin/mvn ${BIN_DIR}"
                // $PATH works after reboot
                //  echo "export PATH=$PATH:$HOME/.local/bin:$HOME/bin" >> ${HOME_DIR}/.bashrc
                sh 'echo $PATH'
                sh "mvn -version"
            }
        }

        stage('Install Git') {
            steps {
                // rpm location: http://172.24.229.44:8882/repo/external/
                sh "git --version || sudo yum install -y git"
                sh "git --version"
            }
        }

        stage('Update M2') {
            steps {
                sh "cd ${DOWNLOADS_DIR} && tar -xf m2.tar.gz"
                sh "[ -d ${HOME_DIR}/.m2 ] && rm -rf ${HOME_DIR}/.m2/*"
                sh "[ -d ${HOME_DIR}/.m2 ] || mkdir -p ${HOME_DIR}/.m2"
                sh "cd ${DOWNLOADS_DIR} && mv repository ${HOME_DIR}/.m2"
            }
        }
    }
}