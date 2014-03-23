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
    "isCriticalSystemObject"
    "isDeleted"
    "defaultGroup"
    "memberOf"
    "managedBy"
    "managedObjects"
    "masteredBy"
    "nonSecurityMemberBL"
    "directReports"
    "whenChanged"
    "whenCreated"
    "cn"
    "c"
    "description"
    "displayName"
    "l"
    "ou"
    "objectSid"
    "objectGUID"
)
## Adding filter:
search_cmd+=( "(&(objectclass=organizationalUnit)) ${ad_fields[@]}" )
${search_cmd[@]} > ${out}
