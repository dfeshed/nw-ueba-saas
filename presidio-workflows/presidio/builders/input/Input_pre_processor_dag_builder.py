from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.input.input_pre_processor_operator import InputPreProcessor
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton


class InputPreProcessorDagBuilder(PresidioDagBuilder):

    def __init__(self):
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self.input_pre_processing = self.get_input_pre_processing(conf_reader)

    def build(self, dag):
        for pre_processing in self.input_pre_processing:
            name = pre_processing.get("name")
            arguments = pre_processing.get("arguments")
            self._build_input_pre_processing_operator(dag, name, arguments)
        return dag

    @staticmethod
    def get_input_pre_processing(conf_reader):
        return conf_reader.read(conf_key='input_pre_processing')

    def _build_input_pre_processing_operator(self, dag, name, arguments):
        InputPreProcessor(name=name, arguments=arguments, command=PresidioDagBuilder.presidio_command).build(dag)


