order = None

IS_CISCO = True #TODO: remove this line completely if we don't run the script for CISCO
START_TIME = 1455062400# + 15 * day # 10/2/2016
END_TIME = None # START_TIME + 3 * day
FIXED_W_DAILY = {
    'F': {
        'distinct_number_of_src_machines_vpn_daily': 0.01
    },
    'P': {
        'sum_of_highest_scores_over_src_machines_vpn_daily': 0.0001
    }
}
FIXED_W_HOURLY = {
    'F': {
        'distinct_number_of_src_machines_vpn_hourly': 0.01
    },
    'P': {
        'sum_of_highest_scores_over_src_machines_vpn_hourly': 0.0001
    }
}
REDUCERS = {
    'number_of_events_wame_daily': {'min_value_for_not_reduce': 5.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'number_of_events_gwame_daily': {'min_value_for_not_reduce': 5.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'number_of_successful_kerberos_logins_daily': {'min_value_for_not_reduce': 6.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'number_of_successful_ssh_daily': {'min_value_for_not_reduce': 6.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'number_of_successful_kerberos_tgt_events_daily': {'min_value_for_not_reduce': 30.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.2},
    'number_of_failed_ssh_daily': {'min_value_for_not_reduce': 6.0,'max_value_for_fully_reduce': 1, 'reducing_factor': 0.1},
    'number_of_events_wame_hourly': {'min_value_for_not_reduce': 5.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'number_of_events_gwame_hourly': {'min_value_for_not_reduce': 5.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'distinct_number_of_src_machines_ssh_hourly': {'min_value_for_not_reduce': 7.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.2},
    'number_of_failed_ssh_events_hourly': {'min_value_for_not_reduce': 4.0,'max_value_for_fully_reduce': 1, 'reducing_factor': 0.1},
    'number_of_successful_ssh_events_hourly': {'min_value_for_not_reduce': 20.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.4},
    'distinct_number_of_countries_vpn_daily': {'min_value_for_not_reduce': 3.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'number_of_failed_vpn_daily': {'min_value_for_not_reduce': 6.0,'max_value_for_fully_reduce': 1, 'reducing_factor': 0.1},
    'distinct_number_of_src_machines_kerberos_logins_daily': {'min_value_for_not_reduce': 5.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'number_of_failed_kerberos_tgt_daily': {'min_value_for_not_reduce': 6.0,'max_value_for_fully_reduce': 1, 'reducing_factor': 0.1},
    'distinct_number_of_src_machines_kerberos_tgt_daily': {'min_value_for_not_reduce': 20.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.4},
    'distinct_number_of_src_machines_ssh_daily': {'min_value_for_not_reduce': 8.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.2},
    'distinct_number_of_dst_machines_kerberos_logins_daily': {'min_value_for_not_reduce': 7.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.2},
    'distinct_number_of_dst_machines_ssh_daily': {'min_value_for_not_reduce': 6.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'distinct_number_of_src_machines_vpn_daily': {'min_value_for_not_reduce': 3.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'distinct_number_of_countries_vpn_hourly': {'min_value_for_not_reduce': 3.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'number_of_successful_kerberos_logins_hourly': {'min_value_for_not_reduce': 20.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.4},
    'distinct_number_of_src_machines_kerberos_logins_hourly': {'min_value_for_not_reduce': 8.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.3},
    'number_of_failed_kerberos_tgt_hourly': {'min_value_for_not_reduce': 6.0,'max_value_for_fully_reduce': 1, 'reducing_factor': 0.1},
    'distinct_number_of_src_machines_kerberos_tgt_hourly': {'min_value_for_not_reduce': 20.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.4},
    'number_of_failed_vpn_events_hourly': {'min_value_for_not_reduce': 6.0,'max_value_for_fully_reduce': 1, 'reducing_factor': 0.3},
    'number_of_successful_kerberos_tgt_events_hourly': {'min_value_for_not_reduce': 30.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.2},
    'distinct_number_of_dst_machines_kerberos_logins_hourly': {'min_value_for_not_reduce': 10.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.3},
    'distinct_number_of_dst_machines_ssh_hourly': {'min_value_for_not_reduce': 6.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1},
    'distinct_number_of_src_machines_vpn_hourly': {'min_value_for_not_reduce': 3.0,'max_value_for_fully_reduce': 2, 'reducing_factor': 0.1}
}
