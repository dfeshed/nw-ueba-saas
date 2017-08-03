from presidio.operators.aggregation.aggregations_operator import AggregationsOperator


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