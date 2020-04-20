class UnsupportedFixedDurationStrategyError(ValueError):
    def __init__(self, fixed_duration_strategy):
        message = '{} is an unsupported fixed duration strategy.'.format(fixed_duration_strategy)
        super(UnsupportedFixedDurationStrategyError, self).__init__(message)
