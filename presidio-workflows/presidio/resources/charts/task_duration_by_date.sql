DELETE FROM chart where id = 3;
INSERT INTO chart (id, label, conn_id, user_id, chart_type, sql_layout, sql, y_log_scale, show_datatable, show_sql, height, default_params, x_is_date, iteration_no, last_modified) VALUES (3, 'Task duration (minutes) by date', 'presidio_airflow_con', NULL, 'line', 'series', 'SELECT task_id,
       execution_date,
       duration / 60 AS duration,
       start_date
FROM   task_instance
WHERE  operator != ''SubDagOperator''
       AND state = ''success''
       AND task_id NOT LIKE ''%sensor%''
       AND task_id NOT LIKE ''%circuit%''
       AND task_id NOT LIKE ''start_operator.%''
       AND task_id NOT LIKE ''end_operator.%''
       AND dag_id LIKE ''full_flow%''
       AND execution_date  >= (SELECT Max(execution_date) - interval ''{{logical_hours_back}}'' hour AS
                                    from_date
                             FROM   task_instance
                             WHERE  operator != ''SubDagOperator''
                                    AND state = ''success''
                                    AND task_id NOT LIKE ''%sensor%''
                                    AND task_id NOT LIKE ''%circuit%''
                                    AND dag_id LIKE ''full_flow%'') ', false, true, false, 600, '{"logical_hours_back":"720"}', true, 41, '2017-10-31 14:28:20.897218');