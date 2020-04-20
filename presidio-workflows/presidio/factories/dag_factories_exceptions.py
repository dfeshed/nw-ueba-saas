class DagsConfigurationContainsOverlappingDatesException(Exception):
    def __init__(self, dag, overlapping_dag):
        message = 'Dags overlapping dates is not allowed. this phenomenon might cause records duplication while ' \
                  'loading data. please fix configuration of the dags={},{} so they would not contain ' \
                  'overlapping dates'\
            .format(dag, overlapping_dag)
        super(DagsConfigurationContainsOverlappingDatesException, self).__init__(message)
