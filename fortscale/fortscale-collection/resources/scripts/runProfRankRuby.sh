#!/bin/bash
repo_root=`dirname $(dirname $0)`
if [[ ! ${repo_root} =~ ^\/.* ]]; then
    repo_root="${HOME}/fortscale/fortscale-scripts"
fi

common_vars="${repo_root}/scripts/common_vars.sh"
if [ -r ${common_vars} ]; then
    source ${common_vars}
fi
logger -t ${curr_log_tag} -p ${curr_log_prio} -- "Running ruby runner"
run_prof_rank_ruby=(
    "${prof_rank_dir}/lib/runner.sh"
    "${server}"
    "ad_user"
    "ad_user_features_extraction"
)
run_array_cmd_stdout_to_syslog "${run_prof_rank_ruby[@]}"