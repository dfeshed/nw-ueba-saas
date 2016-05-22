from impala.dbapi import connect as cn


def connect(host):
    return cn(host=host, port=21050 if host != 'upload' else 31050)
