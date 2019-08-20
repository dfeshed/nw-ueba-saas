#!/usr/bin/env bash
set -e

PRESIDIO_HOME="/home/presidio"
ENV_PROPERTIES_PATH="$PRESIDIO_HOME/environment.properties"

function createEnvironmentPropertiesFile {
        rm -f ${ENV_PROPERTIES_PATH}
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
        cat ${ENV_PROPERTIES_PATH}

        if [[ $( cat ${ENV_PROPERTIES_PATH} | wc -l ) -gt 0  ]] ; then
            echo "New environment.properties file is created"
            exit 0
    	else
    	    echo "environment.properties is broken"
    	    exit 1
    	fi
}


function getProperty {
   PROP_KEY=$1
   PROP_VALUE=`cat ${ENV_PROPERTIES_PATH} | grep "$PROP_KEY" | cut -d'=' -f2`
   echo $PROP_VALUE
}


function help {
    echo "Create new environment.properties file: env_properties_manager.sh -c"
    echo "Get property by name: env_properties_manager.sh -g log-decoder"
}


case $1 in
        -c | --create )
                                createEnvironmentPropertiesFile
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

