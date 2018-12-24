node (env.NODE) {

    stage ('test'){

        steps {
            bash '''#!/bin/bash
                 echo "$hostname"
                 echo "$whoami"
                '''
        }
    }


}