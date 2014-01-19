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
    "objectGUID"
    "thumbnailPhoto"
)
mongo_fields="$( join_with_glue ',' timestamp ${ad_fields[@]} timestampepoch)"
## Adding filter:
search_cmd+=( "(&(objectclass=user)) ${ad_fields[@]}" )
${search_cmd[@]} |
python ldiftocsv.py ${ad_fields[@]} 
