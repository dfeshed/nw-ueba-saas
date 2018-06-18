#!/usr/bin/env bash
set -e

CONFIG_DIR="/etc/netwitness/presidio/configserver/configurations/"
OUTPUT_CONF_FILE_NAME="output-forwarder.properties"
CONFIGURATION_FILE=$CONFIG_DIR$OUTPUT_CONF_FILE_NAME
ENCRYPT_UTIL_APP="/var/lib/netwitness/presidio//install/configserver/EncryptionUtils.jar"

while (( "$#" )); do
  case "$1" in
    --hostname)
        PARAM_HOSTNAME=$2
        shift 2
        ;;
    --port)
        PARAM_PORT=$2
        shift 2
        ;;
    --exchange)
        PARAM_EXCHANGE=$2
        shift 2
        ;;
    --virtualhost)
        PARAM_VIRTUALHOST=$2
        shift 2
        ;;
    --username)
        PARAM_USERNAME=$2
        shift 2
        ;;
    --password)
        PARAM_PASS=$2
        shift 2
        ;;
    --) # end argument parsing
      shift
      break
      ;;
    -*|--*=) # unsupported flags
      echo "Error: Unsupported flag $1" >&2
      exit 1
      ;;

    *) # preserve positional arguments
      PARAM="$PARAMS $1"
      shift
      ;;
  esac
done

# encrypt password
ENC_PASS=$(java -jar "$ENCRYPT_UTIL_APP" encrypt "$PARAM_PASS")
# modify output forwarder params
echo "rabbitmq.hostname=$PARAM_HOSTNAME" >> $CONFIGURATION_FILE
echo "rabbitmq.port=$PARAM_PORT" >> $CONFIGURATION_FILE
echo "rabbitmq.exchange=$PARAM_EXCHANGE" >> $CONFIGURATION_FILE
echo "rabbitmq.virtualhost=$PARAM_VIRTUALHOST" >> $CONFIGURATION_FILE
echo "rabbitmq.password=$ENC_PASS" >> $CONFIGURATION_FILE
echo "rabbitmq.username=$PARAM_USERNAME" >> $CONFIGURATION_FILE

chown presidio:presidio $CONFIGURATION_FILE

