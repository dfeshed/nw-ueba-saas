def adminServerUpgradeScript = "upgrade-admin-server.sh"
def uebaRepoconfigScript = "upgrade-repo-configuration.sh"
def scriptsUrl = "https://github.rsa.lab.emc.com/raw/asoc/presidio-jenkins-job-dsl/master/scripts/"
def nwVersion = getNwVersion()

    environment {
        SECONDARY_NODE = 'ueba_pipeline_node'
    }

node("${params.ADMIN_SERVER_NODE}") {
        cleanWs()
        if (params.ADMIN_SERVER_UPGRADE_STAGE_ENABLED) {
            stage('Init workspace') {
                println(" ++++++++ Init workspace ++++++++ ")
                println(" ++++++++ Downloading  ${scriptsUrl}${adminServerUpgradeScript} script from the Git ++++++++ ")
                sh(script: "wget -q ${scriptsUrl}${adminServerUpgradeScript} --no-check-certificate -P ${WORKSPACE}", returnStatus: true)
                println(" ++++++++ finished ++++++++ ")
            }
            stage('Initialise and upgrade admin-server.') {
                //validateRepoAsocUrl()
                println(" ++++++++ Starting admin-server upgrade ++++++++ ")
                ADMIN_UPGARDE_STATUS = sh(script: "sh ${WORKSPACE}/upgrade-admin-server.sh ${nwVersion} ${params.REPO_ASOC_URL}", returnStatus: true) == 0
                if (!ADMIN_UPGARDE_STATUS) {
                    error("Admin server upgrade progress failed !!!!!!!")
                }
                println(" ++++++++ Finished admin-server upgrade ++++++++ ")
            }
        }
    }

    node(env.SECONDARY_NODE) {
        if (params.ADMIN_SERVER_UPGRADE_STAGE_ENABLED) {
            stage('Waiting for admin-server') {
                println(" ++++++++ Waiting 10 min for the Admin Server to be start ++++++++ ")
                sleep 600
            }
        }
    }

    node("${params.ADMIN_SERVER_NODE}") {
        if (params.UEBA_UPGRADE_STAGE_ENABLED) {
            stage('Upgrading UEBA Node') {
                String uebaIp = sh(returnStdout: true, script: "getent hosts ${params.UEBA_NODE} | awk \'{ print \$1 }\'").trim()
                println(" ++++++++ Downloading  ${scriptsUrl}${uebaRepoconfigScript} script from the Git ++++++++ ")
                sh(script: "wget -q ${scriptsUrl}${uebaRepoconfigScript} --no-check-certificate -P ${WORKSPACE}", returnStatus: true)
                println(" ++++++++ Updating UEBA yum repos ++++++++ ")
                sh(script: "sshpass -p \"netwitness\" ssh root@${params.UEBA_NODE} -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null 'bash -s' < ${WORKSPACE}/${uebaRepoconfigScript} ${nwVersion} ", returnStatus: true)
                println(" ++++++++ Going to upgrade: UEBA node ${params.UEBA_NODE} ++++++++ ")
                sh(returnStdout: true, script: "upgrade-cli-client -u --host-addr ${uebaIp} --version ${nwVersion} -v").trim()
                println(" ++++++++ UEBA Upgrade Complited ++++++++ ")
                println(" ++++++++ Going to reboot ueba: ${params.UEBA_NODE}  ++++++++ ")
                sh(script: "sshpass -p \"netwitness\" ssh root@${params.UEBA_NODE} -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null '/usr/sbin/init 6'", returnStatus: true)

            }
        }
    }

    node("${params.ADMIN_SERVER_NODE}") {
        if (params.OTHER_NODES_UPGRADE_STAGE_ENABLED) {
            stage('Proceeding to other hosts') {
                println(" ++++++++ Upgrading the rest of the hosts ++++++++ ")
                upgradeOtherNodes()
                println(" ++++++++ DONE ++++++++ ")
            }
        }
    }

def upgradeOtherNodes() {
    def nodes = params.OTHER_IPS.split(",")
    println("Other nodes: ${nodes}")
    for (String node : nodes) {
        println(" ++++++++ Going to upgrade node: ${node} ++++++++ ")
        sh "cd /tmp/ ; upgrade-cli-client -u --host-addr ${node} --version ${nwVersion}  -v"
        println(" ++++++++ Going to reboot: ${node} ++++++++ ")
        sh(script:"sshpass -p \"netwitness\" ssh root@${node} -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null '/usr/sbin/init 6'", returnStatus:true)
    }
}

def getNwVersion () {
    String asocUrl = params.REPO_ASOC_URL
    String nw_version = asocUrl.substring(asocUrl.length() - 12, asocUrl.length() - 4)
    return nw_version
}
def validateRepoAsocUrl() {
    String asocUrl = params.REPO_ASOC_URL
    Urlresponsecode = sh(returnStdout: true, script: "curl -o /dev/null -s -w \"%{http_code}\\n\" ${asocUrl}").trim()
    if (Urlresponsecode != '200') {
        error("Repo zip URL isn't valid, getting error code - ${Urlresponsecode}: $asocUrl} ")
    }
}