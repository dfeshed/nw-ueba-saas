# sa-ui client RPM

## Project extra properties

* `nginxConfLocation` - Nginx configuration location where the `sa-ui-client.conf` file will deployed to: `/etc/nginx/conf.d`.
* `createRPM = true` - Turn on `rpm` task.
* `rpmName` - The NAME part of the RPM NVRA (Name-Version-Release.Arch).
* `htmlRootLocation` - This where the client html files will be deployed to: `/opt/rsa/sa-ui/html`.

__sa-ui/client/sa/sa.gradle__
```java
ext {
    nginxLocation = "/etc/nginx"
    nginxConfLocation = "${project.nginxLocation}/conf.d"

    createRPM = true
    rpmName = "${rootProject.name}-client"
    htmlRootLocation = "/opt/rsa/${rootProject.name}/html"
}
```

## Project `rpm` task

* `nginxConf` - Task to copy `sa-ui/client/sa/rpm/nginx/conf.d/sa-ui-client.conf` to `build/rpm/nginx` to expand the project properties.
* `requires("nginx")` - Requires Nginx to be installed.
* `postInstall` - After installed this RPM, remove the Nginx `default.conf` that is listening to port `80` which is conflicted with the current Security Analytics User Interface.
* Deploy the `sa-ui-client.conf` to the `nginxLocation` on the target machine.
* Deploy all generated files from the `dist/production` directory to the `htmlRootLocation` on the target machine.

__sa-ui/client/sa/sa.gradle__
```java
task nginxConf(type: Copy) {
    from(fileTree("rpm/nginx"))
    into "build/rpm/nginx"
    expand project.properties
}

afterEvaluate {
    rpm {
        dependsOn("nginxConf")

        packageName project.rpmName
        packageDescription "This RPM contains RSA Security Analytics User Interface."

        requires("nginx")

        postInstall file("${project.rpmScriptsDir}/postInstall.sh")

        from(fileTree("build/rpm/nginx")) {
            fileType CONFIG
            into project.nginxLocation
        }

        from(fileTree("dist/production")) {
            into project.htmlRootLocation
        }
    }
}
```

## RPM postInstall script

* After installed this RPM, remove the Nginx `default.conf` that is listening to port `80` which is conflicted with the current Security Analytics User Interface.

__sa-ui/client/sa/rpm/scripts/postInstall.sh__
```bash
#!/bin/bash
rm -f "${project.nginxConfLocation}/default.conf"
```

## Nginx configuration

* Listen to port `4242` for http requests.
* Add a reversed proxy to the threats service on `http://localhost:8081` for all http requests that begins with `/api/user/`. These requests are for authentication.
* Add a reversed proxy to the investigation service on `http://localhost:8080` for all http requests that begins with `/investigation/`.
* Add a reversed proxy to the threats service on `http://localhost:8081` for all http requests that begins with `/threats/`.

__sa-ui/client/sa/rpm/nginx/conf.d/sa-ui-client.conf__
```
server {
    listen       4242 default_server;
    server_name  _;

    # Load configuration files for the default server block.
    include ${project.nginxLocation}/${project.rpmName}.d/*.conf;

    location / {
        root   ${project.htmlRootLocation};
        index  index.html index.htm;
    }

    error_page  404              /404.html;
    location = /404.html {
        root   ${project.nginxHtmlLocation};
    }

    # redirect server error pages to the static page /50x.html
    #
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   ${project.nginxHtmlLocation};
    }

    location /api/user/ {
        proxy_pass       http://localhost:8081;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header Host \$http_host;
    }

    location /investigation/ {
        proxy_pass       http://localhost:8080;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header Host \$http_host;
    }

    location /threats/ {
        proxy_pass       http://localhost:8081;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header Host \$http_host;
    }
}
```

## Run the gradle `rpm` task

```bash
$ cd sa-ui/client/sa
$ ../../gradlew rpm
```

## Built RPM location

```
sa-ui/client/sa/build/distributions/sa-ui-client-10.6.0.0-el6.noarch.rpm
```
