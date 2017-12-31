
from airflow.operators.bash_operator import BashOperator
from presidio.builders.maintenance.maintenance_dag_builder import MaintenanceDagBuilder


class PresidioMetircsCleanupDagBuilder(MaintenanceDagBuilder):

    DEFAULT_MAX_APP_METRICS_AGE_IN_DAYS = 30
    DEFAULT_MAX_SYS_METRICS_AGE_IN_DAYS = 30

    def build(self, dag):


        application_metrics_cleanup = """
        echo "Process will be Deleting old application metrics"
        MAX_APP_METRICS_AGE_IN_DAYS="{{dag_run.conf.maxAppMetricsAgeInDays}}"
        if [ "${MAX_APP_METRICS_AGE_IN_DAYS}" == "" ]; then
            echo "maxAppMetricsAgeInDays conf variable isn't included. Using Default '""" + str(PresidioMetircsCleanupDagBuilder.DEFAULT_MAX_APP_METRICS_AGE_IN_DAYS) + """'."
            MAX_APP_METRICS_AGE_IN_DAYS='""" + str(PresidioMetircsCleanupDagBuilder.DEFAULT_MAX_APP_METRICS_AGE_IN_DAYS) + """'
        fi
        # delete /<presidio-monitoring-{now/d}-MAX_APP_METRICS_AGE_IN_DAYS> with URI encoding
        DELETE_STATEMENT="curl -X DELETE http://localhost:9200/%3Cpresidio-monitoring-%7Bnow%2Fd- +${MAX_APP_METRICS_AGE_IN_DAYS} + d%7D%3E"
        eval ${DELETE_STMT}
        DELETE_STMT_EXIT_CODE=$?
        if [ "${DELETE_STMT_EXIT_CODE}" != "0" ]; then
            echo "Delete process failed with exit code '${DELETE_STMT_EXIT_CODE}'"
            exit ${DELETE_STMT_EXIT_CODE}
        fi
        echo "Finished Running Application Metrics Cleanup Process"
        """

        clean_application_metrics_operator = BashOperator(task_id='clean_presidio_application_metrics',
                                                          # delete /<presidio-monitoring-{now/d}-30> with URI encoding
                                                          bash_command=application_metrics_cleanup,
                                                          dag=dag)

        system_metrics_cleanup = """
        echo "Process will be Deleting old application metrics"
        MAX_SYS_METRICS_AGE_IN_DAYS="{{dag_run.conf.maxSysMetricsAgeInDays}}"
        if [ "${DEFAULT_MAX_SYSTEM_METRICS_AGE_IN_DAYS}" == "" ]; then
            echo "maxSysMetricsAgeInDays conf variable isn't included. Using Default '""" + str(PresidioMetircsCleanupDagBuilder.DEFAULT_MAX_SYS_METRICS_AGE_IN_DAYS) + """'."
            MAX_SYS_METRICS_AGE_IN_DAYS='""" + str(PresidioMetircsCleanupDagBuilder.DEFAULT_MAX_SYS_METRICS_AGE_IN_DAYS) + """'
        fi
        # delete /<metricbeat-6.0.0-{now/d}-MAX_APP_METRICS_AGE_IN_DAYS> with URI encoding
        DELETE_STATEMENT="curl -X DELETE http://localhost:9200/%3Cpresidio-monitoring-%7Bnow%2Fd- +${MAX_SYS_METRICS_AGE_IN_DAYS} + d%7D%3E"
        eval ${DELETE_STMT}
        DELETE_STMT_EXIT_CODE=$?
        if [ "${DELETE_STMT_EXIT_CODE}" != "0" ]; then
            echo "Delete process failed with exit code '${DELETE_STMT_EXIT_CODE}'"
            exit ${DELETE_STMT_EXIT_CODE}
        fi
        echo "Finished Running System Metrics Cleanup Process"
        """

        clean_system_metrics_operator = BashOperator(task_id='clean_presidio_system_metrics',
                                                     bash_command=system_metrics_cleanup,
                                                     dag=dag)




        return dag
