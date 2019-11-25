import os
import subprocess
import sys

#################################################################################
# Note: If any of these constants are changed, it should be done in two places: #
#       1. In the Airflow DAGs folder.                                          #
#       2. In the deployed Presidio .egg file.                                  #
#################################################################################
PRESIDIO_UPGRADE_STATE_FILE_NAME = "/var/lib/netwitness/presidio/presidio_upgrade_state.txt"
PRESIDIO_UPGRADE_VERSIONS_PATH = "{}/{}".format(
    "/var/netwitness/presidio/airflow/venv/lib/python2.7/site-packages/presidio_workflows-1.0-py2.7.egg",
    "presidio/upgrade/versions")
PRESIDIO_VERSIONS_FILE_NAME = "/var/lib/netwitness/presidio/presidio_versions.txt"


def is_presidio_version(version):
    """
    :return: True if the given argument is a string representation of a Presidio version, False otherwise
    :rtype: bool
    """
    version = version.split(".")

    if len(version) != 4:
        return False

    for i in range(4):
        if not version[i].isdigit():
            return False

    return True


def version_comparator(first, second):
    """
    :param first: The first Presidio version
    :type first: str
    :param second: The second Presidio version
    :type second: str
    :return: A negative int, zero or a positive int as first is less than, equal to or greater than second
    :rtype: int
    """
    first = first.split(".")
    second = second.split(".")

    if len(first) != 4 or len(second) != 4:
        raise ValueError("Presidio versions are expected to have four parts (e.g. 11.2.0.5)")

    for i in range(4):
        diff = int(first[i]) - int(second[i])

        if diff != 0:
            return diff

    return 0


def is_chef_execution(command_line_arguments):
    """
    :return: True if the current execution of this Python script is a Chef execution, False otherwise
    :rtype: bool
    """
    if len(command_line_arguments) != 3:
        return False

    # command_line_arguments[0] is the name of this file
    from_version = command_line_arguments[1]
    to_version = command_line_arguments[2]
    return is_presidio_version(from_version) and is_presidio_version(to_version)


def write_presidio_versions(from_version, to_version):
    """
    Write the previously and the currently installed versions of Presidio to a predefined file
    :param from_version: The previously installed version of Presidio (e.g. 11.2.0.5)
    :type from_version: str
    :param to_version: The currently installed version of Presidio (e.g. 11.3.0.0)
    :type to_version: str
    """
    presidio_versions_file = open(PRESIDIO_VERSIONS_FILE_NAME, "w")
    presidio_versions_file.write(from_version + "\n")
    presidio_versions_file.write(to_version + "\n")
    presidio_versions_file.close()
    subprocess.call(["chown", "presidio:presidio", PRESIDIO_VERSIONS_FILE_NAME])


def read_presidio_versions():
    """
    Read the previously and the currently installed versions of Presidio from a predefined file
    :return: [0] The previously installed version of Presidio (e.g. 11.2.0.5)
             [1] The currently installed version of Presidio (e.g. 11.3.0.0)
    :rtype: (str, str)
    """
    presidio_versions_file = open(PRESIDIO_VERSIONS_FILE_NAME, "r")
    from_version = presidio_versions_file.readline().strip()
    to_version = presidio_versions_file.readline().strip()
    presidio_versions_file.close()
    return from_version, to_version


def write_presidio_upgrade_state(state):
    """
    Write the current state of the Presidio upgrade to a predefined file
    :param state: The current state of the Presidio upgrade
    :type state: str
    """
    presidio_upgrade_state_file = open(PRESIDIO_UPGRADE_STATE_FILE_NAME, "w")
    presidio_upgrade_state_file.write(state + "\n")
    presidio_upgrade_state_file.close()
    subprocess.call(["chown", "presidio:presidio", PRESIDIO_UPGRADE_STATE_FILE_NAME])


def presidio_upgrade_state_exists():
    """
    :return: True if the predefined file of the Presidio upgrade state exists, False otherwise
    :rtype: bool
    """
    return os.path.exists(PRESIDIO_UPGRADE_STATE_FILE_NAME)


def remove_presidio_upgrade_state():
    """
    Remove the predefined file of the Presidio upgrade state if it exists
    """
    if presidio_upgrade_state_exists():
        os.remove(PRESIDIO_UPGRADE_STATE_FILE_NAME)


def build_presidio_upgrade_dag(dag, from_version, to_version):
    from airflow.operators.bash_operator import BashOperator
    from airflow.operators.python_operator import PythonOperator
    from datetime import timedelta

    # A list of all the versions that have an upgrade script
    # A file named "11.2.0.5.py" is an upgrade script TO version 11.2.0.5
    versions = [version[:-3] for version in os.listdir(PRESIDIO_UPGRADE_VERSIONS_PATH)
                if version.endswith(".py") and version != "__init__.py"]
    # A filtered and sorted list containing only the relevant versions
    versions = [version for version in versions
                if version_comparator(from_version, version) < 0
                and version_comparator(version, to_version) <= 0]
    versions.sort(cmp=version_comparator)

    previous = PythonOperator(task_id="presidio_upgrade_start",
                              python_callable=write_presidio_upgrade_state,
                              op_kwargs={"state": "running"},
                              retries=99999,
                              retry_delay=timedelta(minutes=5),
                              dag=dag)

    for version in versions:
        current = BashOperator(bash_command="python %s/%s.py" % (PRESIDIO_UPGRADE_VERSIONS_PATH, version),
                               dag=dag,
                               retries=99999,
                               retry_delay=timedelta(minutes=5),
                               task_id=version)
        previous >> current
        previous = current

    previous >> PythonOperator(task_id="presidio_upgrade_end",
                               python_callable=remove_presidio_upgrade_state,
                               retries=99999,
                               retry_delay=timedelta(minutes=5),
                               dag=dag)


def handle_chef_execution(command_line_arguments):
    """
    Handle a Chef execution to trigger a Presidio upgrade DAG run
    :param command_line_arguments: [0] The name of this file
                                   [1] The previously installed version of Presidio
                                   [2] The currently installed version of Presidio
    :type command_line_arguments: list
    """
    from_version = command_line_arguments[1]
    to_version = command_line_arguments[2]
    write_presidio_versions(from_version, to_version)
    write_presidio_upgrade_state("pending")


def handle_airflow_execution():
    """
    Handle an Airflow execution to dynamically build a Presidio upgrade DAG
    :return: The Presidio upgrade DAG if there was a Chef execution to trigger a Presidio upgrade DAG run
             None if there hasn't been a Chef execution to trigger a Presidio upgrade DAG run yet
    :rtype: DAG or NoneType
    """
    if os.path.isfile(PRESIDIO_VERSIONS_FILE_NAME):
        from airflow import DAG
        from datetime import datetime

        from_version, to_version = read_presidio_versions()
        dag_id = "presidio_upgrade_dag_from_%s_to_%s" % (from_version, to_version)
        dag = DAG(dag_id=dag_id, schedule_interval=None, start_date=datetime(2019, 1, 1))
        build_presidio_upgrade_dag(dag, from_version, to_version)
        return dag


dag = handle_chef_execution(sys.argv) if is_chef_execution(sys.argv) else handle_airflow_execution()
