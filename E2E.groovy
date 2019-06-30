
pipeline {
    agent { label env.NODE }
    environment {
        BASEURL = "baseurl="
        // The credentials (name + password) associated with the RSA build user.
        RSA_BUILD_CREDENTIALS = credentials('673a74be-2f99-4e9c-9e0c-a4ebc30f9086')
        OWB_ALLOW_NON_FIPS = "on"
    }

    stages {
        stage('test') {
            steps {
                //uebaInstallationTasks = new UebaInstallationTasks()
                def uebaInstallationTasks = load 'UebaInstallationTasks.groovy'
                uebaInstallationTasks.test()
                //def thing = load 'Thing.groovy'
                //echo thing.doStuff()
            }
        }
    }
}
