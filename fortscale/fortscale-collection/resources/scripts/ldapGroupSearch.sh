#!/bin/bash
repo_root=`dirname $(dirname $0)`
if [[ ! ${repo_root} =~ ^\/.* ]]; then
    repo_root="${HOME}/fortscale/fortscale-scripts"
fi

common_vars="${repo_root}/scripts/common_vars.sh"
if [ -r ${common_vars} ]; then
    source ${common_vars}
fi

outputfile=$1

ad_fields=(
    "distinguishedName"
    "name"
    "isCriticalSystemObject"
    "isDeleted"
    "groupType"
    "sAMAccountType"
    "memberOf"
    "managedBy"
    "managedObjects"
    "masteredBy"
    "member"
    "nonSecurityMember"
    "nonSecurityMemberBL"
    "directReports"
    "secretary"
    "whenChanged"
    "whenCreated"
    "accountNameHistory"
    "cn"
    "description"
    "displayName"
    "mail"
    "sAMAccountName"
    "objectSid"
    "objectGUID"    
)
mongo_fields="$( join_with_glue ',' timestamp ${ad_fields[@]} timestampepoch)"
## Adding filter:
search_cmd+=( "(&(objectclass=group)) ${ad_fields[@]}" )
echo "${search_cmd[@]}"
${search_cmd[@]} > ${outputfile}