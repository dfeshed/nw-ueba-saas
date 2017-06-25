#!/bin/bash

echo "Mongo: creating superadmin user"
mongo localhost:27017/admin /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongo_create_root.js
echo "Mongo: creating admin user"
mongo localhost:27017/presidio /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongo_create_presidio_admin.js
echo "Mongo: stopping service"
sudo systemctl stop mongod.service
echo "Mongo: importing & backuping configuration files "
sudo mv /etc/mongod.conf /etc/mongod.conf.no_auth
sudo cp /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongod.conf /etc/mongod.conf.auth
sudo cp /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongod.conf /etc/mongod.conf
echo "Mongo: starting service"
sudo systemctl start mongod.service