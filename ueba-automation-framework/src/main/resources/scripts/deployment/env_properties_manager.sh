#!/usr/bin/env bash
set -e

ENV_PROPERTIES_PATH="/home/presidio/env.properties"

function createEnvironmentPropertiesFile {
        echo "Getting NW components IPs by \"orchestration-cli-client --list-services\" "

        if ($( orchestration-cli-client --list-services > /dev/null )) ; then
            echo "Found new version of orchestration-cli-client"
            ADD_PARAMETER=""
    	else
    	    echo "Found old version of orchestration-cli-client"
    	    ADD_PARAMETER="--broker nw-node-zero"
    	fi

    	orchestration-cli-client ${ADD_PARAMETER} --list-services | grep -oP 'NAME=\K([\w-]+), HOST=([\d\.]+)' | sed 's/, HOST//g' > ${ENV_PROPERTIES_PATH}
        chmod 777 ${ENV_PROPERTIES_PATH}
        echo "New env.properties file is created"
}


function getProperty {
   PROP_KEY=$1
   PROP_VALUE=`cat ${ENV_PROPERTIES_PATH} | grep "$PROP_KEY" | cut -d'=' -f2`
   echo $PROP_VALUE
}


function help {
    echo "Create new env.properties file: env_properties_manager.sh -c"
    echo "Get property by name: env_properties_manager.sh -g log-decoder"
}


case $1 in
        -c | --create )
                                createEnvironmentPropertiesFile
				                exit 0
                                ;;
        -g | --get )
                                shift
				                getProperty $1
				                exit 0
                                ;;
        * )
                                help
                                exit 0

esac

