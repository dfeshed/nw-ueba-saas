#!/bin/bash
cp table-map-custom.xml /etc/netwitness/ng/envision/etc/
cp index-* /etc/netwitness/ng
cp cef-custom.xml /etc/netwitness/ng/envision/etc/devices/cef
systemctl restart nwconcentrator.service
systemctl restart nwlogcollector.service
systemctl restart nwlogdecoder.service
	if ($( orchestration-cli-client --list-services > /dev/null )) ; then
		BrokerHost=$(orchestration-cli-client --list-services | grep  NAME=broker |cut -d '=' -f 4 | cut -d ':' -f 1)
	else
		BrokerHost=$(orchestration-cli-client --broker nw-node-zero --list-services | grep  NAME=broker |cut -d '=' -f 4 | cut -d ':' -f 1)
	fi
echo "Sleeping for 30 seconds"
sleep 30

for try in {1..5} ; do
	echo "Starting Log-Decoder"
	if [ $(curl -o /dev/null -s -w "%{http_code}\n"  -u admin:netwitness "http://localhost:50102/decoder?msg=start") == '200' ]; then
		echo "Action succeeded"
		break
	else
		echo "bad status code"
		sleep 15
	fi
done
sleep 5
for try in {1..5} ; do
	echo "Strating Concentrator"
	if [ $(curl -o /dev/null -s -w "%{http_code}\n"  -u admin:netwitness "http://localhost:50105/concentrator?msg=start") == '200' ]; then
		echo "Action succeeded"
		break
	else
		echo "bad status code"
		sleep 15
	fi
done
sleep 5
for try in {1..5} ; do
	echo "Strating Broker"
	if [ $(curl -o /dev/null -s -w "%{http_code}\n"  -u admin:netwitness "http://$BrokerHost:50103/broker?msg=start") == '200' ]; then
		echo "Action succeeded"
		break
	else
		echo "bad status code"
		sleep 15
	fi
done
echo "all  files have been updated "