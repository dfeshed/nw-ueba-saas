def extract_context_conf(conf_key_name, **context):
    dag_run = context.get("dag_run")

    if dag_run is None:
        return {}
    else:
        conf = getattr(dag_run, "conf", {})
        return conf.get(conf_key_name, {})
