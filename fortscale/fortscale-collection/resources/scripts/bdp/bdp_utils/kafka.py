import itertools
import re
import subprocess
from contextlib import contextmanager


@contextmanager
def read_metrics(logger, host, *metrics):
    kafka_console_consumer_args = [
        'kafka-console-consumer',
        '--topic', 'metrics',
        '--zookeeper', host + ':2181'
    ]
    logger.info('looking for ' + ', '.join(metrics) + ' in metrics by running "' +
                ' '.join(kafka_console_consumer_args) + '"...')
    kafka_p = subprocess.Popen(kafka_console_consumer_args, stdout=subprocess.PIPE)
    lines_iter = iter(kafka_p.stdout.readline, '')
    regex = '"(' + '|'.join(metrics) + ')":(\\d+)'
    matches_iter = itertools.imap(lambda line: re.findall(regex, line), lines_iter)
    flattened_matches_iter = itertools.chain.from_iterable(matches_iter)
    yield flattened_matches_iter
    kafka_p.kill()


def send(logger, host, topic, message):
    echo_args = ['echo', message]
    kafka_console_producer_args = [
        'kafka-console-producer',
        '--broker-list', host + ':9092',
        '--topic', topic
    ]
    logger.info('sending message to kafka: ' + ' '.join(echo_args) + ' | ' + ' '.join(kafka_console_producer_args))
    echo_p = subprocess.Popen(echo_args, stdout=subprocess.PIPE)
    kafka_p = subprocess.Popen(kafka_console_producer_args, stdin=echo_p.stdout)
    kafka_p.wait()
