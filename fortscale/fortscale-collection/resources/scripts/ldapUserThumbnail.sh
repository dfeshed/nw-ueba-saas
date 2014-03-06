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
search_cmd+=( "(&(objectclass=user)) ${ad_fields[@]}" )
${search_cmd[@]} |
python ${repo_root}/scripts/ldiftocsv.py ${ad_fields[@]} 
