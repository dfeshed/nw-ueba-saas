import os
from presidio.builders.presidioupgrade.presidio_upgrade_operator import PresidioUpgradeOperator
from presidio.builders.presidioupgrade.presidio_upgrade_utils import presidio_version_comparator


class PresidioUpgradeDagBuilder(object):
    @staticmethod
    def build(dag):
        # The absolute path of this file
        absolute_path = os.path.abspath(__file__)
        # The absolute path of this file's parent directory
        directory_name = os.path.dirname(absolute_path)
        # The absolute path of the "versions" directory
        directory_name = "%s/versions" % directory_name
        # A sorted list of all the versions that have an upgrade script
        # A file named "11.2.0.5.py" is an upgrade script TO version 11.2.0.5
        versions = [version[:-3] for version in os.listdir(directory_name)]
        versions.sort(cmp=presidio_version_comparator)

        previous = None

        for version in versions:
            current = PresidioUpgradeOperator(version,
                                              bash_command="python %s/%s.py" % (directory_name, version),
                                              dag=dag,
                                              retries=99999,
                                              task_id=version)

            if previous is not None:
                previous >> current

            previous = current

        return dag
