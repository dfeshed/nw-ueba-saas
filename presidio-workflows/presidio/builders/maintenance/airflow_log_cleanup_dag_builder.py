from airflow.configuration import conf
from airflow.operators.bash_operator import BashOperator

from presidio.builders.maintenance.maintenance_dag_builder import MaintenanceDagBuilder


class AirflowLogCleanupDagBuilder(MaintenanceDagBuilder):

    DEFAULT_MAX_LOG_AGE_IN_DAYS = 3
    ENABLE_DELETE = True
    DEFAULT_MAX_LOG_SIZE_IN_BYTES = 10485760
    DEFAULT_NUM_OF_LOG_ENTRIES_TO_KEEP = 100
    ENABLE_TRIMMING = True

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

        log_trimming = """
        echo "Going to trim logs that are too large."

        BASE_LOG_FOLDER='""" + self.base_log_folder + """'
        MAX_LOG_SIZE_IN_BYTES="{{dag_run.conf.maxLogSizeInBytes}}"
        if [ "${MAX_LOG_SIZE_IN_BYTES}" == "" ]; then
            echo "The maxLogSizeInBytes variable isn't included - Using default: '""" + str(AirflowLogCleanupDagBuilder.DEFAULT_MAX_LOG_SIZE_IN_BYTES) + """'."
            MAX_LOG_SIZE_IN_BYTES='""" + str(AirflowLogCleanupDagBuilder.DEFAULT_MAX_LOG_SIZE_IN_BYTES) + """'
        fi
        NUM_OF_LOG_ENTRIES_TO_KEEP="{{dag_run.conf.numOfLogEntriesToKeep}}"
        if [ "${NUM_OF_LOG_ENTRIES_TO_KEEP}" == "" ]; then
            echo "The numOfLogEntriesToKeep variable isn't included - Using default: '""" + str(AirflowLogCleanupDagBuilder.DEFAULT_NUM_OF_LOG_ENTRIES_TO_KEEP) + """'."
            NUM_OF_LOG_ENTRIES_TO_KEEP='""" + str(AirflowLogCleanupDagBuilder.DEFAULT_NUM_OF_LOG_ENTRIES_TO_KEEP) + """'
        fi
        ENABLE_TRIMMING=""" + str("true" if AirflowLogCleanupDagBuilder.ENABLE_TRIMMING else "false") + """

        echo "Base log folder: '${BASE_LOG_FOLDER}'"
        echo "Max log size in bytes: '${MAX_LOG_SIZE_IN_BYTES}'"
        echo "Num of log entries to keep: '${NUM_OF_LOG_ENTRIES_TO_KEEP}'"
        echo "Enable trimming: '${ENABLE_TRIMMING}'"

        FIND_COMMAND="find ${BASE_LOG_FOLDER}/*/* -type f -size +${MAX_LOG_SIZE_IN_BYTES}c"
        echo "Going to execute the following find command: ${FIND_COMMAND}"
        FILES_MARKED_FOR_TRIMMING=()
        while IFS= read -r -d $'\0'; do
            FILES_MARKED_FOR_TRIMMING+=("$REPLY")
        done < <(${FIND_COMMAND} -print0)

        echo "Following files will be trimmed:"
        echo "${FILES_MARKED_FOR_TRIMMING[@]}"
        if [ "${ENABLE_TRIMMING}" == "true" ]; then
            for FILE_MARKED_FOR_TRIMMING in "${FILES_MARKED_FOR_TRIMMING[@]}"; do
                echo "$(tail -"$NUM_OF_LOG_ENTRIES_TO_KEEP" "$FILE_MARKED_FOR_TRIMMING")" > $FILE_MARKED_FOR_TRIMMING
            done
        else
            echo "WARN: Trimming is disabled!"
        fi

        echo "Finished trimming logs that are too large."
        """

        log_cleanup = BashOperator(
            task_id='log_cleanup',
            bash_command=log_cleanup,
            dag=dag
        )
        log_trimming = BashOperator(
            task_id='log_trimming',
            bash_command=log_trimming,
            dag=dag
        )
        log_cleanup >> log_trimming
        return dag
