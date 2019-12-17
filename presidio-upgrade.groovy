
def adminServerUpgradeScript="upgrade-admin-server.sh"
def uebaRepoconfigScript="upgrade-repo-configuration.sh"

node("${params.ADMIN_SERVER_NODE}") {

        stage('Waiting for admin-server') {
            println(" ++++++++ Waiting 10 min for the admin server to be up after reboot ++++++++ ")
            sh "reboot"
            retry(20){
                sleep time: 30
                unit: 'SECONDS'
                sh "ssh -o ConnectTimeout=1 ${params.ADMIN_SERVER_NODE} exit"
            }
        }
    }


node("${params.UEBA_NODE}") {
    if (params.UEBA_REPO_CONF_STAGE_ENABLED) {
        stage('UEBA repo configuration') {
            println(" ++++++++ Going to configure UEBA node repo ++++++++ ")
            cleanWs()
            sh "whoami"
            sh(script: "wget ${params.SCRIPTS_URL}${uebaRepoconfigScript} --no-check-certificate -P ${WORKSPACE}", returnStatus: true)
            sh(script: "sh ${WORKSPACE}/upgrade-repo-configuration.sh ${params.NW_VERSION}", returnStatus: true)
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
        println(" ++++++++ Going to upgrade: ${node} ++++++++ ")
        sh "cd /tmp/ ; upgrade-cli-client -u --host-addr ${node} --version ${params.NW_VERSION} -v"
        println(" ++++++++ Ready: ${node} ++++++++ ")
    }
}