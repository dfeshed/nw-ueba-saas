#!/bin/bash
#git pull
dest="/var/lib/tomcat6/webapps/fortscale-webapp/resources"
if [ $# -gt 0 ]; then
    dest=$1
fi

if [ ! -d "${dest}" ]; then
    echo  "Destination folder: ${dest} does not exist!"
    exit 1
fi
src="dist"
if [ ! -d ${src} ]; then
    src="app"
fi
sudo service tomcat6 stop
sudo -u tomcat -g tomcat cp -r ${src}/* "${dest}"/
sudo service tomcat6 start
