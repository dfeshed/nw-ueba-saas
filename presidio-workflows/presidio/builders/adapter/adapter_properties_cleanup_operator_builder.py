from airflow.operators.bash_operator import BashOperator


ADAPTER_PROPERTIES_PATH = "/var/lib/netwitness/presidio/flume/conf/adapter/"


def build_adapter_properties_cleanup_operator(cleanup_dag, num_hours_not_delete, task_id):
    adapter_clean_bash_command = "find {0} -type f -name '*-*' -mmin +{1} -exec rm -f {{}} \;"\
        .format(ADAPTER_PROPERTIES_PATH, num_hours_not_delete * 60)
    clean_adapter_operator = BashOperator(task_id=task_id,
                                          bash_command=adapter_clean_bash_command,
                                          dag=cleanup_dag)
    return clean_adapter_operator
