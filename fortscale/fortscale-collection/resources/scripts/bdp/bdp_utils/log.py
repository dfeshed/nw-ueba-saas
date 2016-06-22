import logging
import sys
from logging.handlers import SysLogHandler

errors_logger = logging.getLogger('bdp_errors')
syslog_handler = SysLogHandler()
syslog_handler.setLevel(logging.ERROR)
errors_logger.addHandler(syslog_handler)


def log_and_send_mail(msg):
    errors_logger.error('ERROR: ' + msg)


def init_logging(logger):
    logging.basicConfig(level=logging.INFO,
                        filename='pythonBdp.log',
                        format='%(asctime)s %(levelname)s %(name)s: %(message)s',
                        datefmt="%d/%m/%Y %H:%M:%S")
    logger.info('running command: ' + ' '.join(sys.argv))
