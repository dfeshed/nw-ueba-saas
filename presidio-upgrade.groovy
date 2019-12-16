
node("${params.ADMIN_SERVER_NODE}") {

    stage('Init workspace') {
        println("Init workspace")
        cleanWs()
        sh "pwd"
        sh "whoami"
        sh(script:"wget ${params.SCRIPT_URL} --no-check-certificate -P ${WORKSPACE}", returnStatus:true)
    }

    stage('Initialise and upgrade admin-server.') {
        println("Starting script")
        sh(script:"sh ${WORKSPACE}/upgrade-admin-server.sh ${params.NW_VERSION} ${params.ASOC_URL}", returnStatus:true)
        println("Finished script")
    }
}


node("${params.NODE}") {
    stage('Waiting for admin-server') {
        println("Waiting 10 min")
        sleep 600
    }
}


node("${params.ADMIN_SERVER_NODE}") {
    stage('Proceeding to other hosts') {
        println("Proceeding to other hosts")
        upgradeOtherNodes()
        println("DONE")
    }
}


def upgradeOtherNodes() {
    def nodes = params.OTHER_IPS.split(",")
    println("Other nodes: ${nodes}")
    for(String node : nodes) {
        println("  #############  Going to upgrade: ${node}  #############  ")
        sh"cd /tmp/ ; upgrade-cli-client -u --host-addr ${node} --version ${params.NW_VERSION} -v"
        println("  #############  Done: ${node}  #############  ")
    }
}