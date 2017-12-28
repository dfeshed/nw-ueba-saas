

# cleanup_dag = DAG()
#
# def clean_adapter(ds, **kwargs):
#
#     delete_ca = kwargs['dag_run'].conf['message']
#
#     if delete_ca :
#         do something
#
# clean_adapter_operator = PythonOperator(task_id='clean_adapter',
#                                         python_callable=clean_adapter,
#                                         provide_context=True,
#                                         dag=cleanup_dag)