import itertools
import subprocess
from contextlib import contextmanager


@contextmanager
def metrics_reader(logger, host, *metrics):
    kafka_console_consumer_args = [
        'kafka-console-consumer',
        '--topic', 'metrics',
        '--zookeeper', host + ':2181'
    ]
    grep_args = [
        'grep',
        '-o',
        '-P', '\"(' + '|'.join(metrics) + ')\":(\d+)'
    ]
    logger.info('inspecting metrics: ' + ' '.join(kafka_console_consumer_args) + ' | ' + ' '.join(grep_args))
    kafka_p = subprocess.Popen(kafka_console_consumer_args, stdout=subprocess.PIPE)
    grep_p = subprocess.Popen(grep_args, stdin=kafka_p.stdout, stdout=subprocess.PIPE)
    yield itertools.imap(lambda l: (l[1:l.index('"', 1)], int(l[l.index(':') + 1:])), iter(grep_p.stdout.readline, ''))
    kafka_p.kill()
    grep_p.kill()


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
