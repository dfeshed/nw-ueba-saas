DAG_ID_SUFIX = "ueba_flow"


def ueba_flow_decorator_wrapper(func):
    """
    Add ueba_flow as a sufix to dag ids.
    :param func: get_dag_id method
    :return: wrapper
    """
    def wrapper(*args):
        return "{0}_{1}".format(func(*args), DAG_ID_SUFIX)

    return wrapper

