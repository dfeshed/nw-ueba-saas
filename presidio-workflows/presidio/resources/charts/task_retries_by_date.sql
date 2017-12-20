INSERT INTO chart (id, label, conn_id, user_id, chart_type, sql_layout, sql, y_log_scale, show_datatable, show_sql, height, default_params, x_is_date, iteration_no, last_modified) VALUES (7, 'Task retries by date', 'presidio_airflow_con', NULL, 'line', 'series', 'SELECT task_id,
       execution_date,
       try_number,
       start_date
FROM   task_instance
WHERE  dag_id LIKE ''full_flow%''
       AND try_number > 1
       AND execution_date >= (SELECT Max(execution_date) - interval ''{{logical_hours_back}}'' hour
                                     AS from_date
                              FROM   task_instance
                              WHERE  dag_id LIKE ''full_flow%''
                                     AND try_number > 1) ', false, true, true, 600, '{"logical_hours_back":"720"}', true, 5, '2017-12-20 10:03:36.547683');