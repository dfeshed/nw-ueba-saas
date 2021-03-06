from presidio.operators.aggregation.aggregations_operator import AggregationsOperator


class FeatureAggregationsOperator(AggregationsOperator):
    """
    Runs the "Feature Aggregations" task (JAR). The task:
    1. Reads the relevant enriched events from the DB.
    2. Creates feature aggregation buckets from these events.
    3. Creates feature aggregation events from these buckets.
    4. Scores the feature aggregation events.
    5. Writes the scored feature aggregation events to the DB.
    """

    # Color configurations for the Airflow UI
    ui_color = '#3498db'
    ui_fgcolor = '#000000'

    def get_task_name(self):
        """
        :return: The task name
        """

        return 'feature_aggregations'