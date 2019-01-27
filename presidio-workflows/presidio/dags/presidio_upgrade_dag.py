import os
import subprocess
import sys

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
        from presidio.builders.presidioupgrade import presidio_upgrade_dag_builder

        from_version, to_version = read_presidio_versions()
        dag_id = "presidio_upgrade_dag_from_%s_to_%s" % (from_version, to_version)
        dag = DAG(dag_id=dag_id, schedule_interval="@once", start_date=datetime(2019, 1, 1))
        presidio_upgrade_dag_builder.build(dag, from_version, to_version)
        return dag


dag = handle_chef_execution(sys.argv) if is_chef_execution(sys.argv) else handle_airflow_execution()
