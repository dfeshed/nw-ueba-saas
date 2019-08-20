#!/usr/bin/env bash
set -e
LogDecoderHost=$1
BrokerHost=$2
ENV_PROPERTIES_PATH="/home/presidio/env.properties"

echo "%%%%%%%%%%%% Start Running Hybrid Reset DBs Script %%%%%%%%%%%%"

function getEnvProperty {
   PROP_KEY=$1
   PROP_VALUE=`cat ${ENV_PROPERTIES_PATH} | grep "$PROP_KEY" | cut -d'=' -f2`
   echo $PROP_VALUE
}

if [[ -z ${LogDecoderHost} ]]; then
        LogDecoderHost=$(getEnvProperty "log-decoder")
        BrokerHost=$(getEnvProperty "broker")
else
	echo "Log-Decoder have been selected by the user- $LogDecoderHost" 
fi

for try in {1..5} ; do
	echo "Cleaning Log-Decoder"
	if [ $(curl -o /dev/null -s -w "%{http_code}\n"  -u admin:netwitness "http://$LogDecoderHost:50102/decoder?msg=reset&data=1&log=1&force=1") == '200' ]; then
		echo "Action succeeded"
		break
	else 
		echo "bad status code"
		sleep 15
	fi
done

sleep 5

for try in {1..5} ; do
	echo "Cleaning Concentrator"
	if [ $(curl -o /dev/null -s -w "%{http_code}\n"  -u admin:netwitness "http://$LogDecoderHost:50105/concentrator?msg=reset&data=1&log=1&force=1") == '200' ]; then
		echo "Action succeeded"
		break
	else 
		echo "bad status code"
		sleep 15
	fi
done

sleep 30

for try in {1..5} ; do
	echo "Starting Log-Decoder"
	if [ $(curl -o /dev/null -s -w "%{http_code}\n"  -u admin:netwitness "http://$LogDecoderHost:50102/decoder?msg=start") == '200' ]; then
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
	if [ $(curl -o /dev/null -s -w "%{http_code}\n"  -u admin:netwitness "http://$LogDecoderHost:50105/concentrator?msg=start") == '200' ]; then
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
echo "%%%%%%%%%%%% Hybrid Reset DBs Script Completed Successfully %%%%%%%%%%%%"
