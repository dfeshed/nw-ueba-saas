
pipeline {
    agent { label env.NODE }
    environment {
        BASEURL = "baseurl="
        // The credentials (name + password) associated with the RSA build user.
            RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
    OWB_ALLOW_NON_FIPS = "on"
}

    stages {
        stage('test') {
            steps {
                println("NODE: "+env.NODE)
                setBaseUrl()
            }
        }
    }

}
def setBaseUrl(
        String rpmBuildPath = env.SPECIFIC_RPM_BUILD,
        String rpmVeriosn = env.VERSION,
        String stability = env.STABILITY
) {

    ThirdDir=env.VERSION
    String[] versionArray = ThirdDir.split("\\.")
    FirstDir=versionArray[0] + "." + versionArray[1]
    SecondDir= FirstDir + "." + versionArray[2]
    baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/" + FirstDir + "/" + SecondDir +  "/" + ThirdDir  + "-" + stability + "/"
    println(baseUrl)
    baseUrlValidation = baseUrl.drop(8)
    baseUrlresponsecode = sh(returnStdout: true, script: "curl -o /dev/null -s -w \"%{http_code}\\n\" ${baseUrlValidation}").trim()
    if (baseUrlresponsecode == '200') {
        prinln("connection succeeded")
    }

}
