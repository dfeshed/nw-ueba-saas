INSERT INTO chart (id, label, conn_id, user_id, chart_type, sql_layout, sql, y_log_scale, show_datatable, show_sql, height, default_params, x_is_date, iteration_no, last_modified) VALUES (5, 'Throughput', 'presidio_airflow_con', NULL, 'line', 'series', 'SELECT dag_id1 as dag_id,
        execution_day_1 as execution_day,
        EXTRACT (epoch from (end_time-start_time)/60) as duration_in_minutes,
        to_char(Age(end_time,start_time), ''HH24:MI:SS'') as duration,
        start_time, end_time

FROM   ((SELECT Min(start_date)                   AS start_time,
               Date_trunc(''day'', execution_date) AS execution_day_1,
               dag_id AS dag_id1
        FROM   dag_run
        WHERE  dag_id LIKE ''full_flow%''
               AND dag_id NOT LIKE ''full_flow%.%''
        GROUP  BY 2,3) AS t1
        inner join (SELECT Max(start_date)
                           AS end_time,
                           ( Date_trunc(''day'', execution_date -
                                               interval ''1'' second) )
                                               AS
                                                          execution_day_2,
                                                          dag_id AS dag_id2
                    FROM   dag_run
                    WHERE  dag_id LIKE ''full_flow%''
                           AND dag_id NOT LIKE ''full_flow%.%''
                    GROUP  BY 2,3) AS t2
                ON (t1.execution_day_1 = t2.execution_day_2 AND t1.dag_id1 = t2.dag_id2)) AS t3
WHERE execution_day_1 >= (SELECT Max(execution_date) - interval ''{{logical_days_back}}'' day AS
                                    from_date
                             FROM   task_instance
                             WHERE  operator != ''SubDagOperator''
                                    AND state = ''success''
                                    AND dag_id LIKE ''full_flow%'') ', false, true, true, 600, '{"logical_days_back":"30"}', true, 16, '2017-10-31 14:33:22.879552');