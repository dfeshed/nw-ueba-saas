# Security Analytics UI

## Build RPMs

Build all 3 RPMs (client, investigation and threats)
```bash
$ cd sa-ui
$ ./gradlew rpm
```

The 3 RPMs can be located at:
```
sa-ui/client/sa/build/distributions/sa-ui-client-10.6.0.0-el6.noarch.rpm
sa-ui/server/investigation/build/distributions/sa-ui-investigation-10.6.0.0-el6.noarch.rpm
sa-ui/server/threats/build/distributions/sa-ui-threats-10.6.0.0-el6.noarch.rpm
```

When deploying the RPMs, the SA UI client can be accessed at port: 4242

## Running / Development

### SA client

The SA UI client can be accessed at [http://localhost:4200](http://localhost:4200)
```bash
$ cd sa-ui/client/sa
$ ../../gradlew build
$ ember serve --proxy=http://localhost:8080
```

### Investigation server

Start Investigation Jetty server on default port 8080
```bash
$ cd sa-ui/server/investigation
$ ../../gradlew run
```

### Threats server

Start Threats Jetty server on default port 8081
```bash
$ cd sa-ui/server/threats
$ ../../gradlew run
```

### Test access to the server

Login, get the build information and logout
```bash
$ curl -i -X POST -d username=admin -d password=netwitness -c ./cookies.txt http://localhost:8080/api/user/login
$ curl -i -X GET -b ./cookies.txt http://localhost:8080/api/info
$ curl -i -X GET -b ./cookies.txt http://localhost:8080/api/user/logout
```
