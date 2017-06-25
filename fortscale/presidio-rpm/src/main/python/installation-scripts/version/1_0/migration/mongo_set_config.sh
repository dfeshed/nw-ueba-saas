#!/bin/bash
mongo localhost:27017/admin ../utils/mongo_create_root
mongo localhost:27017/presidio ../utils/mongo_create_presidio_admin
systemctl stop mongod.service
mv /etc/mongod.conf /etc/mongod.conf.no_auth
cp ../utils/mongod.conf /etc/mongod.conf.auth
cp ../utils/mongod.conf /etc/mongod.conf
systemctl start mongod.service