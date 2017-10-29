import logging

import os
import pkg_resources
from airflow.utils.db import provide_session


@provide_session
def insert_conn(session=None):
    file_path = pkg_resources.resource_filename('presidio',
                                                'resources/charts/connection/connection.sql')
    run_sql_file(file_path, session)


@provide_session
def insert_charts(session=None):
    dir_path = pkg_resources.resource_filename('presidio',
                                               'resources/charts/')

    for file_name in os.listdir(dir_path):
        if file_name.endswith(".sql"):
            file_path = os.path.join(dir_path, file_name)
            run_sql_file(file_path=file_path, session=session)


def run_sql_file(file_path, session):
    with open(file_path, 'r') as data_file:
        sql = data_file.read()
        logging.info("executing sql %s ", sql)
        try:
            session.execute(sql)
        except Exception as e:
            logging.error("got error while executing sql ")
            logging.exception(e)


def deploy_charts():
    insert_conn()
    insert_charts()


