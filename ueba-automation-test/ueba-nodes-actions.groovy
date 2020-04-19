pipeline {
    parameters {
        choice(name: 'ACTION', choices: ['start','stop'], description: '')
    }

    agent none

    stages {
        stage('Init') {
            agent { label 'master' }
            steps {
                sh 'pwd'
                sh 'whoami'
                cleanWs()
            }
        }

        stage('Run EC2 start') {
            agent { label 'master' }
            when {  expression { return params.ACTION == 'start' } }

            steps {
                runAction("start")
                sleep(time:2, unit:"MINUTES")
            }
        }

        stage('Test Config Server ready') {
            agent { label "${NODE_LABEL}" }
            when {  expression { return params.ACTION == 'start' } }

            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    retry(100) {
                        sh "curl http://localhost:8888/application-null.properties || sleep 20"
                    }
                }
            }
        }

        stage('Test Mongodb ready') {
            agent { label "${NODE_LABEL}" }
            when {  expression { return params.ACTION == 'start' } }

            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    retry(100) {
                        script{
                            println "Going to resolve DB Host"
                            def dbIpSearch = sh(script: "curl http://localhost:8888/application-null.properties -s | grep mongo.host.name", returnStdout: true).trim() as String
                            def dbIp = dbIpSearch.split()[1]
                            println dbIp
                            sh "mongo -host "+ dbIp + " --eval \"print(\\\"Ready\\\")\" || sleep 20"
                        }
                    }
                }
            }
        }

        stage('Run EC2 stop') {
            agent { label 'master' }
            when {  expression { return params.ACTION == 'stop' } }

            steps {
                runAction("stop")
            }
        }

    }
}

def runAction(String action) {
    node_label_ids_map = initMap()
    String label = "${NODE_LABEL}"
    ids_for_action = node_label_ids_map.get(label)

    println '***** Going to start nodes ******'
    sh "aws ec2 " + action + "-instances --instance-ids " + ids_for_action
    println '***** Running start FINISHED *****'
}

def initMap() {
    println 'Going to get params from ' + NODE_LABELS_IDS
    def lines = params.NODE_LABELS_IDS.split("\n")

    myMap = [:]
    for(line in lines) {
        keyVal = line.split(":")
        myMap.put(keyVal[0], keyVal[1])
    }

    for (entry in myMap) {
        println "Label: $entry.key IDs: $entry.value"
    }

    return myMap
}
