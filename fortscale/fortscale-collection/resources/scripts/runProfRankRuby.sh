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
logger -t ${curr_log_tag} -p ${curr_log_prio} -- "Running ruby runner"
run_prof_rank_ruby=(
    "${prof_rank_dir}/lib/runner.sh"
    "${server}"
    "ad_user"
)
run_array_cmd_stdout_to_syslog "${run_prof_rank_ruby[@]}"
