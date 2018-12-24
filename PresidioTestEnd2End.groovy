node (env.NODE) {

    stage ('test'){

        steps {
            sh '''#!/bin/bash
                 echo "$hostname"
                 echo "$whoami"
                '''
        }
    }


}