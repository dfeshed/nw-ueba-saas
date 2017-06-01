from presidio.operators.aggregation.aggregations_operator import AggregationsOperator
from datetime import timedelta
from presidio.utils.exceptions import UnsupportedFixedDurationStrategyError


class ScoreAggregationsOperator(AggregationsOperator):
    """
    Runs a "Score Aggregations" task (JAR). Currently there are 2 tasks - Hourly and daily. The fixed_duration_strategy
    determines which JAR is executed - Hourly score aggregations or daily score aggregations. Each task:
    1. Reads the relevant enriched events from the DB.
    2. Scores the relevant fields in these events.
    3. Creates score aggregation buckets from the scored events.
    4. Creates score aggregation events from these buckets.
    5. Writes the score aggregation events to the DB.
    """

    # Color configurations for the Airflow UI
    ui_color = '#1abc9c'
    ui_fgcolor = '#000000'

    def get_task_name(self):
        """
        :return: The task name
        """

        return 'score_aggregations'

    def get_jar_file_path(self):
        """
        Chooses the right JAR file according to the fixed_duration_strategy.
        :return: The full path to the chosen "Score Aggregations" JAR file
        """

        if self.fixed_duration_strategy == timedelta(hours=1):
            return '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test-mock-project-0.0.1-SNAPSHOT.jar'
        elif self.fixed_duration_strategy == timedelta(days=1):
            return '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test-mock-project-0.0.1-SNAPSHOT.jar'
        else:
            raise UnsupportedFixedDurationStrategyError(self.fixed_duration_strategy)

    def get_main_class(self):
        """
       Chooses the right main class of JAR file according to the fixed_duration_strategy.
       :return: The main class name of JAR file
       """

        if self.fixed_duration_strategy == timedelta(hours=1):
            return 'com.fortscale.test.TestMockProjectApplication'
        elif self.fixed_duration_strategy == timedelta(days=1):
            return 'com.fortscale.test.TestMockProjectApplication'
        else:
            raise UnsupportedFixedDurationStrategyError(self.fixed_duration_strategy)