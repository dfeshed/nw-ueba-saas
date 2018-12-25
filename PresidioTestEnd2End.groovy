pipeline {
        agent { label env.NODE }
        environment {
            BASEURL = "baseurl="
        }
        stages {
            stage('Build') {
                steps {
                    script {
                        setBaseUrl ()
                    }
                }
            }
        }
}

/******************************
 *   UEBA RPMs Installation   *
 ******************************/
def setBaseUrl (
        String RpmBuildPath = env.SPECIFIC_RPM_BUILD ){
    String baseUrl = "baseurl="
    if (RpmBuildPath != '') {
        baseUrl = baseUrl + RpmBuildPath
        println (baseUrl)
        println (RpmBuildPath)
    } else {
        echo 'I execute elsewhere'
    }
}
