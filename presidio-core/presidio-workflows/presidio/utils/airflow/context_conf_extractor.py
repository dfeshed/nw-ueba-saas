def extract_context_conf(conf_key_name, **context):
    # Assume context is a Dictionary that is not None.
    dag_run = context.get("dag_run")

    if dag_run is None:
        return {}
    else:
        # Assume dag_run is an Object (cannot be None).
        conf = getattr(dag_run, "conf", {})

        if conf is None:
            return {}
        else:
            # Assume conf is a Dictionary (cannot be None).
            # Assume conf_key_name is a String that is not None.
            value = conf.get(conf_key_name, {})
            # Assume value is a Dictionary that can be None.
            return {} if value is None else value
