pipeline {
    agent {
        label 'el7 && java8'
    }
    stages {
        stage('Building Project: presidio-core-packages') {
            when {
                expression {
                    return env.RUN_CORE_PACKAGES == 'true'
                }
            }
            steps {
                build job: 'presidio-core-packages', parameters: [
                    [$class: 'StringParameterValue', name: 'STABILITY', value: env.STABILITY],
                    [$class: 'StringParameterValue', name: 'GITHUB_ORG', value: env.GITHUB_ORG],
                    [$class: 'StringParameterValue', name: 'VERSION', value: env.VERSION],
                    [$class: 'StringParameterValue', name: 'BRANCH', value: env.BRANCH],
                    [$class: 'BooleanParameterValue', name: 'PUBLISH', value: env.PUBLISH]
                ]
            }
        }
        stage('Building Project: presidio-flume-packages') {
            when {
                expression {
                    return env.RUN_FLUME_PACKAGES == 'true'
                }
            }
            steps {
                build job: 'presidio-flume-packages', parameters: [
                    [$class: 'StringParameterValue', name: 'STABILITY', value: env.STABILITY],
                    [$class: 'StringParameterValue', name: 'GITHUB_ORG', value: env.GITHUB_ORG],
                    [$class: 'StringParameterValue', name: 'VERSION', value: env.VERSION],
                    [$class: 'StringParameterValue', name: 'BRANCH', value: env.BRANCH],
                    [$class: 'BooleanParameterValue', name: 'PUBLISH', value: env.PUBLISH]
                ]
            }
        }
        stage('Building Project: presidio-nw-extension-packages') {
            when {
                expression {
                    return env.RUN_NW_EXTENSION_PACKAGES == 'true'
                }
            }
            steps {
                build job: 'presidio-nw-extension-packages', parameters: [
                    [$class: 'StringParameterValue', name: 'STABILITY', value: env.STABILITY],
                    [$class: 'StringParameterValue', name: 'GITHUB_ORG', value: env.GITHUB_ORG],
                    [$class: 'StringParameterValue', name: 'VERSION', value: env.VERSION],
                    [$class: 'StringParameterValue', name: 'BRANCH', value: env.BRANCH],
                    [$class: 'BooleanParameterValue', name: 'PUBLISH', value: env.PUBLISH]
                ]
            }
        }
        stage('Building Project: presidio-ui-packages') {
            when {
                expression {
                    return env.RUN_PRESIDIO_UI_PACKAGES == 'true'
                }
            }
            steps {
                build job: 'presidio-ui-packages', parameters: [
                    [$class: 'StringParameterValue', name: 'STABILITY', value: env.STABILITY],
                    [$class: 'StringParameterValue', name: 'GITHUB_ORG', value: env.GITHUB_ORG],
                    [$class: 'StringParameterValue', name: 'VERSION', value: env.VERSION],
                    [$class: 'StringParameterValue', name: 'BRANCH', value: env.BRANCH],
                    [$class: 'BooleanParameterValue', name: 'PUBLISH', value: env.PUBLISH]
                ]
            }
        }
    }
}
