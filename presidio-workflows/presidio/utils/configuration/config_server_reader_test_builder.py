import os

from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton


class ConfigServerConfigurationReaderTestBuilder():
    def build(self):
        reader = ConfigServerConfigurationReaderSingleton().config_reader
        reader.set_properties(properties={
            "dags": {
                "operators": {
                    "default_jar_values": {
                        "java_path": "/usr/bin/java",
                        "jvm_args":
                            {
                                "jar_path": "",
                                "main_class": "com.fortscale.test.TestMockProjectApplication",
                                "xms": "100",
                                "xmx": "2048",
                                "timezone": "-Duser.timezone=UTC",
                                "remote_debug_enabled": False,
                                "remote_debug_suspend": False,
                                "jmx_enabled": False,
                            },
                        "retry_args":{
                            "retries": 3,
                            "retry_delay": 0,
                            "retry_exponential_backoff": True,
                            "max_retry_delay": 300
                        },
                        "subdag_retry_args": {
                            "max_retry_delay": 300,
                            "retries": 4,
                            "retry_delay": 5,
                            "retry_exponential_backoff": True,
                        }
                    }
                }
            }
        })
        default_test_jar_path = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                                             '../../../') + '/tests/resources/jars/test-mock-project-0.0.1-SNAPSHOT.jar'
        reader.properties["dags"]["operators"]["default_jar_values"]["jvm_args"]["jar_path"] = default_test_jar_path

        return reader
