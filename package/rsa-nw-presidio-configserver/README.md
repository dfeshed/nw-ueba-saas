# Presidio cloud config server 

Presidio utilizes [Spring cloud config](http://cloud.spring.io/spring-cloud-static/spring-cloud-config/1.3.3.RELEASE/single/spring-cloud-config.html) as a configuration centralization tool.



## Installation

```sh
yum -y install rsa-nw-presidio-configserver

# Configure Mongodb credentials
/var/lib/netwitness/presidio/install/configserver/mongo_creds.sh --db presidio-ui --pass P@ssw0rd
/var/lib/netwitness/presidio/install/configserver/mongo_creds.sh --db presidio --pass P@ssw0rd

# Configure RabitMQ credentials
/var/lib/netwitness/presidio/install/configserver/rabitmq_creds.sh --hostname localhost --port 5672 --exchange carlos.alerts --virtualhost /rsa/system --username guest --password guest

systemctl daemon-reload
systemctl enable presidio-configserver
systemctl start presidio-configserver
```

## Usage
After installation should be accessible via [http://localhost:8888](http://localhost:8888)

### Configuration overrides
can be achieved by placing a matching *.properties or *.yml file under /etc/netwitness/presidio/configserver/configurations/ 

