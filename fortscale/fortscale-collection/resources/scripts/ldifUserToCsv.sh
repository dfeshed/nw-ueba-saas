#!/bin/bash
for v in HOME USER; do
    ## verify crucial environment variables are ok
    eval val=\$$v
    if [ -z "${val}" ]; then
        echo "ERROR: Corrupted shell environment, failed to expand env variable $v" > /dev/stderr
        exit 1
    fi
done
repo_root="${HOME}/fortscale/fortscale-scripts"
common_vars="${repo_root}/scripts/common_vars.sh"
if [ ! -r "${common_vars}" ]; then
    echo "ERROR: shell environment, failed to find $common_vars script, aborting" > /dev/stderr
    exit 1
fi
source "${common_vars}"
input=$1
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
    "objectSid"
    "objectGUID"
)
cat ${input} |
python ${repo_root}/scripts/ldiftocsv.py ${ad_fields[@]}
