from presidio.builders.indicator.adapter_dag_builder import AdapterDagBuilder
from presidio.builders.indicator.aggregations_dag_builder import AggregationsDagBuilder
from presidio.builders.indicator.input_dag_builder import InputDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.retention.input.input_retention_dag_builder import InputRetentionDagBuilder
from presidio.factories.model_dag_factory import ModelDagFactory
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval, set_schedule_interval
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid, FIX_DURATION_STRATEGY_DAILY, \
    fixed_duration_strategy_to_string, FIX_DURATION_STRATEGY_HOURLY


class IndicatorDagBuilder(PresidioDagBuilder):

    def build(self, indicator_dag):
        """
        Receives an indicator DAG, creates the adapter, input and scoring operators, links them to the DAG and
        configures the dependencies between them.
        :param indicator_dag: The indicator DAG to populate
        :type indicator_dag: airflow.models.DAG
        :return: The given indicator DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        schema = indicator_dag.default_args.get('schema')

        adapter_operator = self._get_adapter_operator(schema, indicator_dag)
        input_operator = self._get_input_operator(schema, indicator_dag)
        aggregations_operator = self._get_aggregations_operator(schema, indicator_dag)
        self._get_input_retention_operator(schema, indicator_dag)

        model_dag_id = ModelDagFactory.get_dag_id(schema)

        python_callable = lambda context, dag_run_obj: dag_run_obj if is_execution_date_valid(context['execution_date'],
                                                                                              FIX_DURATION_STRATEGY_DAILY,
                                                                                              get_schedule_interval(
                                                                                                  indicator_dag)) else None
        model_trigger = self._create_expanded_trigger_dag_run_operator('{0}_{1}'.format(schema, 'model_trigger_dagrun'),
                                                                       model_dag_id, indicator_dag, python_callable)
        set_schedule_interval(model_dag_id, FIX_DURATION_STRATEGY_DAILY)

        adapter_operator >> input_operator >> aggregations_operator >> model_trigger

        self.remove_multi_point_group_container(indicator_dag)
        return indicator_dag

    def _get_adapter_operator(self, schema, indicator_dag):
        adapter_operator_id = '{}_adapter_operator'.format(schema)
        return self._create_multi_point_group_connector(AdapterDagBuilder(schema), indicator_dag, adapter_operator_id,
                                                        None, False)

    def _get_input_operator(self, schema, indicator_dag):
        input_operator_id = '{}_input_operator'.format(schema)
        return self._create_multi_point_group_connector(InputDagBuilder(schema), indicator_dag, input_operator_id, None,
                                                        False)

    def _get_aggregations_operator(self, schema, indicator_dag):
        fixed_duration_strategy = FIX_DURATION_STRATEGY_HOURLY
        aggregations_operator_id = '{}_{}_aggregations_operator'.format(schema, fixed_duration_strategy_to_string(
            fixed_duration_strategy))
        return self._create_multi_point_group_connector(AggregationsDagBuilder(schema, fixed_duration_strategy),
                                                        indicator_dag, aggregations_operator_id, None, False)

    def _get_input_retention_operator(self, schema, indicator_dag):
        input_retention_operator_id = '{}_input_retention_operator'.format(schema)
        return self._create_multi_point_group_connector(InputRetentionDagBuilder(schema), indicator_dag,
                                                        input_retention_operator_id, None,
                                                        False)
