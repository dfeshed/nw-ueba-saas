import logging
from subprocess import Popen, STDOUT, PIPE
from tempfile import gettempdir, NamedTemporaryFile

import os
from airflow.exceptions import AirflowException
from airflow.operators.bash_operator import BashOperator
from airflow.utils.decorators import apply_defaults
from airflow.utils.file import TemporaryDirectory

from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.string_service import is_blank
from datetime import timedelta 

RETRY_ARGS_CONF_KEY = "retry_args"

JVM_ARGS_CONF_KEY = "jvm_args"

DAGS_CONF_KEY = "dags"


class SpringBootJarOperator(BashOperator):
    """
    Execute a Jar file.

    :param jvm_args: The jvm args 
        options:
            jar_path, class_path - mandatory
            main_class - mandatory
            java_path
            xms
            xmx
            timezone
            remote_debug_enabled
            remote_debug_port
            remote_debug_suspend
            jmx_enabled
            jmx_port,
            java_logback_conf_path,
            extra_jvm,
            extra_args
    :type jvm_args: dict
    :param java_args: The java args.
    :type java_args: dict
    """
    ui_color = '#9EB9D4'
    java_args_prefix = '--'
    conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

# def __init__(self):
    #     self.conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

    @apply_defaults
    def __init__(self, command,jvm_args={}, java_args={}, *args, **kwargs):
        logging.debug("creating operator %s" % str(self.__class__ ))
        self.task_id = kwargs['task_id']
        logging.debug("task %s" % str(kwargs['task_id']))
        self.final_conf_path=""
        self._calc_jvm_args(jvm_args)
        self.java_args = java_args
        self.validate_mandatory_fields()
        self.merged_args = self.merge_args()
        self.command = command
        bash_command = self.get_bash_command()

        # add retry callback
        retry_args = self._calc_retry_args()
        if 'retry_callback' in kwargs:
            retry_callback = kwargs['retry_callback']
        else:
            retry_callback = SpringBootJarOperator.handle_retry
        kwargs['params']['retry_command'] = self.get_retry_command()

        super(SpringBootJarOperator, self).__init__(retries=retry_args['retries'],
                                                    retry_delay=timedelta(seconds=int(retry_args['retry_delay'])),
                                                    retry_exponential_backoff=retry_args['retry_exponential_backoff'],
                                                    max_retry_delay=timedelta(
                                                        seconds=int(retry_args['max_retry_delay'])),
                                                    bash_command=bash_command, on_retry_callback=retry_callback,
                                                    *args, **kwargs)

    def _calc_retry_args(self):
        retry_args={}
        if self.task_id:
            # read task jvm args
            retry_args = SpringBootJarOperator.conf_reader.read(conf_key=self.get_retry_args_task_instance_conf_key_prefix())
        if not retry_args:
            # read operator jvm args
            logging.debug((
                "did not found task retry configuration for task_id=%s. settling for operator=%s configuration" % (
                    self.task_id, self.__class__.__name__)))
            retry_args = SpringBootJarOperator.conf_reader.read(conf_key=self.get_retry_args_operator_conf_key_prefix())
        if not retry_args:
            # read default jvm args
            logging.debug((
                "did not found operator retry configuration for operator=%s. settling for default configuration" % (
                    self.__class__.__name__)))
            retry_args = SpringBootJarOperator.conf_reader.read(
                conf_key=self.get_default_retry_args_conf_key())
        return retry_args

    def _calc_jvm_args(self, jvm_args):

        if not jvm_args:
            if self.task_id:
                # read task jvm args
                self.final_conf_path = self.get_jvm_args_task_instance_conf_key_prefix()
                self.jvm_args = SpringBootJarOperator.conf_reader.read(conf_key= self.final_conf_path)
            if not self.jvm_args:
                # read operator jvm args
                logging.debug((
                             "did not found task configuration for task_id=%s. settling for operator=%s configuration" % (
                             self.task_id, self.__class__.__name__)))
                self.final_conf_path = self.get_jvm_args_operator_conf_key_prefix()
                self.jvm_args = SpringBootJarOperator.conf_reader.read(conf_key=self.final_conf_path)
            if not self.jvm_args:
                # read default jvm args
                logging.debug((
                    "did not found operator configuration for operator=%s. settling for default configuration" % (
                         self.__class__.__name__)))
                self.final_conf_path = self.get_default_jvm_args_conf_key()
                self.jvm_args = SpringBootJarOperator.conf_reader.read(
                        conf_key=self.final_conf_path)

        else:
            self.jvm_args = jvm_args

    def update_java_args(self, java_args):
        """
        
        Update java args
        :param java_args: The java args 
        :type java_args: dict
        :return: 
        """
        self.java_args.update(java_args)
        self.bash_command = self.get_bash_command()

    def merge_args(self):
        """
        
        Merge jvm_args and default values that exist in config.ini
        :return: 
        """
        default_options_items = SpringBootJarOperator.conf_reader.read(conf_key="dags.operators.default_jar_values")
        if not default_options_items:
            return dict(self.jvm_args.items())
        tmp = default_options_items.get(JVM_ARGS_CONF_KEY).copy()
        tmp.update(self.jvm_args)

        args = default_options_items.copy()
        args[JVM_ARGS_CONF_KEY] = tmp
        return args

    def get_bash_command(self):
        """
        
        Create bash command in order to run jar

        :return: 
        """
        bash_command = []

        self.java_path(bash_command)

        self.jvm_memory_allocation(bash_command)

        self.extra_jvm(bash_command)

        self.timezone(bash_command)

        self.spring_profile(bash_command)

        self.logback(bash_command)

        self.remote_debug_options(bash_command)

        self.jmx(bash_command)

        self.jar_path(bash_command,self.command)

        self.extra_args(bash_command)

        bash_command = [elem for elem in bash_command if (elem != "''" and elem != "")]
        bash_command = ' '.join(bash_command)
        return bash_command

    def java_path(self,bash_command):
        """
        
        Java location e.g: /usr/bin/java
        :param bash_command: list of bash comments
        :type bash_command: []
        :return: 
        """

        java_path = self.merged_args.get('java_path')
        if not is_blank(java_path):
            bash_command.extend([java_path])
        else:
            logging.error('java_path is not defined')
            raise ValueError('Please set java_path')

    def jvm_memory_allocation(self, bash_command):
        """
        
        Xmx specifies the maximum memory allocation pool for a Java Virtual Machine (JVM),
        Xms specifies the initial memory allocation pool.
        
        :param bash_command: list of bash comments
        :type bash_command: []
        :return: 
        """

        xms = self.merged_args.get(JVM_ARGS_CONF_KEY).get('xms')
        if not is_blank(xms):
            xms = '-Xms%sm' % xms
            bash_command.extend([xms])

        xmx = self.merged_args.get(JVM_ARGS_CONF_KEY).get('xmx')
        if not is_blank(xmx):
            xmx = '-Xmx%sm' % xmx
            bash_command.extend([xmx])

    def extra_jvm(self, bash_command):
        """
        
        extra args (like GC) for Java Virtual Machine (JVM),        
        :param bash_command: list of bash comments
        :type bash_command: []
        :return: 
        """

        extra_jvm = self.merged_args.get(JVM_ARGS_CONF_KEY).get('extra_jvm')
        if not is_blank(extra_jvm):
            bash_command.extend([extra_jvm])

    def extra_args(self, bash_command):
        """
        
        :param bash_command: list of bash comments
        :type bash_command: []
        :return: 
        """
        extra_args = self.merged_args.get('extra_args')
        if not is_blank(extra_args):
            bash_command.extend(extra_args.split(' '))

    def jar_path(self, bash_command,command):
        """
        
        Validate that main_class, jar_path or class_path exist in merged_args, 
        otherwise throw an error
        
        :param bash_command: list of bash comments
        :type bash_command: []
        :raise ValueError: main_class, class path or jar path were not defined
        :return: 
        """
        jar_path_conf_key = 'jar_path'
        class_path = self.merged_args.get(JVM_ARGS_CONF_KEY).get(jar_path_conf_key)

        if not is_blank(self.merged_args.get(JVM_ARGS_CONF_KEY).get(class_path)):
            class_path = '%s;%s' % (self.merged_args.get(JVM_ARGS_CONF_KEY).get(jar_path_conf_key), self.merged_args.get(JVM_ARGS_CONF_KEY).get(class_path))
        if is_blank(class_path):
            logging.error('Could not run jar file without class path or jar path')
            raise ValueError('Please set class path or jar path')
        main_class_conf_key = 'main_class'
        if is_blank(self.merged_args.get(JVM_ARGS_CONF_KEY).get(main_class_conf_key)):
            logging.error('Could not run jar file without main class')
            raise ValueError('Please set the full name of main class')
        else:
            bash_command.extend(['-cp', class_path])
            bash_command.extend(['-Dloader.main=%s' % self.merged_args.get(JVM_ARGS_CONF_KEY).get(main_class_conf_key)])
            bash_command.extend(['org.springframework.boot.loader.PropertiesLauncher'])

        if not is_blank(self.java_args):
            java_args = ' '.join(SpringBootJarOperator.java_args_prefix + '%s %s' % (key, val) for (key, val) in self.java_args.iteritems())
            bash_command.append(command)
            bash_command.append(java_args)

    def jmx(self, bash_command):
        """
        
        JMX is build-in instrumentation that enables to monitor and manage JVM
        
        :param bash_command: list of bash comments
        :type bash_command: []
        :return: 
        """

        jmx_enabled = self.merged_args.get(JVM_ARGS_CONF_KEY).get('jmx_enabled' )
        if not is_blank(jmx_enabled) and jmx_enabled is True:
            jmx_port = self.merged_args.get(JVM_ARGS_CONF_KEY).get('jmx_port')
            if not is_blank('jmx_port'):
                jmx = '-Djavax.management.builder.initial= -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=%s' % (
                    jmx_port)
                jmx_args = self.merged_args.get(JVM_ARGS_CONF_KEY).get('jmx_args')
                if not is_blank(jmx_args):
                    jmx += ' %s' % jmx_args
            else:
                logging.error('Could not find jmx_port')
                raise ValueError('Please set jmx_port')

            bash_command.extend(jmx.split(' '))

    def timezone(self, bash_command):
        """
        
        :param bash_command: list of bash comments
        :type bash_command list
        :return: 
        """
        timezone = self.merged_args.get(JVM_ARGS_CONF_KEY).get('timezone' )
        if not is_blank(timezone):
            bash_command.append(timezone)

    def spring_profile(self, bash_command):
        """

        :param bash_command: list of bash comments
        :type bash_command list
        :return: 
        """
        profile = self.merged_args.get(JVM_ARGS_CONF_KEY).get('profile')
        if not is_blank(profile):
            bash_command.append('-Dspring.profiles.active={}'.format(profile))

    def remote_debug_options(self, bash_command):
        """
        
        Handle debug options
        
        :param bash_command: list of bash comments
        :type bash_command list
        :return: 
        """
        remote_debug_conf_key = 'remote_debug_enabled'
        remote_debug_enabled = self.merged_args.get(JVM_ARGS_CONF_KEY).get(remote_debug_conf_key)
        if not is_blank(remote_debug_enabled) and remote_debug_enabled is True:
            remote_debug_suspend = self.merged_args.get(JVM_ARGS_CONF_KEY).get('remote_debug_suspend' )
            if not is_blank(remote_debug_suspend) and remote_debug_suspend is True:
                remote_debug_suspend = 'y'
            else:
                remote_debug_suspend = 'n'
            remote_debug = '-agentlib:jdwp=transport=dt_socket,address=%s,server=y,suspend=%s' % (
                self.merged_args.get(JVM_ARGS_CONF_KEY).get('remote_debug_port' ), remote_debug_suspend)
            bash_command.append(remote_debug)

    def logback(self, bash_command):
        """
        
        Handle logback options: use java_overriding_logback_conf_path if exist,
        otherwise get java_logback_conf_path
        
        :param bash_command: list of bash comments
        :type bash_command list
        :return: 
        """
        logback_config = '-Dlogging.config=%s'
        overriding_logback_config = False

        logback_conf_key = 'java_overriding_logback_conf_path'
        java_overriding_logback_conf_path = self.merged_args.get(JVM_ARGS_CONF_KEY).get(logback_conf_key)
        if not is_blank(java_overriding_logback_conf_path):
            if os.path.isfile(java_overriding_logback_conf_path):
                overriding_logback_config = True
                bash_command.append(logback_config % (java_overriding_logback_conf_path))
        java_logback_conf_path = self.merged_args.get(JVM_ARGS_CONF_KEY).get(logback_conf_key)
        if not is_blank(java_logback_conf_path) and overriding_logback_config is False:
            bash_command.append(logback_config % java_logback_conf_path)

    def validate_mandatory_fields(self):
        """
        
        Validate if all the mandatory fields were set.
        main_class, jar_path or class_path are mandatory.
        :raise ValueError - throw an exception when fields are missing.
        :return: 
        """
        jar_path = self.jvm_args.get('jar_path')

        class_path = self.jvm_args.get('class_path')

        if is_blank(jar_path) and is_blank(class_path):
            logging.error('Could not run jar file without class path or jar path')
            raise ValueError('Please set class path or jar path')

        main_class = self.jvm_args.get('main_class')
        if is_blank(main_class):
            logging.error('Could not run jar file without main class')
            raise ValueError('Please set the full name of main class')

    def get_operator_conf_key_prefix(self):
        return "%s.operators.%s" % (DAGS_CONF_KEY, self.__class__.__name__)

    def get_default_jvm_args_conf_key(self):
        return "%s.operators.default_jar_values.%s" % (DAGS_CONF_KEY,JVM_ARGS_CONF_KEY)

    def get_jvm_args_operator_conf_key_prefix(self):
        return "%s.%s" % (self.get_operator_conf_key_prefix(), JVM_ARGS_CONF_KEY)

    def get_task_instance_conf_key_prefix(self):
        return "%s.tasks_instances" % (DAGS_CONF_KEY)

    def get_jvm_args_task_instance_conf_key_prefix(self):
        return "%s.%s.%s" % (self.get_task_instance_conf_key_prefix(), self.task_id, JVM_ARGS_CONF_KEY)

    def get_default_retry_args_conf_key(self):
        return "%s.operators.default_jar_values.%s" % (DAGS_CONF_KEY,RETRY_ARGS_CONF_KEY)

    def get_retry_args_operator_conf_key_prefix(self):
        return "%s.%s" % (self.get_operator_conf_key_prefix(), RETRY_ARGS_CONF_KEY)

    def get_retry_args_task_instance_conf_key_prefix(self):
        return "%s.%s.%s" % (self.get_task_instance_conf_key_prefix(), self.task_id, RETRY_ARGS_CONF_KEY)

    def get_retry_command(self):
        bash_command = []
        self.java_path(bash_command)

        self.jvm_memory_allocation(bash_command)

        self.extra_jvm(bash_command)

        self.timezone(bash_command)

        self.spring_profile(bash_command)

        self.logback(bash_command)

        self.jar_path(bash_command=bash_command,command="cleanup")

        bash_command = [elem for elem in bash_command if (elem != "''" and elem != "")]

        return ' '.join(bash_command)

    def get_additional_java_args(self, context):
        return {}

    def handle_retry(self, context):
        logging.info("executing default retry handler")
        if 'retry_command' in context['params']:
            additional_java_args = self.calc_additional_java_args(context)
            self.java_args.update(additional_java_args)
            bash_command = self.get_retry_command()
            logging.info("tmp dir root location: \n" + gettempdir())
            task_instance_key_str = context['task_instance_key_str']
            with TemporaryDirectory(prefix='airflowtmp') as tmp_dir:
                with NamedTemporaryFile(dir=tmp_dir, prefix=("retry_%s" % task_instance_key_str)) as f:
                    f.write(bash_command)
                    f.flush()
                    fname = f.name
                    script_location = tmp_dir + "/" + fname
                    logging.info("Temporary script "
                                 "location :{0}".format(script_location))
                    logging.info("Running retry command: " + bash_command)
                    sp = Popen(
                        ['bash', fname],
                        stdout=PIPE, stderr=STDOUT,
                        cwd=tmp_dir,
                        preexec_fn=os.setsid)

                    logging.info("Retry command output:")
                    line = ''
                    for line in iter(sp.stdout.readline, b''):
                        line = line.decode("UTF-8").strip()
                        logging.info(line)
                    sp.wait()
                    logging.info("Retry command exited with "
                                 "return code {0}".format(sp.returncode))

                    if sp.returncode:
                        raise AirflowException("Retry bash command failed")