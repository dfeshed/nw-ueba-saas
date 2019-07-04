pipeline {
    parameters {
        string(name: 'SPECIFIC_RPM_BUILD', defaultValue: '', description: 'specify the link to the RPMs e.q: http://asoc-platform.rsa.lab.emc.com/buildStorage/ci/master/promoted/11978/11.4.0.0/RSA/')
        string(name: 'INTEGRATION_TEST_BRANCH_NAME', defaultValue: 'origin/master', description: '')
        booleanParam(name: 'RUN_ONLY_TESTS', defaultValue: true, description: '')
        booleanParam(name: 'INSTALL_UEBA_RPMS', defaultValue: true, description: '')
        //choice(name: 'STABILITY', choices: ['dev','beta','alpha','rc','gold'], description: 'RPMs stability type')
        //choice(name: 'VERSION', choices: ['11.4.0.0','11.3.0.0','11.3.1.0','11.2.1.0'], description: 'RPMs version')
        //choice(name: 'NODE_LABLE', choices: ['','','nw-hz-03-ueba','nw-hz-04-ueba','nw-hz-05-ueba','nw-hz-06-ueba','nw-hz-07-ueba'], description: '')
    }
    agent { label env.NODE_LABLE }
    environment {
        FLUME_HOME = '/var/lib/netwitness/presidio/flume/'
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        REPOSITORY_NAME = "presidio-integration-test"
    }

    stages {
        stage('presidio-integration-test Project Clone') {
            steps {
                echo "dsfjkhgdslfkfjdsgj"
            }
        }
    }
}
