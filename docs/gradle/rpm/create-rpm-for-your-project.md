# Creating RPM for your project

## Setup your project.gradle file to create RPM

* By default the `rpm` task is turned off.

__sa-ui/build.gradle__
```java
afterEvaluate { Project project ->
    if (project.hasProperty("createRPM") && project.createRPM) {

        task rpm(type:Rpm, dependsOn: ["build", "rpmScripts"]) {

            packageName project.archivesBaseName
            packageDescription "RSA Security Analytics - RPM"
            version project.version.split("-")[0]
            release "el6"
            arch "noarch"
            os "LINUX"
            prefix "/"
            summary "RSA Security Analytics"
            vendor "RSA, The Security Division of EMC"
            license "Copyright (c) RSA, The Security Division of EMC"
            user "root"
        }
    }
}
```

* The `rpm` script files will be copy from `rpm/scripts` directory to `build/rpm/scripts` to expand the project properties. This copy task will be done before the `rpm` task.

__sa-ui/build.gradle__
```java
ext {
    rpmScriptsDir = "build/rpm/scripts"
}

task rpmScripts(type: Copy) {
    from(fileTree("rpm/scripts"))
    into project.rpmScriptsDir
    expand project.properties
}
```

* Set `createRPM` property to turn on `rpm` task for your project

__sa-ui/project/project.gradle__
```java
ext.createRPM = true
```
or
```java
ext {
    createRPM = true
}
```

* Add your project specific settings to the `rpm` task

__sa-ui/project/project.gradle__
```java
afterEvaluate {
    rpm {
        packageName project.rpmName
        packageDescription "RPM brief description."

        requires("nginx")

        preInstall file("${project.rpmScriptsDir}/preInstall.sh")
        postInstall file("${project.rpmScriptsDir}/postInstall.sh")
        preUninstall file("${project.rpmScriptsDir}/preUninstall.sh")
        postUninstall file("${project.rpmScriptsDir}/postUninstall.sh")

        from(fileTree("dist/production")) {
            into "/opt/rsa/${rootProject.name}/html"
        }
    }
}
```

## Run the gradle `rpm` task

```bash
$ cd sa-ui/project
$ ../gradlew rpm
```

## Built RPM location

```
sa-ui/project/build/distributions/project-10.6.0.0-el6.noarch.rpm
```
