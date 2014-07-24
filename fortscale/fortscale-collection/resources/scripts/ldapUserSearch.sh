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
    "objectSid"
    "objectGUID"
)
## Adding filter:
if [ ! -z "$2" ]
    then
        ou_filter=$2
        search_cmd=(
            ldapsearch -LLL -x -H ldap://${dc_address}
            -D "${domain_username}"
            -w "${domain_password}"
            -E pr=1000/noprompt
            -b "${ou_filter}"
        )
fi
search_cmd+=( "(&(objectclass=user)(!(objectclass=computer))) ${ad_fields[@]}" )
${search_cmd[@]} > ${out} 
