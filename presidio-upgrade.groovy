
def adminServerUpgradeScript="upgrade-admin-server.sh"
def uebaRepoconfigScript="upgrade-repo-configuration.sh"
environment {
    SECONDARY_NODE = 'ueba_pipeline_node'
    SCRIPTS_URL = "https://github.rsa.lab.emc.com/raw/asoc/presidio-jenkins-job-dsl/master/scripts/"
}

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
            ADMIN_UPGARDE_STATUS = sh (script: "sh ${WORKSPACE}/upgrade-admin-server.sh ${params.NW_VERSION} ${params.REPO_ASOC_URL}", returnStatus: true) == 0
            if (!ADMIN_UPGARDE_STATUS){
                println("Admin server upgrade progress failed !!!!!!!")
                System.exit(1)
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
            println(" ++++++++ Going to configure UEBA node repo ++++++++ ")
            sh "whoami"
            sh(script: "wget $env.SCRIPTS_URL${uebaRepoconfigScript} --no-check-certificate -P ${WORKSPACE}", returnStatus: true)
            sh(script:"sshpass -p \"netwitness\" ssh root@${params.UEBA_NODE} -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null 'bash -s' < ${WORKSPACE}/${uebaRepoconfigScript}", returnStatus:true)
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