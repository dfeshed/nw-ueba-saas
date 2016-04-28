import json
import time
import os
import sys

import impala_stats
import mongo_stats

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))

import logging
logger = logging.getLogger('validation')


_DATA_SOURCE_TO_JOB_REPORTS_PIPELINE = {
    'vpn': [
        {
            'job_name': 'HDFSWriterStreamTask',
            'data_type_prefix': 'vpn/MultipleEventsPrevalenceModelStreamTask'
        }
    ],
    'vpn_session': [
        {
            'job_name': 'HDFSWriterStreamTask',
            'data_type_prefix': 'vpn_session/MultipleEventsPrevalenceModelStreamTask'
        }
    ],
    'crmsf': [
        {
            'job_name': 'HDFSWriterStreamTask',
            'data_type_prefix': 'crmsf/MultipleEventsPrevalenceModelStreamTask'
        }
    ],
    'wame': [
        {
            'job_name': 'HDFSWriterStreamTask',
            'data_type_prefix': 'wame/MultipleEventsPrevalenceModelStreamTask'
        }
    ],
    'gwame': [
        {
            'job_name': 'HDFSWriterStreamTask',
            'data_type_prefix': 'gwame/MultipleEventsPrevalenceModelStreamTask'
        }
    ],
    'kerberos_logins': [
        {
            'job_name': '4769-EventsFilterStreaming',
            'data_type_prefix': 'kerberos_logins/HDFSWriterStreamTask'
        },
        {
            'job_name': 'HDFSWriterStreamTask',
            'data_type_prefix': 'kerberos_logins/MultipleEventsPrevalenceModelStreamTask'
        }
    ],
    'kerberos_tgt': [
        {
            'job_name': 'HDFSWriterStreamTask',
            'data_type_prefix': 'kerberos_tgt/MultipleEventsPrevalenceModelStreamTask'
        }
    ],
    'ntlm': [
        {
            'job_name': 'HDFSWriterStreamTask',
            'data_type_prefix': 'ntlm/MultipleEventsPrevalenceModelStreamTask'
        }
    ],
    'oracle': [
        {
            'job_name': 'HDFSWriterStreamTask',
            'data_type_prefix': 'oracle/MultipleEventsPrevalenceModelStreamTask'
        }
    ],
    'prnlog': [
        {
            'job_name': 'HDFSWriterStreamTask',
            'data_type_prefix': 'prnlog/MultipleEventsPrevalenceModelStreamTask'
        }
    ],
    'ssh': [
        {
            'job_name': 'HDFSWriterStreamTask',
            'data_type_prefix': 'ssh/MultipleEventsPrevalenceModelStreamTask'
        }
    ]
}


def validate_no_missing_events(host, data_source, timeout, polling_interval):
    num_of_enriched_events = impala_stats.get_num_of_enriched_events(host=host, data_source=data_source)
    num_of_scored_events = impala_stats.get_num_of_scored_events(host=host, data_source=data_source)
    last_progress_time = time.time()
    last_first_job_report_total_events = -1
    success = False
    while not success:
        logger.info('validating that there are no missing events in ' + data_source + '...')
        num_of_events = num_of_enriched_events
        job_report_results = []
        for job_report in _DATA_SOURCE_TO_JOB_REPORTS_PIPELINE[data_source]:
            job_report_result = mongo_stats.get_job_report(host=host,
                                                           job_name=job_report['job_name'],
                                                           data_type_regex=job_report['data_type_prefix'])
            job_report_results.append((job_report['job_name'], job_report_result))
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
        success = success and num_of_events == num_of_scored_events

        if not success:
            if time.time() - last_progress_time >= timeout:
                break
            _print_validation_results(success, num_of_enriched_events, num_of_scored_events, job_report_results)
            logger.info('going to sleep for ' + str(polling_interval / 60) + ' minutes and then will try again...')
            time.sleep(polling_interval)

    _print_validation_results(success, num_of_enriched_events, num_of_scored_events, job_report_results)
    return success


def _print_validation_results(success, num_of_enriched_events, num_of_scored_events, job_report_results):
    if success:
        logger.info('OK')
    else:
        logger.error('FAILED')
        logger.error('\tnumber of enriched events in impala: ' + str(num_of_enriched_events))
        logger.error('\tnumber of scored events in impala: ' + str(num_of_scored_events))
        logger.error('\tjob reports:' +
                     '\n\t'.join([''] + json.dumps(job_report_results, indent=4, sort_keys=True).split('\n')))
    return success
