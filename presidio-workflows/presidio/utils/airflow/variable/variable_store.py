import logging

from airflow.models import Variable


def read_from_variable(key, default_var):
    """
    :return: Try to get the value of the variable
    from airflow's variables key-value store
    if not exist, default value is returned
    """
    variable_value = default_var
    try:
        variable_value = Variable.get(key=key, default_var=default_var)
    except Exception:
        logging.debug("Fails on Filling up the DagBag while airflow resetdb, before tables created")

    return variable_value


def set_to_variable(key, value):
    try:
        Variable.set(key=key, value=value)
    except Exception:
        logging.debug("Fails on Filling up the DagBag while airflow resetdb, before tables created")
