node (env.NODE) {

        stage {
            steps {
                sh "#!/bin/bash \n" +
                        "echo \"Hello from \$SHELL\""
            }
        }

}