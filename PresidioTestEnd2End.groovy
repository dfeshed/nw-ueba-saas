pipeline {
        agent { label env.NODE }
        environment {
            BASEURL = "baseurl="
        }
        stages {
            stage('Build') {
                steps {
                    script {
                        if (env.SPECIFIC_RPM_BUILD != '') {
                            $BASEURL="$BASEURL"+env.SPECIFIC_RPM_BUILD
                            echo "$BASEURL"
                        } else {
                            echo 'I execute elsewhere'
                        }
                    }
                }
            }
        }
}