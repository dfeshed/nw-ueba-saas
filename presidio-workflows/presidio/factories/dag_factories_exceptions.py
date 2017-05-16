class DagsConfigurationContainsOverlappingDates(Exception):
    def __init__(self, dag, overlapping_dag):
        message = 'Dags overlapping dates is not allowed. this phenomenom might cause records dulplication while ' \
                  'loading data. please fix configuration of the dags={},{} so they would not contain overlapping dates'\
            .format(dag, overlapping_dag)
        super(DagsConfigurationContainsOverlappingDates, self).__init__(message)
