### Docker image for nwsaui
How do i build this image locally ?

```
docker build -t asoc-dtr2.rsa.lab.emc.com/rsa/nwsaui:11.0.0-latest .
```

How do i run this image locally ?

```
docker run -p 443:443 -d asoc-dtr2.rsa.lab.emc.com/rsa/nwsaui:11.0.0-latest
```

How do i run this image with docker-compose ?
PS: Remember overwriting the conf file with your set of services

```
version: '2'
services:
  nwsaui:
    image: asoc-dtr2.rsa.lab.emc.com/rsa/nwsaui:11.0.0-latest
    volumes:
      - conf/saui.conf:/etc/nginx/conf.d/saui.conf
    network_mode: "host"
```
