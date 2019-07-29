from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.input.input_pre_processor_operator import InputPreProcessorOperator


class InputPreProcessorDagBuilder(PresidioDagBuilder):

    def build(self, dag):
        schema_name = dag.default_args.get("schema_name")
        params_list = dag.default_args.get("params_list")
        for pre_processing in params_list:
            name = pre_processing.get("name")
            static_arguments = pre_processing.get("static_arguments")
            dynamic_arguments = pre_processing.get("dynamic_arguments")
            self._build_input_pre_processing_operator(dag, name, schema_name, static_arguments, dynamic_arguments)
        return dag

    @staticmethod
    def get_input_pre_processing(conf_reader):
        return conf_reader.read(conf_key='input_pre_processing')

    def _build_input_pre_processing_operator(self, dag, name, schema_name, static_arguments, dynamic_arguments):
        InputPreProcessorOperator(dag=dag, name=name, schema_name=schema_name,
                                  static_arguments=static_arguments,
                                  dynamic_arguments=dynamic_arguments,
                                  command=PresidioDagBuilder.presidio_command,
                                  run_clean_command_before_retry=False, )




