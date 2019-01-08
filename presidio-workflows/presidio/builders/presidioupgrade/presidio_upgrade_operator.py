from airflow.operators.bash_operator import BashOperator
from presidio.builders.presidioupgrade import presidio_upgrade_utils
from presidio.utils.airflow.context_conf_extractor import extract_context_conf


class PresidioUpgradeOperator(BashOperator):
    UPGRADE_CONF_KEY_NAME = "upgrade"
    FROM_VERSION_KEY_NAME = "fromVersion"
    TO_VERSION_KEY_NAME = "toVersion"

    def __new__(cls, version, *args, **kwargs):
        return super(PresidioUpgradeOperator, cls).__new__(cls)

    def __init__(self, version, *args, **kwargs):
        super(PresidioUpgradeOperator, self).__init__(*args, **kwargs)
        self.version = version

    def execute(self, context):
        upgrade_conf = extract_context_conf(PresidioUpgradeOperator.UPGRADE_CONF_KEY_NAME, **context)
        from_version = upgrade_conf.get(PresidioUpgradeOperator.FROM_VERSION_KEY_NAME)
        to_version = upgrade_conf.get(PresidioUpgradeOperator.TO_VERSION_KEY_NAME)
        gt_from_version = presidio_upgrade_utils.presidio_version_comparator(from_version, self.version) < 0
        lte_to_version = presidio_upgrade_utils.presidio_version_comparator(to_version, self.version) >= 0

        if gt_from_version and lte_to_version:
            super(PresidioUpgradeOperator, self).execute(context)
