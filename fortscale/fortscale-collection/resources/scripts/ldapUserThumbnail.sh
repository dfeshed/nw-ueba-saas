#!/bin/bash
repo_root=`dirname $(dirname $0)`
if [[ ! ${repo_root} =~ ^\/.* ]]; then
    repo_root="${HOME}/fortscale/fortscale-scripts"
fi

common_vars="${repo_root}/scripts/common_vars.sh"
if [ -r ${common_vars} ]; then
    source ${common_vars}
fi
ad_fields=(
	"thumbnailPhoto"
    "objectGUID"
)
## Adding filter:
if [ ! -z "$1" ]
    then
        ou_filter=$1
        search_cmd=(
            ldapsearch -LLL -x -H ldap://${dc_address}
            -D "${domain_username}"
            -w "${domain_password}"
            -E pr=1000/noprompt
            -b "${ou_filter},${domain_base_search}"
        )
fi
search_cmd+=( "(&(objectclass=user)) ${ad_fields[@]}" )
${search_cmd[@]} |
python ${repo_root}/scripts/ldiftocsv.py ${ad_fields[@]} 
