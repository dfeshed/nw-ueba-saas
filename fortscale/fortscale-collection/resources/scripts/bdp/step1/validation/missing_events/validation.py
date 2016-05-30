import json
import logging
import sys
import os

import impala_stats
import mongo_stats

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.run import validate_by_polling

logger = logging.getLogger('step1.validation')


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


def validate_no_missing_events(host, data_source, timeout, polling_interval, start, end):
    logger.info('validating that there are no missing events in ' + data_source + '...')
    num_of_enriched_events = impala_stats.get_num_of_enriched_events(host=host,
                                                                     data_source=data_source,
                                                                     start=start,
                                                                     end=end)
    logger.info('number of enriched events in impala: ' + str(num_of_enriched_events))
    if num_of_enriched_events == 0:
        logger.info('OK')
        return True
    is_valid = validate_by_polling(logger=logger,
                                   progress_cb=lambda: _calc_progress(host=host,
                                                                      data_source=data_source,
                                                                      start=start,
                                                                      end=end,
                                                                      num_of_enriched_events=num_of_enriched_events),
                                   is_done_cb=_have_all_events_arrived,
                                   no_progress_timeout=timeout,
                                   polling=polling_interval)
    if is_valid:
        logger.info('OK')
    else:
        logger.error('FAILED')
    return is_valid


def _calc_progress(host, data_source, start, end, num_of_enriched_events):
    job_report_results = [mongo_stats.get_job_report(host=host,
                                                     job_name=job_report['job_name'],
                                                     data_type_regex=job_report['data_type_prefix'],
                                                     start=start,
                                                     end=end)
                          for job_report in _DATA_SOURCE_TO_JOB_REPORTS_PIPELINE[data_source]]
    logger.info('job reports:' +
                '\n\t'.join([''] + json.dumps(job_report_results, indent=4, sort_keys=True).split('\n')))
    num_of_scored_events = impala_stats.get_num_of_scored_events(host=host,
                                                                 data_source=data_source,
                                                                 start=start,
                                                                 end=end)
    logger.info('number of scored events in impala: ' + str(num_of_scored_events))
    return {
        'job_report_results': job_report_results,
        'num_of_enriched_events': num_of_enriched_events,
        'num_of_scored_events': num_of_scored_events
    }


def _have_all_events_arrived(progress):
    num_of_events = progress['num_of_enriched_events']
    for job_report_result in progress['job_report_results']:
        total_events = job_report_result[filter(lambda key: key.endswith('- Total Events'),
                                                job_report_result.iterkeys())[0]]
        if num_of_events != total_events:
            return False
        num_of_events = job_report_result[filter(lambda key: key.endswith('- Processed Event'),
                                                 job_report_result.iterkeys())[0]]
    return num_of_events == progress['num_of_scored_events']
