pipeline {
        agent { label env.NODE }
        stages {
            stage('Build') {
                steps {
                    def BASEURL="baseurl="
                    script {
                        if (env.SPECIFIC_RPM_BUILD != '') {
                            BASEURL=BASEURL+env.SPECIFIC_RPM_BUILD
                            echo "$BASEURL"
                        } else {
                            echo 'I execute elsewhere'
                        }
                    }
                }
            }
        }
}