INSERT INTO chart (id, label, conn_id, user_id, chart_type, sql_layout, sql, y_log_scale, show_datatable, show_sql, height, default_params, x_is_date, iteration_no, last_modified) VALUES (4, 'Sensor duration (minutes) by date', 'presidio_airflow_con', NULL, 'line', 'series',
'SELECT task_id,
       execution_date,
       duration / 60 AS duration,
       start_date
FROM   task_instance
WHERE  operator != ''SubDagOperator''
       AND state = ''success''
       AND task_id LIKE ''%sensor%''
       AND dag_id LIKE ''%full_flow%''
       AND start_date > current_date - interval ''{{last_days}}'' day
       AND execution_date > current_date - interval ''{{last_execution_date}}'' day ', false, true, true, 600, '{"last_days":"7","last_execution_date":"60"}', true, 3, '2017-10-25 15:04:26.440182');