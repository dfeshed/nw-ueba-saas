import logging
from logging.handlers import SysLogHandler
errors_logger = logging.getLogger('step2_errors')
syslog_handler = SysLogHandler()
syslog_handler.setLevel(logging.ERROR)
errors_logger.addHandler(syslog_handler)


def log_and_send_mail(msg):
    errors_logger.error('ERROR: ' + msg)
