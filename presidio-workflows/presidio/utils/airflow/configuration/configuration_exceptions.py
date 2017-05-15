class InvalidDefaultValue(Exception):
    def __init__(self, conf_key, default_value, default_value_file_path):
        message = 'only one of the args: default_value & default_value_file_path should be passed' \
                  'got both: conf_key={}, default_value={}, default_value_file_path={}'.format(conf_key, default_value,
                                                                                               default_value_file_path)
        super(InvalidDefaultValue, self).__init__(message)
