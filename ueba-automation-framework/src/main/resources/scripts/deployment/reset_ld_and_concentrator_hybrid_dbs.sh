#!/usr/bin/env bash
set -e
LogDecoderHost=$1
BrokerHost=$2
echo "%%%%%%%%%%%% Start Running Hybrid Reset DBs Script %%%%%%%%%%%%"
if [ -z $LogDecoderHost ]; then
		if ($( orchestration-cli-client --list-services > /dev/null )) ; then
    		LogDecoderHost=$(orchestration-cli-client --list-services | grep  NAME=log-decoder |cut -d '=' -f 4 | cut -d ':' -f 1)
    		BrokerHost=$(orchestration-cli-client --list-services | grep  NAME=broker |cut -d '=' -f 4 | cut -d ':' -f 1)
    		echo "Getting NW components IPs by \"orchestration-cli-client --list-services\" "
    	else
    		LogDecoderHost=$(orchestration-cli-client --broker nw-node-zero --list-services | grep  NAME=log-decoder |cut -d '=' -f 4 | cut -d ':' -f 1)
    		BrokerHost=$(orchestration-cli-client --broker nw-node-zero --list-services | grep  NAME=broker |cut -d '=' -f 4 | cut -d ':' -f 1)
    		echo "Getting NW components IPs by \"orchestration-cli-client --list-services\" "
    	fi
else
	echo "Log-Decoder have been selected by the user- $LogDecoderHost" 
fi

setProperty(){
  echo '#### Going to create /home/presidio/env.properties ####'
  echo "$2=$3" > "$1.tmp"
  echo "$4=$5" >> "$1.tmp"
  mv -f $1.tmp $1
  cat $1
  chmod 777 $1
  echo '#### Done ####'
}

setProperty "/home/presidio/env.properties" "log_decoder.ip" $LogDecoderHost "broker.ip" $BrokerHost

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
