#!/usr/bin/env bash
set -e

CONFIG_DIR="/etc/netwitness/presidio/configserver/configurations/"
APP_CONF_FILE_NAME="application.properties"
UI_CONF_FILE_NAME="presidio-uiconf.properties"
ENCRYPT_UTIL_APP="/var/lib/netwitness/presidio//install/configserver/EncryptionUtils.jar"

while (( "$#" )); do
  case "$1" in
    -d|--db)
      DB=$2
      shift 2
      ;;
    -p|--pass)
        PASS=$2
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
ENC_PASS=$(java -jar "$ENCRYPT_UTIL_APP" encrypt "$PASS")


# modfy config server properties file
if [ "$DB" == "presidio" ]
then
    echo "INFO: modifed presidio-db password"
    echo "mongo.db.password=$ENC_PASS" >> $CONFIG_DIR$APP_CONF_FILE_NAME

elif [ "$DB" == "presidio-ui" ]
then
    echo "INFO: modifed presidio-ui-db password"
    echo "mongo.db.password=$ENC_PASS" >> $CONFIG_DIR$UI_CONF_FILE_NAME

else
    echo "ERROR: DB must be presidio/presidio-ui" >&2
    exit 1
fi
