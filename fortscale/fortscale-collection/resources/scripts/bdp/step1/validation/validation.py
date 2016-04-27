import json
import time
import os
import sys

import impala_stats
import mongo_stats

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))

import logging
logger = logging.getLogger('validation')


def validate_no_missing_events(host, data_source, timeout, polling_interval):
    data_source_to_job_reports_pipeline = {
        'kerberos_logins': [
            {
                'job_name': '4769-EventsFilterStreaming',
                'data_type_prefix': 'kerberos_logins/HDFSWriterStreamTask'
            },
            {
                'job_name': 'HDFSWriterStreamTask',
                'data_type_prefix': 'kerberos_logins/MultipleEventsPrevalenceModelStreamTask'
            }
        ]
    }
    last_progress_time = time.time()
    last_first_job_report_total_events = -1
    success = False
    while not success:
        logger.info('validating that there are no missing events...')
        num_of_enriched_events = impala_stats.get_num_of_enriched_events(host=host, data_source=data_source)
        num_of_events = num_of_enriched_events
        job_report_results = {}
        for job_report in data_source_to_job_reports_pipeline[data_source]:
            job_report_result = mongo_stats.get_job_report(host=host,
                                                           job_name=job_report['job_name'],
                                                           data_type_regex=job_report['data_type_prefix'])
            job_report_results[job_report['job_name']] = job_report_result
            total_events = job_report_result[job_report['data_type_prefix'] + '- Total Events']
            if num_of_events != total_events:
                success = False
                break
            if total_events > last_first_job_report_total_events:
                last_first_job_report_total_events = total_events
                last_progress_time = time.time()
            num_of_events = job_report_result[job_report['data_type_prefix'] + '- Processed Event']
        else:
            success = True

        if time.time() - last_progress_time >= timeout:
            break
        if not success:
            logger.info('validation failed. Going to sleep for ' + str(polling_interval / 60) +
                        ' minutes and then will try again...')
            time.sleep(polling_interval)

    if success:
        logger.info('validation succeeded')
    else:
        logger.error('validation failed:')
        logger.error('\tnumber of enriched events in impala: ' + str(num_of_enriched_events))
        logger.error('\tjob reports:\n' +
                     '\n\t'.join([''] + json.dumps(job_report_results, indent=4, sort_keys=True).split('\n')))
    return success
