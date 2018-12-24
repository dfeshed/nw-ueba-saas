node (env.NODE) {

        stage('UEBA RPM Upgarde') {
            steps {
                sh "#!/bin/bash \n" +
                        "echo \"Hello from \$SHELL\""
            }
        }

}