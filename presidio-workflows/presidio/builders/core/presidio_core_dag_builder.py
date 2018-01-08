import logging
from datetime import timedelta

from presidio.builders.ade.anomaly_detection_engine_scoring_dag_builder import AnomalyDetectionEngineScoringDagBuilder
from presidio.builders.ade.anomaly_detection_engine_modeling_dag_builder import AnomalyDetectionEngineModelingDagBuilder
from presidio.builders.input.input_dag_builder import InputDagBuilder
from presidio.builders.output.output_dag_builder import OutputDagBuilder
from presidio.builders.output.push_forwarder_task_builder import PushForwarderTaskBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService


class PresidioCoreDagBuilder(PresidioDagBuilder):
    """
    The "Online rum" DAG consists of all the presidio flow
    """

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_sources: The data sources whose events should be handled
        :type data_sources: list[str]
        """

        self.data_sources = data_sources

    def build(self, presidio_core_dag):
        """
        Receives an presidio core DAG, creates the operators (input, ade and output), links them to the DAG and 
        configures the dependencies between them.
        :param presidio_core_dag: The presidio core DAG to populate
        :type presidio_core_dag: airflow.models.DAG
        :return: The given presidio core DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        logging.debug("populating the presidio core dag, dag_id=%s ", presidio_core_dag.dag_id)

        task_sensor_service = TaskSensorService()

        input_sub_dag_operator = self._get_input_sub_dag_operator(presidio_core_dag)
        task_sensor_service.add_task_sequential_sensor(input_sub_dag_operator)

        ade_scoring_sub_dag_operator = self._get_ade_scoring_sub_dag_operator(presidio_core_dag)

        output_sub_dag_operator = self._get_output_sub_dag_operator(presidio_core_dag)
        task_sensor_service.add_task_sequential_sensor(output_sub_dag_operator)

        self._push_forwarding(output_sub_dag_operator, presidio_core_dag)

        ade_modeling_sub_dag_operator = self._get_ade_modeling_sub_dag_operator(presidio_core_dag)
        
        input_sub_dag_operator >> ade_scoring_sub_dag_operator >> output_sub_dag_operator
        ade_scoring_sub_dag_operator >> ade_modeling_sub_dag_operator

        return presidio_core_dag

    def _push_forwarding(self, output_sub_dag_operator, presidio_core_dag):
        default_args = presidio_core_dag.default_args
        should_push_data = default_args.get("should_push_data")
        logging.debug("should_push_data=%s ", should_push_data)
        if should_push_data:
            push_forwarding_task = PushForwarderTaskBuilder().build(presidio_core_dag)
            output_sub_dag_operator >> push_forwarding_task

    def _get_input_sub_dag_operator(self, presidio_core_dag):
        input_dag_id = 'input_dag'

        return self._create_sub_dag_operator(InputDagBuilder(self.data_sources), input_dag_id, presidio_core_dag)

    def _get_ade_scoring_sub_dag_operator(self, presidio_core_dag):
        default_args = presidio_core_dag.default_args
        daily_smart_events_confs = default_args.get("daily_smart_events_confs")
        hourly_smart_events_confs = default_args.get("hourly_smart_events_confs")

        ade_scoring_dag_id = 'ade_scoring_dag'

        builder = AnomalyDetectionEngineScoringDagBuilder(self.data_sources, hourly_smart_events_confs, daily_smart_events_confs)
        return self._create_sub_dag_operator(builder, ade_scoring_dag_id, presidio_core_dag)

    def _get_ade_modeling_sub_dag_operator(self, presidio_core_dag):
        default_args = presidio_core_dag.default_args
        daily_smart_events_confs = default_args.get("daily_smart_events_confs")
        hourly_smart_events_confs = default_args.get("hourly_smart_events_confs")

        ade_modeling_dag_id = 'ade_modeling_dag'

        builder = AnomalyDetectionEngineModelingDagBuilder(self.data_sources, hourly_smart_events_confs, daily_smart_events_confs)
        return self._create_sub_dag_operator(builder, ade_modeling_dag_id, presidio_core_dag)

    def _get_output_sub_dag_operator(self, presidio_core_dag):
        output_dag_id = 'output_dag'

        return self._create_sub_dag_operator(OutputDagBuilder(self.data_sources), output_dag_id, presidio_core_dag)

