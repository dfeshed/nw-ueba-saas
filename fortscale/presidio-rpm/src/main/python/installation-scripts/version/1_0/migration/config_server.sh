#!/bin/bash
cp ../utils/configserver /etc/init.d
systemctl stop configserver
sudo chkconfig --add configserver
systemctl start configserver


