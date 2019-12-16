def runParallel = true
def buildStages

node("${params.NODE}") {

    stage('Initialise') {
        cleanWs()
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout']], gitTool: 'Default EL7', submoduleCfg: [], userRemoteConfigs: [[credentialsId: '5ee6d182-da05-4a48-8a0c-ac411909a431', url: 'https://github.rsa.lab.emc.com/asoc/presidio-jenkins-job-dsl.git']]])
        // Set up List<Map<String,Closure>> describing the builds
        buildStages = prepareBuildStages()
        println("Initialised pipeline.")
    }

    for (builds in buildStages) {
        if (runParallel) {
            parallel(builds)
        } else {
            // run serially (nb. Map is unordered! )
            for (build in builds.values()) {
                build.call()
            }
        }
    }

    stage('Finish') {
        println('Build complete.')
    }
}

// Create List of build stages to suit
def prepareBuildStages() {
    println("########## Script to execute: ${params.SCRIPT_TO_EXECUTE} ############")
    println("########## Remote servers: ${params.REMOTE_SERVERS} ############")

    def remoteServers = "${params.REMOTE_SERVERS}".split(',').collect{it as String}
    def script = "${params.SCRIPT_TO_EXECUTE}"

    def buildList = []
    def buildStages = [:]

    for (remoteServer in remoteServers ) {
        def server = "${remoteServer}"
        buildStages.put(server, prepareOneBuildStage(server, script))
    }
    buildList.add(buildStages)
    return buildList
}

def prepareOneBuildStage(String remoteServer, String script) {
    return {
        stage("Build stage:${remoteServer}") {
            println("Started on ${remoteServer}")
            sh(script:"sshpass -p \"netwitness\" ssh root@${remoteServer} -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null 'bash -s' < ${WORKSPACE}/scripts/${script}", returnStatus:true)
            println("Finished on ${remoteServer}")
        }
    }
}