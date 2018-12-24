node (env.NODE) {

    stage ('GetNodeName'){

        steps {
            def node_name = "${NODE_NAME}"
            echo "The Node Name is: ${node_name}"
        }
    }


}