node (env.NODE) {

    stage (){

        steps {
            bash '''#!/bin/bash
                 echo "$hostname"
                 echo "$whoami"
                '''
        }
    }


}