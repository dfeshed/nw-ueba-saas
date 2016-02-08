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
ad_fields=(
    "thumbnailPhoto"
    "objectGUID"
)
## Adding filter:
if [ ! -z "$1" ]; then
    ou_filter=$1
    search_cmd=(
        ldapsearch -LLL -x -H ldap://${dc_address}
        -D "${domain_username}"
        -w "${domain_password}"
        -E pr=1000/noprompt
        -b "${ou_filter}"
    )
fi
search_cmd+=( "(&(objectclass=user)) ${ad_fields[@]}" )
${search_cmd[@]} |
python ${repo_root}/scripts/ldiftocsv.py ${ad_fields[@]} 
