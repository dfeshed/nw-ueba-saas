DELETE FROM chart where id = 4;
INSERT INTO chart (id, label, conn_id, user_id, chart_type, sql_layout, sql, y_log_scale, show_datatable, show_sql, height, default_params, x_is_date, iteration_no, last_modified) VALUES (4, 'Sensor duration (minutes) by date', 'presidio_airflow_con', NULL, 'line', 'series', 'SELECT task_id,
       execution_date,
       duration / 60 AS duration,
       start_date
FROM   task_instance
WHERE  operator != ''SubDagOperator''
       AND state = ''success''
       AND task_id LIKE ''%sensor%''
       AND dag_id NOT LIKE ''maintenance_flow_dag%''
       AND dag_id NOT LIKE ''airflow_zombie_killer%''
       AND dag_id NOT LIKE ''reset_presidio%''
       AND execution_date >= (SELECT Max(execution_date) - interval ''{{logical_hours_back}}'' hour AS
                                    from_date
                             FROM   task_instance
                             WHERE  operator != ''SubDagOperator''
                                    AND state = ''success''
                                    AND task_id LIKE ''%sensor%''
                                    AND dag_id NOT LIKE ''maintenance_flow_dag%''
                                    AND dag_id NOT LIKE ''airflow_zombie_killer%''
                                    AND dag_id NOT LIKE ''reset_presidio%''
                                    ) ', false, true, false, 600, '{"logical_hours_back":"25"}', true, 4, '2017-10-31 14:30:01.993905');