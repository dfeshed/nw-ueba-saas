from presidio.builders.model.aggr_model_dag_builder import AggrModelDagBuilder
from presidio.builders.model.raw_model_dag_builder import RawModelDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.services.fixed_duration_strategy import fixed_duration_strategy_to_string, \
    FIX_DURATION_STRATEGY_HOURLY


class ModelDagBuilder(PresidioDagBuilder):
    """
         An "Model DAG" builder  creates  "Aggr Model" and "Raw Model"  operators, links them to the DAG and
        configures the dependencies between them.
         """

    def build(self, model_dag):
        """
        Builds the aggr model and raw model:
        :param model_dag: The model DAG to populate
        :type model_dag: airflow.models.DAG
        :return: The given model DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        schema = model_dag.default_args['schema']
        self._get_raw_model_operator(schema, model_dag)
        self._get_aggr_model_operator(schema, model_dag)

        self.remove_multi_point_group_container(model_dag)
        return model_dag

    def _get_raw_model_operator(self, schema, model_dag):
        raw_model_operator_id = '{}_raw_model'.format(schema)
        return self._create_multi_point_group_connector(RawModelDagBuilder(schema), model_dag, raw_model_operator_id,
                                                        None, False)

    def _get_aggr_model_operator(self, schema, model_dag):
        fixed_duration_strategy = FIX_DURATION_STRATEGY_HOURLY
        aggr_model_operator_id = '{}_{}_aggr_model'.format(schema,
                                                           fixed_duration_strategy_to_string(fixed_duration_strategy))
        return self._create_multi_point_group_connector(AggrModelDagBuilder(schema, fixed_duration_strategy),
                                                        model_dag, aggr_model_operator_id, None, False)
