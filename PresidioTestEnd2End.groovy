pipeline {
        agent { label env.NODE }
        stages {
            stage('Build') {
                steps {
                    String BASEURL="baseurl=";
                    script {
                        if (env.SPECIFIC_RPM_BUILD != '') {
                            BASEURL=BASEURL+env.SPECIFIC_RPM_BUILD
                        } else {
                            echo 'I execute elsewhere'
                        }
                    }
                }
            }
        }
}