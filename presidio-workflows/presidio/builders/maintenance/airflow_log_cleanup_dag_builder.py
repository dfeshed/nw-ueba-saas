from airflow.configuration import conf
from airflow.operators.bash_operator import BashOperator

from presidio.builders.maintenance.maintenance_dag_builder import MaintenanceDagBuilder


class AirflowLogCleanupDagBuilder(MaintenanceDagBuilder):

    DEFAULT_MAX_LOG_AGE_IN_DAYS = 3
    ENABLE_DELETE = True
    NUMBER_OF_WORKERS = 1

    def __init__(self):
        self.base_log_folder = conf.get("core", "BASE_LOG_FOLDER")


    def build(self, dag):

        log_cleanup = """
        echo "Getting Configurations..."
        BASE_LOG_FOLDER='""" + self.base_log_folder + """'
        MAX_LOG_AGE_IN_DAYS="{{dag_run.conf.maxLogAgeInDays}}"
        if [ "${MAX_LOG_AGE_IN_DAYS}" == "" ]; then
            echo "maxLogAgeInDays conf variable isn't included. Using Default '""" + str(AirflowLogCleanupDagBuilder.DEFAULT_MAX_LOG_AGE_IN_DAYS) + """'."
            MAX_LOG_AGE_IN_DAYS='""" + str(AirflowLogCleanupDagBuilder.DEFAULT_MAX_LOG_AGE_IN_DAYS) + """'
        fi
        ENABLE_DELETE=""" + str("true" if AirflowLogCleanupDagBuilder.ENABLE_DELETE else "false") + """
        echo "Finished Getting Configurations"
        echo ""
        echo "Configurations:"
        echo "BASE_LOG_FOLDER:      '${BASE_LOG_FOLDER}'"
        echo "MAX_LOG_AGE_IN_DAYS:  '${MAX_LOG_AGE_IN_DAYS}'"
        echo "ENABLE_DELETE:        '${ENABLE_DELETE}'"
        echo ""
        echo "Running Cleanup Process..."
        FIND_STATEMENT="find ${BASE_LOG_FOLDER}/*/* -type f -mtime +${MAX_LOG_AGE_IN_DAYS}"
        echo "Executing Find Statement: ${FIND_STATEMENT}"
        FILES_MARKED_FOR_DELETE=`eval ${FIND_STATEMENT}`
        echo "Process will be Deleting the following directories:"
        echo "${FILES_MARKED_FOR_DELETE}"
        echo "Process will be Deleting `echo "${FILES_MARKED_FOR_DELETE}" | grep -v '^$' | wc -l ` file(s)"     # "grep -v '^$'" - removes empty lines. "wc -l" - Counts the number of lines
        echo ""
        if [ "${ENABLE_DELETE}" == "true" ];
        then
            DELETE_STMT="${FIND_STATEMENT} -delete"
            echo "Executing Delete Statement: ${DELETE_STMT}"
            eval ${DELETE_STMT}
            DELETE_STMT_EXIT_CODE=$?
            if [ "${DELETE_STMT_EXIT_CODE}" != "0" ]; then
                echo "Delete process failed with exit code '${DELETE_STMT_EXIT_CODE}'"
                exit ${DELETE_STMT_EXIT_CODE}
            fi
        else
            echo "WARN: You're opted to skip deleting the files!!!"
        fi
        echo "Finished Running Cleanup Process"
        """

        for log_cleanup_id in range(1, AirflowLogCleanupDagBuilder.NUMBER_OF_WORKERS + 1):
            log_cleanup = BashOperator(
                task_id='log_cleanup_' + str(log_cleanup_id),
                bash_command=log_cleanup,
                dag=dag)

        return dag