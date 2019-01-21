import os
import sys

PRESIDIO_VERSIONS_FILE_NAME = "/var/lib/netwitness/presidio/presidio_versions.txt"


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


##########################################################
# A Chef execution to trigger a Presidio upgrade DAG run #
##########################################################

# sys.argv[0] is the name of this file
# sys.argv[1] is the previously installed version of Presidio
# sys.argv[2] is the currently installed version of Presidio
if len(sys.argv) == 3:
    from_version = sys.argv[1]
    to_version = sys.argv[2]
    if from_version != "None":
        write_presidio_versions(from_version, to_version)
    sys.exit()


####################################################################
# An Airflow execution to dynamically build a Presidio upgrade DAG #
####################################################################

# There hasn't been a Chef execution to trigger a Presidio upgrade DAG run yet
if not os.path.isfile(PRESIDIO_VERSIONS_FILE_NAME):
    sys.exit()

# There was a Chef execution to trigger a Presidio upgrade DAG run
from airflow import DAG
from datetime import datetime
from presidio.builders.presidioupgrade import presidio_upgrade_dag_builder
from_version, to_version = read_presidio_versions()
dag_id = "presidio_upgrade_dag_from_%s_to_%s" % (from_version, to_version)
dag = DAG(dag_id=dag_id, schedule_interval="@once", start_date=datetime(2019, 1, 1))
presidio_upgrade_dag_builder.build(dag, from_version, to_version)
