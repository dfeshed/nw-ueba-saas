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
        String rpmBuildPath = env.SPECIFIC_RPM_BUILD,
        String rpmVeriosn = env.VERSION,
        String stability = env.STABILITY
        ){
    String baseUrl = "baseurl="
    if (rpmBuildPath != '') {
        baseUrl = baseUrl + rpmBuildPath
        println (baseUrl)
    } else if (rpmVeriosn == '11.2.1.0'){
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.2/11.2.1/11.2.1.0-" + stability
        println (baseUrl)
    } else if (rpmVeriosn == '11.3.0.0'){
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.3/11.3.0/11.3.0.0-" + stability
        println (baseUrl)
    }
}
