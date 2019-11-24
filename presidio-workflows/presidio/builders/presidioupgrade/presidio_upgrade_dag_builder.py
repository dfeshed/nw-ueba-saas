import os
from airflow.models import Variable
from airflow.operators.bash_operator import BashOperator
from airflow.operators.python_operator import PythonOperator
from airflow.utils.db import provide_session
from datetime import timedelta

PRESIDIO_UPGRADE_STATE_KEY = "presidio_upgrade_state"


@provide_session
def delete_presidio_upgrade_state(session=None):
    session.query(Variable).filter(Variable.key == PRESIDIO_UPGRADE_STATE_KEY).delete()


@provide_session
def is_presidio_upgrade_state_present(session=None):
    return session.query(Variable).filter(Variable.key == PRESIDIO_UPGRADE_STATE_KEY).first() is not None


def build(dag, from_version, to_version):
    # The absolute path of this file
    absolute_path = os.path.abspath(__file__)
    # The absolute path of this file's parent directory
    directory_name = os.path.dirname(absolute_path)
    # The absolute path of the "versions" directory
    directory_name = "%s/versions" % directory_name
    # A list of all the versions that have an upgrade script
    # A file named "11.2.0.5.py" is an upgrade script TO version 11.2.0.5
    versions = [version[:-3] for version in os.listdir(directory_name)
                if version.endswith(".py") and version != "__init__.py"]
    # A filtered and sorted list containing only the relevant versions
    versions = [version for version in versions
                if version_comparator(from_version, version) < 0
                and version_comparator(version, to_version) <= 0]
    versions.sort(cmp=version_comparator)

    previous = PythonOperator(task_id="presidio_upgrade_start",
                              python_callable=lambda argument: Variable.set(PRESIDIO_UPGRADE_STATE_KEY, "running"),
                              retries=99999,
                              retry_delay=timedelta(minutes=5),
                              dag=dag)

    for version in versions:
        current = BashOperator(bash_command="python %s/%s.py" % (directory_name, version),
                               dag=dag,
                               retries=99999,
                               retry_delay=timedelta(minutes=5),
                               task_id=version)
        previous >> current
        previous = current

    previous >> PythonOperator(task_id="presidio_upgrade_end",
                               python_callable=delete_presidio_upgrade_state,
                               retries=99999,
                               retry_delay=timedelta(minutes=5),
                               dag=dag)


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
