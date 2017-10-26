INSERT INTO chart (id, label, conn_id, user_id, chart_type, sql_layout, sql, y_log_scale, show_datatable, show_sql, height, default_params, x_is_date, iteration_no, last_modified) VALUES (2, 'Task duration (seconds) stats', 'presidio_airflow_con', NULL, 'datatable', 'series',
'SELECT Avg(duration)  AS avg_duration,
       Min(duration)  AS min_duration,
       Max(duration)  AS max_duration,
       task_id,
       Count(task_id) AS num_of_samples
FROM   task_instance
WHERE  operator != ''SubDagOperator''
       AND state = ''success''
       AND task_id NOT LIKE ''%sensor%''
       AND task_id NOT LIKE ''%circuit%''
       AND dag_id LIKE ''%full_flow%''
       AND start_date > current_date - interval ''{{last_days}}'' day
       AND execution_date > current_date - interval ''{{last_execution_date}}'' day
GROUP  BY task_id
ORDER  BY avg_duration DESC ', false, true, true, 600, '{"last_days":"7","last_execution_date":"30"}', false, 6, '2017-10-25 14:26:48.997801');