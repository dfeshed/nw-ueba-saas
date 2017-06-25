#!/bin/bash
mongo localhost:27017/admin /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongo_create_root
mongo localhost:27017/presidio /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongo_create_presidio_admin
systemctl stop mongod.service
mv /etc/mongod.conf /etc/mongod.conf.no_auth
cp /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongod.conf /etc/mongod.conf.auth
cp /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongod.conf /etc/mongod.conf
systemctl start mongod.service