
def adminServerUpgradeScript="upgrade-admin-server.sh"
def uebaRepoconfigScript="upgrade-repo-configuration.sh"

node("${params.ADMIN_SERVER_NODE}") {

    if (params.ADMIN_SERVER_UPGRADE_STAGE_ENABLED) {

        stage('Init workspace') {
            println(" ++++++++ Init workspace ++++++++ ")
            cleanWs()
            sh "pwd"
            sh "whoami"
            println(" ++++++++ Downloading upgrade scripts from the Git ++++++++ ")
            sh(script: "wget ${params.SCRIPTS_URL}${adminServerUpgradeScript} --no-check-certificate -P ${WORKSPACE}", returnStatus: true)
            println(" ++++++++ finished ++++++++ ")
        }

        stage('Initialise and upgrade admin-server.') {
            println(" ++++++++ Starting admin-server upgrade ++++++++ ")
            sh(script: "sh ${WORKSPACE}/upgrade-admin-server.sh ${params.NW_VERSION} ${params.ASOC_URL}", returnStatus: true)
            println(" ++++++++ Finished admin-server upgrade ++++++++ ")
        }
    }
}


node("${params.UEBA_NODE}") {
    if (params.WAITING_REBOOT_STAGE_ENABLED) {
        stage('Waiting for admin-server') {
            println(" ++++++++ Waiting 10 min ++++++++ ")
            sleep 600
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
            sh(script: "sh ${WORKSPACE}/upgrade-repo-configuration.sh ${params.NW_VERSION} ${params.ASOC_URL}", returnStatus: true)
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