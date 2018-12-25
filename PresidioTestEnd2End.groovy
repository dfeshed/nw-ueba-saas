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
                            env.BASEURL = env.BASEURL env.SPECIFIC_RPM_BUILD
                            echo env.BASEURL
                            echo env.SPECIFIC_RPM_BUILD
                        } else {
                            echo 'I execute elsewhere'
                        }
                    }
                }
            }
        }
}