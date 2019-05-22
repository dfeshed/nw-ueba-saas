DELETE FROM chart where id = 2;
INSERT INTO chart (id, label, conn_id, user_id, chart_type, sql_layout, sql, y_log_scale, show_datatable, show_sql, height, default_params, x_is_date, iteration_no, last_modified) VALUES (2, 'Task duration (seconds) stats', 'presidio_airflow_con', NULL, 'datatable', 'series', 'SELECT Avg(duration)  AS avg_duration,
       Min(duration)  AS min_duration,
       Max(duration)  AS max_duration,
       task_id,
       Count(task_id) AS num_of_samples
FROM   task_instance
WHERE  operator != ''SubDagOperator''
       AND state = ''success''
       AND task_id NOT LIKE ''%sensor%''
       AND task_id NOT LIKE ''%circuit%''
       AND task_id NOT LIKE ''start_operator.%''
       AND task_id NOT LIKE ''end_operator.%''
       AND dag_id NOT LIKE ''maintenance_flow_dag%''
       AND dag_id NOT LIKE ''airflow_zombie_killer%''
       AND dag_id NOT LIKE ''reset_presidio%''
       AND execution_date  >= (SELECT Max(execution_date) - interval ''{{logical_hours_back}}'' hour AS
                                    from_date
                             FROM   task_instance
                             WHERE  operator != ''SubDagOperator''
                                    AND state = ''success''
                                    AND task_id NOT LIKE ''%sensor%''
                                    AND task_id NOT LIKE ''%circuit%''
                                    AND dag_id NOT LIKE ''maintenance_flow_dag%''
                                    AND dag_id NOT LIKE ''airflow_zombie_killer%''
                                    AND dag_id NOT LIKE ''reset_presidio%'')
GROUP  BY task_id
ORDER  BY avg_duration DESC ', false, true, false, 600, '{"logical_hours_back":"720"}', false, 8, '2017-10-31 14:24:03.74253');