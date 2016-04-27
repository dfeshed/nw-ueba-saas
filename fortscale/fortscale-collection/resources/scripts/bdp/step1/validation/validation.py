import time
import os
import sys

import impala_stats
import mongo_stats

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))

import logging
logger = logging.getLogger('validation')


def validate_no_missing_events(host, data_source, timeout, polling_interval):
    data_source_to_job_report_job_name = {
        'kerberos_logins': '4769-EventsFilterStreaming'
    }
    last_progress_time = time.time()
    last_job_report_total = -1
    success = False
    while not success:
        logger.info('validating that there are no missing events...')
        num_of_enriched_events = impala_stats.get_num_of_enriched_events(host=host,
                                                                         data_source=data_source)
        data_source_job_report_job_name = data_source_to_job_report_job_name[data_source]
        num_of_processed_and_filtered_events = \
            mongo_stats.get_num_of_processed_and_filtered_events(host=host,
                                                                 job_report_job_name=data_source_job_report_job_name)
        success = num_of_enriched_events == num_of_processed_and_filtered_events
        if num_of_processed_and_filtered_events > last_job_report_total:
            last_job_report_total = num_of_processed_and_filtered_events
            last_progress_time = time.time()
        if time.time() - last_progress_time >= timeout:
            break
        if not success:
            time.sleep(polling_interval)

    if False and success:
        logger.info('validation succeeded')
    else:
        logger.error('validation failed:')
        logger.error('\tnumber of enriched events in impala: ' + str(num_of_enriched_events))
        logger.error('\tnumber of processed and filtered events in job_report ' + data_source_job_report_job_name +
                     ': ' + str(num_of_processed_and_filtered_events))
    return success
