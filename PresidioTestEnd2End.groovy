pipeline {
        agent { label env.NODE }
        stages {
            stage('Build') {
                steps {
                    sh "#!/bin/bash \n" +
                            "echo \"Hello from \$SHELL\""
                }
            }
        }
}