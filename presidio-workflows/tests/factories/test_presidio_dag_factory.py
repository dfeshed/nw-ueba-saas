from __future__ import generators

import pkg_resources
import pytest
from airflow.operators.dummy_operator import DummyOperator

from presidio.builder.presidio_dag_builder import PresidioDagBuilder
from presidio.factories.dag_factories_exceptions import DagsConfigurationContainsOverlappingDatesException
from presidio.factories.presidio_dag_factory import PresidioDagFactory
from presidio.utils.airflow.dag.dag_factory import DagFactories
from presidio.utils.airflow.variable.variable_configuration_reader import VariableReader
import json


class DummyPresidioDagBuilder(PresidioDagBuilder):
    def build(self, dag):
        DummyOperator(task_id='dummy', dag=dag)


class TestPresidioDagFactory:
    def setup_method(self, method):
        PresidioDagFactory()
        self.dag_builder = DummyPresidioDagBuilder()

    def test_should_raise_exception_for_overlapping_dags(self):
        default_conf_file_path = pkg_resources.resource_filename('tests',
                                                                 'resources/variables/dags/test_workflow_creator_overlapping_config.json')
        name_space = globals()
        with pytest.raises(DagsConfigurationContainsOverlappingDatesException):
            self._create_dags(default_conf_file_path=default_conf_file_path,name_space=name_space)

    def test_should_create_dags_by_configuration(self):
        default_conf_file_path = pkg_resources.resource_filename('tests',
                                                                 'resources/variables/dags/test_workflow_creator_config.json')
        name_space = globals()
        dags = self._create_dags(default_conf_file_path=default_conf_file_path,name_space=name_space)

        with open(default_conf_file_path) as conf_file:
            creator_conf = json.load(conf_file)

        assert creator_conf is not None

        # check that all configured dags are created
        amount_of_dags = len(dags)
        assert amount_of_dags >= 2

        for dag in dags:
            # check that all dags are assigned to namespace scope
            assert name_space[dag.dag_id] is dag
            # check that all active dags was properly build (containing a dummy operator)
            active_tasks = dag.active_tasks
            assert active_tasks is not None
            for task in active_tasks:
                assert isinstance(task, DummyOperator)

    def _create_dags(self, default_conf_file_path,name_space):

        variable_reader = VariableReader(default_value_file_path=default_conf_file_path,
                                         var_key='test_presidio_dag_factory')
        dags = DagFactories.create_dags("PresidioDag", conf_reader=variable_reader, name_space=name_space,
                                        dag_builder=self.dag_builder)
        return dags
