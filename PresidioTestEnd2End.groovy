pipeline {
        agent { label env.NODE }
        environment {
            BASEURL
        }
        stages {
            stage('Build') {
                steps {
                    script {
                        if (env.SPECIFIC_RPM_BUILD != '') {
                            $BASEURL="baseurl="+env.SPECIFIC_RPM_BUILD
                            echo "$BASEURL"
                        } else {
                            echo 'I execute elsewhere'
                        }
                    }
                }
            }
        }
}