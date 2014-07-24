#!/bin/bash
for v in HOME USER; do
    ## verify crucial environment variables are ok
    eval val=\$$v
    if [ -z "${val}" ]; then
        echo "ERROR: Corrupted shell environment, failed to expand env variable $v" > /dev/$$/fd/2
        exit 1
    fi
done
repo_root="${HOME}/fortscale/fortscale-scripts"
common_vars="${repo_root}/scripts/common_vars.sh"
if [ ! -r "${common_vars}" ]; then
    echo "ERROR:  shell environment, failed to expand env variable $v" > /dev/$$/fd/2
    exit 1
fi
source "${common_vars}"
input=$1
ad_fields=(
    "distinguishedName"
    "operatingSystem"
    "operatingSystemHotfix"
    "operatingSystemServicePack"
    "operatingSystemVersion"
    "lastLogoff"
    "lastLogon"
    "lastLogonTimestamp"
    "logonCount"
    "whenChanged"
    "whenCreated"
    "cn"
    "description"
    "pwdLastSet"
    "memberOf"
    "objectSid"
    "objectGUID"
)
cat ${input} |
python ${repo_root}/scripts/ldiftocsv.py ${ad_fields[@]}
