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
    "badPwdCount"
    "logonCount"
    "primaryGroupID"
    "sAMAccountType"
    "userAccountControl"
    "accountExpires"
    "badPasswordTime"
    "lastLogoff"
    "lockoutTime"
    "assistant"
    "memberOf"
    "managedObjects"
    "manager"
    "masteredBy"
    "directReports"
    "secretary"
    "logonHours"
    "whenChanged"
    "streetAddress"
    "cn"
    "company"
    "c"
    "department"
    "description"
    "displayName"
    "division"
    "mail"
    "employeeID"
    "employeeNumber"
    "employeeType"
    "givenName"
    "l"
    "o"
    "personalTitle"
    "otherFacsimileTelephoneNumber"
    "otherHomePhone"
    "homePhone"
    "otherMobile"
    "mobile"
    "otherTelephone"
    "roomNumber"
    "userPrincipalName"
    "telephoneNumber"
    "title"
    "userParameters"
    "userWorkstations"
    "lastLogon"
    "pwdLastSet"
    "whenCreated"
    "sn"
    "sAMAccountName"
    "thumbnailPhoto"
    "objectSid"
    "objectGUID"
)
## Adding filter:
search_cmd+=( "(&(objectclass=user))(!(objectclass=computer)) ${ad_fields[@]}" )
${search_cmd[@]} > ${out} 
