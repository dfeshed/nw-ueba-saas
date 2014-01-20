#!/bin/bash
repo_root=`dirname $(dirname $0)`
if [[ ! ${repo_root} =~ ^\/.* ]]; then
    repo_root="${HOME}/fortscale/fortscale-scripts"
fi

common_vars="${repo_root}/scripts/common_vars.sh"
if [ -r ${common_vars} ]; then
    source ${common_vars}
fi
input=$1
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
cat ${input} |
python ${repo_root}/scripts/ldiftocsv.py ${ad_fields[@]}
