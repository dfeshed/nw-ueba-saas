from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.input.input_pre_processor_operator import InputPreProcessorOperator

SCHEMA_NAME_KEY = "name"
PRE_PROCESSORS_KEY = "pre_processors"
PRE_PROCESSORS_NAME_KEY = "name"
STATIC_ARGUMENTS_KEY = "static_arguments"
DYNAMIC_ARGUMENTS_KEY = "dynamic_arguments"


class InputPreProcessingDagBuilder(PresidioDagBuilder):

    def build(self, dag):
        schema_name = dag.default_args.get(SCHEMA_NAME_KEY)
        pre_processors = dag.default_args.get(PRE_PROCESSORS_KEY)
        for pre_processor in pre_processors:
            name = pre_processor.get(PRE_PROCESSORS_NAME_KEY)
            static_arguments = pre_processor.get(STATIC_ARGUMENTS_KEY)
            dynamic_arguments = pre_processor.get(DYNAMIC_ARGUMENTS_KEY)
            self._add_input_pre_processor_operator(dag, name, schema_name, static_arguments, dynamic_arguments)
        return dag

    def _add_input_pre_processor_operator(self, dag, name, schema_name, static_arguments, dynamic_arguments):
        InputPreProcessorOperator(dag=dag, name=name, schema_name=schema_name,
                                  static_arguments=static_arguments,
                                  dynamic_arguments=dynamic_arguments,
                                  command=PresidioDagBuilder.presidio_command,
                                  run_clean_command_before_retry=False)




