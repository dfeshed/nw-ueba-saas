import logging

import os
import pkg_resources
from airflow.utils.db import provide_session
from airflow import configuration
import re


def get_airflow_password():
    airflow_connection = str(configuration.get('core', 'SQL_ALCHEMY_CONN'))
    airflow_password_re = "postgresql\+psycopg2:\/\/airflow:(.*?)\@127\.0\.0\.1\/airflow"
    return "\'{0}\'".format(str(re.search(airflow_password_re, airflow_connection).group(1)))

@provide_session
def insert_conn(session=None):
    file_path = pkg_resources.resource_filename('presidio',
                                                'resources/charts/connection/connection.sql')
    with open(file_path, 'r') as data_file:
        sql = data_file.read()
        airflow_password = get_airflow_password()
        conn_sql=sql.format(airflow_password)
        run_sql(session, conn_sql)


@provide_session
def insert_charts(session=None):
    dir_path = pkg_resources.resource_filename('presidio',
                                               'resources/charts/')

    for file_name in os.listdir(dir_path):
        if file_name.endswith(".sql"):
            file_path = os.path.join(dir_path, file_name)
            run_sql_file(file_path=file_path, session=session)


@provide_session
def refresh_charts_sequence(session=None):
    file_path = pkg_resources.resource_filename('presidio',
                                                'resources/charts/connection/refresh_chart_seq.sql')
    run_sql_file(file_path, session)


def run_sql_file(file_path, session):
    with open(file_path, 'r') as data_file:
        sql = data_file.read()
        run_sql(session, sql)


def run_sql(session, sql):
    logging.info("executing sql %s ", sql)
    try:
        session.execute(sql)
    except Exception as e:
        logging.error("got error while executing sql ")
        logging.exception(e)


def deploy_charts():
    insert_conn()
    insert_charts()
    refresh_charts_sequence()