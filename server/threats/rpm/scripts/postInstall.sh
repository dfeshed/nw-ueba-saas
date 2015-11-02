#!/bin/bash

ln -s "/opt/rsa/sa-ui/service/sa-ui-threats.jar" "/etc/init.d/sa-ui-threats"
chkconfig sa-ui-threats on
