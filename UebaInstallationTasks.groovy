
class UebaInstallationTasks {

def setBaseUrl(
        String rpmBuildPath = env.SPECIFIC_RPM_BUILD,
        String rpmVeriosn = env.VERSION,
        String stability = env.STABILITY
) {
    String baseUrl = "baseurl="
    if (rpmBuildPath != '') {
        baseUrl = baseUrl + rpmBuildPath
        println(baseUrl)
    } else if (rpmVeriosn == '11.2.1.0') {
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.2/11.2.1/11.2.1.0-" + stability + "/"
        println(baseUrl)
    } else if (rpmVeriosn == '11.3.0.0') {
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.3/11.3.0/11.3.0.0-" + stability + "/"
        println(baseUrl)
    } else if (rpmVeriosn == '11.3.0.1') {
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.3/11.3.0/11.3.0.1-" + stability + "/"
        println(baseUrl)
    } else if (rpmVeriosn == '11.3.1.0') {
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.3/11.3.1/11.3.1.0-" + stability + "/"
        println(baseUrl)
    } else if (rpmVeriosn == '11.4.0.0') {
        baseUrl = baseUrl + "http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.4/11.4.0/11.4.0.0-" + stability + "/"
    }
    baseUrlValidation = baseUrl.drop(8)
    baseUrlresponsecode = sh(returnStdout: true, script: "curl -o /dev/null -s -w \"%{http_code}\\n\" ${baseUrlValidation}").trim()
    if (baseUrlresponsecode == '200') {
        sh "sudo sed -i \"s|.*baseurl=.*|${baseUrl}|g\" /etc/yum.repos.d/tier2-rsa-nw-upgrade.repo"
        sh "sudo sed -i \"s|enabled=.*|enabled=0|g\" /etc/yum.repos.d/*.repo"
        sh "sudo sed -i \"s|enabled=.*|enabled=1|g\" /etc/yum.repos.d/tier2-rsa-nw-upgrade.repo"
        sh "sudo yum clean all"
        sh "sudo rm -rf /var/cache/yum"
    } else {
        error("RPM Location is Wrong- ${baseUrlValidation}")
    }
}

    def test (){

        //sh "echo \$(hostname)"
        print('HI')
    }

}
return new UebaInstallationTasks();