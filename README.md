# Security Analytics UI

## Running / Development

### SA client

The SA UI client can be accessed at [http://localhost:4200](http://localhost:4200).
```
$ cd client
$ ember serve --proxy=http://localhost:8080
```

### Investigation server

Start Investigation Jetty server on default port 8080
```
$ cd server/investigation
$ gradle run
```

### Test access to the server

Login, get the build information and logout
```
$ curl -i -X POST -d username=admin -d password=netwitness -c ./cookies.txt http://localhost:8080/api/user/login
$ curl -i -X GET -b ./cookies.txt http://localhost:8080/api/info
$ curl -i -X GET -b ./cookies.txt http://localhost:8080/api/user/logout
```
