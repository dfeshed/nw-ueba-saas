#!/bin/bash
echo "content of ../utils/:"
ll ../utils/
cp ../utils/configserver /etc/init.d
echo "we are here"
pwd
systemctl stop configserver
sudo chkconfig --add configserver
systemctl start configserver


