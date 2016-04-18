#!/bin/bash
if [ $# -ne 2 ] ; then
   echo "Proper use: import_qradar_cert.sh <host> <port>"
   exit
fi
cert_file="certfile.pem"
echo QUIT | openssl s_client -connect $1:$2 -showcerts | openssl x509 -outform PEM > $cert_file
sudo $JAVA_HOME/bin/keytool -import -alias "qradar" -file $cert_file -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -noprompt
rm $cert_file