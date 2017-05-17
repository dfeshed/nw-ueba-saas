class InvalidDefaultValueException(Exception):
    def __init__(self, conf_key, default_value, default_value_file_path):
        message = 'only one of the args: default_value & default_value_file_path should be passed' \
                  'got both: conf_key={}, default_value={}, default_value_file_path={}'.format(conf_key, default_value,
                                                                                               default_value_file_path)
        super(InvalidDefaultValueException, self).__init__(message)


class DefaultConfFileNotValidException(Exception):
    def __init__(self, default_value_file_path, cause):
        message = 'could read default_value_file_path={}'.format(default_value_file_path)
        super(DefaultConfFileNotValidException, self).__init__(message + u', caused by ' + repr(cause))
