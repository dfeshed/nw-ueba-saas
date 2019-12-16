
node("${params.ADMIN_SERVER_NODE}") {


    stage('Init workspace') {
        println("Init workspace")
        cleanWs()
        sh "pwd"
        sh(script:"wget ${params.SCRIPT_URL} --no-check-certificate -P ${WORKSPACE}", returnStatus:true)
    }

    stage('Initialise and upgrade admin-server.') {
        println("Starting script")
        sh(script:"sh ${WORKSPACE}/upgrade-environment.sh ${params.NW_VERSION} ${params.ASOC_URL}", returnStatus:true)
        println("Finished script")
    }
}


node("${params.NODE}") {
    stage('Waiting for admin-server') {
        println("Waiting 10 min")
        sleep 60
    }
}


node("${params.ADMIN_SERVER_NODE}") {
    stage('Proceeding to other hosts') {
        println("Proceeding to other hosts")
    }
}