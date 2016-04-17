import logging
errors_logger = logging.getLogger('step_runner_errors')
fh = logging.FileHandler('/var/log/messages')
fh.setLevel(logging.ERROR)
errors_logger.addHandler(fh)


def log_and_send_mail(msg):
    errors_logger.error('ERROR: ' + msg)
