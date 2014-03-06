#!/bin/bash
repo_root=`dirname $(dirname $0)`
if [[ ! ${repo_root} =~ ^\/.* ]]; then
    repo_root="${HOME}/fortscale/fortscale-scripts"
fi

common_vars="${repo_root}/scripts/common_vars.sh"
if [ -r ${common_vars} ]; then
    source ${common_vars}
fi
out=$1
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
## Adding filter:
search_cmd+=( "(&(objectclass=computer)) ${ad_fields[@]}" )
${search_cmd[@]} > ${out}