def runParallel = true
def buildStages

def adminServers = "${params.ADMIN_SERVERS}"

node('nw-hz-08-ueba') {
    parameters {
        booleanParam(name: 'INSTALL_UEBA_UI_RPMS', defaultValue: true, description: '')
    }

    stage('Initialise') {
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
    echo adminServers

    def buildList = []
    def buildStages = [:]

    for (name in adminServers ) {
        def n = "${name}"
        buildStages.put(n, prepareOneBuildStage(n))
    }
    buildList.add(buildStages)
    return buildList
}

def prepareOneBuildStage(String name) {
    return {
        stage("Build stage:${name}") {
            println("UI RPMs upgrade on ${name}")
            sh(script:"sleep 5 ; sshpass -p \"netwitness\" ssh root@${name} -o StrictHostKeyChecking=no UserKnownHostsFile=/dev/null 'bash -s' < ${WORKSPACE}/scripts/presidio-ui-update.sh", returnStatus:true)
        }
    }
}