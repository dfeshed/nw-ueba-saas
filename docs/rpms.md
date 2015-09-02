# RPMs for Security Analytics UI

## Generating RPMs

To generate RPMs for all sub-projects of the SA UI project, use the `rpm` 
Gradle task:

```
$ cd sa-ui
$ ./gradlew rpm
```

This task will build the client Ember application, and all the backend 
services and bundle each into an RPM.  The RPMs are located in the 
`build/distributions` directory under each project.

## Creating an RPM

By default, the RPM task is disabled for each sub-project.  To enable
it, simply set the `createRPM` property to true in the Gradle build file:

```
ext {
    createRPM = true
    
    // other properties
}
```

The RPM attributes can be customized by configuring the `rpm` task.
For example:

```
rpm {
    requires("java", "1.8.0", GREATER | EQUAL)

    postInstall file("${projectDir}/rpm/scripts/postInstall.sh")
    postUninstall file("${projectDir}/rpm/scripts/postUninstall.sh")

    from(libsDir) {
        into "/opt/rsa/sa-ui/service"
    }
}
```

All configuration options are documented on the 
[plugin page](https://github.com/nebula-plugins/gradle-ospackage-plugin/blob/master/Plugin-Rpm.md). 

