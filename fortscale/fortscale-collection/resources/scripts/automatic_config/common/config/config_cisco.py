order = None

START_TIME = 1455062400 # 10/2/2016
END_TIME = None
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
