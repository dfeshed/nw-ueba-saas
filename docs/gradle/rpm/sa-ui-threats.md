# sa-ui Threats service RPM

## Project extra properties

* `createRPM = true` - Turn on `rpm` task.
* `rpmName` - The NAME part of the RPM NVRA (Name-Version-Release.Arch).
* `serviceLocation` - This where the Threats service jar will be deployed to: `/opt/rsa/sa-ui/service`.
* `serviceFile` - The Threats service jar name `sa-ui-threats-10.6.0.0-SNAPSHOT.jar`.
* `serviceName` - The name of the Threats service `sa-ui-threats` on the target machine directory: `/etc/init.d`.

__sa-ui/server/threats/threats.gradle__
```java
ext {
    createRPM = true
    rpmName = project.archivesBaseName
    serviceLocation = "/opt/rsa/${rootProject.name}/service"
    serviceFile = "${project.archivesBaseName}-${project.version}.jar"
    serviceName = project.archivesBaseName
}
```

## Project `rpm` task

* `requires("java", "1.8.0", GREATER | EQUAL)` - Requires java version `1.8.0` or later to be installed.
* `postInstall` - After installed this RPM, create a symbolic link for the service `sa-ui-threats` in the directory `/etc/init.d` to point to the service jar `/opt/rsa/sa-ui/service/sa-ui-threats-10.6.0.0-SNAPSHOT.jar`.
* `postUninstall` - After un-installed this RPM, remove the symbolic link `sa-ui-threats` from the directory: `/etc/init.d`.
* Deploy the service jar `sa-ui-threats-10.6.0.0-SNAPSHOT.jar` to the `project.serviceLocation` directory on the target machine.

__sa-ui/server/threats/threats.gradle__
```java
afterEvaluate {
    rpm {
        packageName project.rpmName
        packageDescription "This RPM contains the Threats service for RSA Security Analytics."

        requires("java", "1.8.0", GREATER | EQUAL)

        postInstall file("${project.rpmScriptsDir}/postInstall.sh")
        postUninstall file("${project.rpmScriptsDir}/postUninstall.sh")

        from(new File(project.libsDir.path, project.serviceFile)) {
            into project.serviceLocation
        }
    }
}
```

## RPM postInstall script

* After installed this RPM, create a symbolic link for the service `sa-ui-threats` in the directory `/etc/init.d` to point to the service jar `/opt/rsa/sa-ui/service/sa-ui-threats-10.6.0.0-SNAPSHOT.jar`.

__sa-ui/server/threats/rpm/scripts/postInstall.sh__
```bash
#!/bin/bash
ln -s "${project.serviceLocation}/${project.serviceFile}" "/etc/init.d/${project.serviceName}"
```

## RPM postUninstall script

* After un-installed this RPM, remove the symbolic link `sa-ui-threats` from the directory: `/etc/init.d`.

__sa-ui/server/threats/rpm/scripts/postUninstall.sh__
```bash
#!/bin/bash
rm -f "/etc/init.d/${project.serviceName}"
```

## Run the gradle `rpm` task

```bash
$ cd sa-ui/server/threats
$ ../../gradlew rpm
```

## Built RPM location

```
sa-ui/server/threats/build/distributions/sa-ui-threats-10.6.0.0-el6.noarch.rpm
```
