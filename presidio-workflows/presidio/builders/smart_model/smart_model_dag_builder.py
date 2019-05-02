from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.smart_model.smart_model_accumulate_operator_builder import SmartModelAccumulateOperatorBuilder
from presidio.builders.smart_model.smart_model_operator_builder import SmartModelOperatorBuilder
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid


class SmartModelDagBuilder(PresidioDagBuilder):
    """
           A "Smart Model DAG" builder -
           The "Smart Model DAG" consists of smart accumulating operator followed by smart model build operator
           The smart accumulating operator responsible for accumulating the smart events
           The smart model build operator is responsible for building the models
           Accumulating the data will happen once a day whereas the models might be built once a day or less (i.e. once a week)

           returns the DAG according to the given smart
           """

    def build(self, dag):
        """
        Fill the given "Smart Model DAG" with smart accumulating operator followed by smart model build operator
        The smart accumulating operator responsible for accumulating the smart events
        The smart model build operator is respobsible for building the models
        Accumulating the data will happen once a day whereas the models might be built once a day or less (i.e. once a week)

        :param dag: The smart_model DAG to populate
        :type dag: airflow.models.DAG
        :return: The smart model DAG, after it has been populated
        :rtype: airflow.models.DAG
        """
        smart_accumulate_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='smart_accumulate_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     SmartModelAccumulateOperatorBuilder.get_accumulate_interval(
                                                                         PresidioDagBuilder.conf_reader),
                                                                     get_schedule_interval(dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 SmartModelAccumulateOperatorBuilder.get_min_gap_from_dag_start_date_to_start_accumulating(
                                                     PresidioDagBuilder.conf_reader),
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(dag))
        )

        smart_model_accumulate_operator = SmartModelAccumulateOperatorBuilder().build(dag)

        smart_model_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='smart_model_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     SmartModelOperatorBuilder.get_build_model_interval(
                                                                         PresidioDagBuilder.conf_reader),
                                                                     get_schedule_interval(dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 SmartModelOperatorBuilder.get_min_gap_from_dag_start_date_to_start_modeling(
                                                     PresidioDagBuilder.conf_reader),
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(dag)))

        smart_model_operator = SmartModelOperatorBuilder().build(dag)
        smart_accumulate_short_circuit_operator >> smart_model_accumulate_operator >> smart_model_short_circuit_operator >> smart_model_operator
        return dag
