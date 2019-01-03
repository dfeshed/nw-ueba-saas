def extract_context_conf(conf_key_name, **context):
    dag_run = context["dag_run"]

    if dag_run:
        conf = dag_run.conf
        return conf.get(conf_key_name, {}) if conf else {}
    else:
        return {}
