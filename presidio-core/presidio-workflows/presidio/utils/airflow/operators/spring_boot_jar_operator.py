import functools
import os
from abc import abstractmethod
from datetime import timedelta
from subprocess import Popen, STDOUT, PIPE
from tempfile import gettempdir, NamedTemporaryFile

from airflow import LoggingMixin
from airflow.api.common.experimental import pool
from airflow.exceptions import AirflowException
from airflow.models import Variable
from airflow.operators.bash_operator import BashOperator
from airflow.utils.decorators import apply_defaults
from airflow.utils.file import TemporaryDirectory

from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.string_service import is_blank

POOL_NAME_CONF_KEY = "pool_name"
RETRY_ARGS_CONF_KEY = "retry_args"
JVM_ARGS_CONF_KEY = "jvm_args"
DAGS_CONF_KEY = "dags"
EXECUTION_TIMEOUT_CONF_KEY = "execution_timeout_in_seconds"

RETRY_STATE_KEY_PREFIX = "retry_state_"
RETRY_RUNNING_STATE = "RUNNING"
RETRY_FAIL_STATE = "FAIL"
RETRY_SUCCESS_STATE = "SUCCESS"

_BASH_OPERATOR_INIT_METHOD_ARGUMENT_NAMES = [
    "task_id",
    "owner",
    "email",
    "email_on_retry",
    "email_on_failure",
    "retries",
    "retry_delay",
    "retry_exponential_backoff",
    "max_retry_delay",
    "start_date",
    "end_date",
    "schedule_interval",
    "depends_on_past",
    "wait_for_downstream",
    "dag",
    "params",
    "default_args",
    "adhoc",
    "priority_weight",
    "queue",
    "pool",
    "sla",
    "execution_timeout",
    "on_failure_callback",
    "on_success_callback",
    "on_retry_callback",
    "trigger_rule",
    "resources",
    "run_as_user",
    "task_concurrency"
]


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

    @apply_defaults
    def __init__(self, command, jvm_args={}, java_args={}, java_retry_args={}, component=None, condition=None, *args, **kwargs):
        self.log.debug("creating operator %s" % str(self.__class__))
        self.task_id = kwargs['task_id']
        self.log.debug("task %s" % str(kwargs['task_id']))
        self.final_conf_path = ""
        self.component = component
        self._calc_jvm_args(jvm_args)
        self.java_args = java_args
        self.java_retry_args = java_retry_args
        self.validate_mandatory_fields()
        self.merged_args = self.merge_args()
        self.command = command
        self.condition = condition
        bash_command = self.get_bash_command()
        pool_name = self._get_pool_name_if_exist()
        execution_timeout = self.get_execution_timeout()

        # add retry callback
        retry_args = self._calc_retry_args()

        if 'retry_java_args_method' in kwargs:
            kwargs['params']['retry_java_args_method'] = kwargs['retry_java_args_method']
        if 'retry_extra_params' in kwargs:
            kwargs['params']['retry_extra_params'] = kwargs['retry_extra_params']
        if 'retry_callback' in kwargs:
            retry_fn = kwargs['retry_callback']
        else:
            retry_fn = SpringBootJarOperator.clean_before_retry
        retry_callback = self.get_retry_callback(retry_fn=retry_fn)
        if self._should_run_clean_command_before_retry(kwargs):
            kwargs['params']['retry_command'] = self.get_retry_command()

        super(SpringBootJarOperator, self).__init__(
            retries=retry_args['retries'],
            retry_delay=timedelta(seconds=int(retry_args['retry_delay'])),
            retry_exponential_backoff=retry_args['retry_exponential_backoff'],
            max_retry_delay=timedelta(seconds=int(retry_args['max_retry_delay'])),
            bash_command=bash_command,
            on_retry_callback=retry_callback,
            pool=pool_name,
            execution_timeout=execution_timeout,
            # BaseOperator will write a warning to the log if invalid arguments are passed to the __init__ method
            # (i.e. arguments that are passed through *args or **kwargs/arguments that are not named), because the
            # support for passing such arguments will be dropped in Airflow 2.0. Since the log is flooded with this
            # warning, arguments that are not named in the __init__ method are filtered out from kwargs.
            **{key: kwargs[key] for key in _BASH_OPERATOR_INIT_METHOD_ARGUMENT_NAMES if key in kwargs})

    def get_retry_callback(self, retry_fn):
        return functools.partial(SpringBootJarOperator.handle_retry, retry_fn=retry_fn)

    def execute(self, context):
        retry_task_state = SpringBootJarOperator.get_task_retry_value(context, RETRY_SUCCESS_STATE)
        if retry_task_state == RETRY_SUCCESS_STATE:
            result = True
            if self.condition:
                result = self.condition(context)
                self.log.info("Condition result is %s", result)
            if result:
                self._is_execution_date_valid(context)
                self.log.info('Proceeding with downstream tasks...')
                super(SpringBootJarOperator, self).execute(context)
            else:
                self.log.info('Skip the task...')
        else:
            ti = context['task_instance']
            skip_msg = 'skipping try attempt %s: last retry did not succeed. retry state: %s' % (
                ti.try_number, retry_task_state)
            self.log.info(skip_msg)
            raise AirflowException(skip_msg)

    @abstractmethod
    def _is_execution_date_valid(self, context):
        pass

    def _should_run_clean_command_before_retry(self, kwargs):
        if 'run_clean_command_before_retry' in kwargs and not kwargs['run_clean_command_before_retry']:
            return False

        return True

    def _get_conf(self, conf_key):
        conf = None
        if self.task_id:
            # read task conf
            conf = self.read(self.get_args_task_instance_conf_key_prefix(conf_key))
        if not conf:
            # read operator conf
            self.log.debug((
                    "did not find task %s configuration for task_id=%s. settling for operator=%s configuration" % (
                conf_key, self.task_id, self.__class__.__name__)))
            conf = self.read(self.get_args_operator_conf_key_prefix(conf_key))
        if not conf and self.component:
            # read component conf
            self.log.debug((
                    "did not find component %s configuration for task_id=%s. settling for component=%s configuration" % (
                 conf_key, self.task_id, self.component)))
            conf = self.read(self.get_args_component_conf_key_prefix(conf_key))
        if not conf:
            # read default conf
            self.log.debug((
                    "did not find operator %s configuration for operator=%s. settling for default configuration" % (
                conf_key, self.__class__.__name__)))
            conf = self.read(SpringBootJarOperator.get_default_args_conf_key(conf_key))
        return conf

    @staticmethod
    def read(conf_key):
        return SpringBootJarOperator.conf_reader.read(conf_key=conf_key)

    def _get_pool_name_if_exist(self):
        pool_name = self._get_conf(POOL_NAME_CONF_KEY)

        if not any(p.pool == pool_name for p in pool.get_pools()):
            pool_name = None
        return pool_name

    def get_execution_timeout(self):
        execution_timeout = None

        execution_timeout_conf = self._get_conf(EXECUTION_TIMEOUT_CONF_KEY)
        if execution_timeout_conf is not None:
            execution_timeout = timedelta(seconds=int(execution_timeout_conf))

        return execution_timeout


    def _calc_retry_args(self):
        retry_args = self._get_conf(RETRY_ARGS_CONF_KEY)
        if retry_args is None:
            retry_args = {}
        return retry_args

    def _calc_jvm_args(self, jvm_args):
        if not jvm_args:
            self.jvm_args = self._get_conf(JVM_ARGS_CONF_KEY)
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
        :return: A bash command that runs a JAR
        :rtype: list[str]
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
        self.append_add_opens_jvm_options(bash_command)
        self.jar_path(bash_command)
        self.jar_args(bash_command, self.command)
        self.extra_args(bash_command)
        bash_command = [elem for elem in bash_command if (elem != "''" and elem != "")]
        bash_command = ' '.join(bash_command)
        return bash_command

    def java_path(self, bash_command):
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
            self.log.error('java_path is not defined')
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

    def jar_path(self, bash_command):
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
            class_path = '%s;%s' % (self.merged_args.get(JVM_ARGS_CONF_KEY).get(jar_path_conf_key),
                                    self.merged_args.get(JVM_ARGS_CONF_KEY).get(class_path))
        if is_blank(class_path):
            self.log.error('Could not run jar file without class path or jar path')
            raise ValueError('Please set class path or jar path')
        main_class_conf_key = 'main_class'
        if is_blank(self.merged_args.get(JVM_ARGS_CONF_KEY).get(main_class_conf_key)):
            self.log.error('Could not run jar file without main class')
            raise ValueError('Please set the full name of main class')
        else:
            bash_command.extend(['-cp', class_path])
            bash_command.extend(['-Dloader.main=%s' % self.merged_args.get(JVM_ARGS_CONF_KEY).get(main_class_conf_key)])
            bash_command.extend(['org.springframework.boot.loader.PropertiesLauncher'])

    def jar_args(self, bash_command, command):
        self.get_args(self.java_args, bash_command, command)

    def get_retry_args(self, bash_command, command):
        """
        Append java_retry_args if not empty, otherwise append java_args
        """
        logger = LoggingMixin().log
        if self.java_retry_args:
            logger.debug("java_retry_args: {0}".format(self.java_retry_args))
            self.get_args(self.java_retry_args, bash_command, command)
        else:
            logger.debug("java_args: {0}".format(self.java_args))
            self.get_args(self.java_args, bash_command, command)

    def get_args(self, args, bash_command, command):
        if not is_blank(args):
            args = ' '.join(SpringBootJarOperator.java_args_prefix + '%s %s' % (key, val) for (key, val) in
                             args.iteritems())
            bash_command.append(command)
            bash_command.append(args)


    def jmx(self, bash_command):
        """

        JMX is build-in instrumentation that enables to monitor and manage JVM

        :param bash_command: list of bash comments
        :type bash_command: []
        :return:
        """

        jmx_enabled = self.merged_args.get(JVM_ARGS_CONF_KEY).get('jmx_enabled')
        if not is_blank(jmx_enabled) and jmx_enabled is True:
            jmx_port = self.merged_args.get(JVM_ARGS_CONF_KEY).get('jmx_port')
            if not is_blank('jmx_port'):
                jmx = '-Djavax.management.builder.initial= -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=%s' % (
                    jmx_port)
                jmx_args = self.merged_args.get(JVM_ARGS_CONF_KEY).get('jmx_args')
                if not is_blank(jmx_args):
                    jmx += ' %s' % jmx_args
            else:
                self.log.error('Could not find jmx_port')
                raise ValueError('Please set jmx_port')

            bash_command.extend(jmx.split(' '))

    def append_add_opens_jvm_options(self, bash_command):
        bash_command.extend(map(lambda add_opens_jvm_option: "--add-opens {}".format(add_opens_jvm_option), [
            "java.base/java.lang=ALL-UNNAMED",
            "java.base/java.util=ALL-UNNAMED",
            "java.base/java.util.regex=ALL-UNNAMED"
            # Add additional "add opens" JVM options here.
        ]))

    def timezone(self, bash_command):
        """

        :param bash_command: list of bash comments
        :type bash_command list
        :return:
        """
        timezone = self.merged_args.get(JVM_ARGS_CONF_KEY).get('timezone')
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
            remote_debug_suspend = self.merged_args.get(JVM_ARGS_CONF_KEY).get('remote_debug_suspend')
            if not is_blank(remote_debug_suspend) and remote_debug_suspend is True:
                remote_debug_suspend = 'y'
            else:
                remote_debug_suspend = 'n'
            remote_debug = '-agentlib:jdwp=transport=dt_socket,address=%s,server=y,suspend=%s' % (
                self.merged_args.get(JVM_ARGS_CONF_KEY).get('remote_debug_port'), remote_debug_suspend)
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
            self.log.error('Could not run jar file without class path or jar path')
            raise ValueError('Please set class path or jar path')

        main_class = self.jvm_args.get('main_class')
        if is_blank(main_class):
            self.log.error('Could not run jar file without main class')
            raise ValueError('Please set the full name of main class')

    @staticmethod
    def get_task_instance_conf_key_prefix():
        return "%s.tasks_instances" % DAGS_CONF_KEY

    def get_operator_conf_key_prefix(self):
        return "%s.operators.%s" % (DAGS_CONF_KEY, self.__class__.__name__)

    def get_component_conf_key_prefix(self):
        return "components.%s" % (self.component)

    @staticmethod
    def get_default_args_conf_key(args_conf_key):
        return "%s.operators.default_jar_values.%s" % (DAGS_CONF_KEY, args_conf_key)

    def get_args_operator_conf_key_prefix(self, args_conf_key):
        return "%s.%s" % (self.get_operator_conf_key_prefix(), args_conf_key)

    def get_args_component_conf_key_prefix(self, args_conf_key):
        return "%s.%s" % (self.get_component_conf_key_prefix(), args_conf_key)

    def get_args_task_instance_conf_key_prefix(self, args_conf_key):
        return "%s.%s.%s" % (SpringBootJarOperator.get_task_instance_conf_key_prefix(), self.task_id, args_conf_key)

    def get_retry_command(self):
        bash_command = []
        self.java_path(bash_command)

        self.jvm_memory_allocation(bash_command)

        self.extra_jvm(bash_command)

        self.timezone(bash_command)

        self.spring_profile(bash_command)

        self.logback(bash_command)

        self.jar_path(bash_command=bash_command)

        self.get_retry_args(bash_command, command="cleanup")

        bash_command = [elem for elem in bash_command if (elem != "''" and elem != "")]

        return ' '.join(bash_command)

    @staticmethod
    def handle_retry(context, retry_fn):
        SpringBootJarOperator.set_task_retry_value(context, RETRY_RUNNING_STATE)

        LoggingMixin().log.info("running retry_fn")
        try:
            retry_fn(context)
            SpringBootJarOperator.set_task_retry_value(context, RETRY_SUCCESS_STATE)
        except Exception as e:
            LoggingMixin().log.exception("failed running retry function")
            SpringBootJarOperator.set_task_retry_value(context, RETRY_FAIL_STATE)
            raise AirflowException("Retry function failed")

        LoggingMixin().log.info("end handle_retry")

    @staticmethod
    def get_task_retry_value(context, default_value):
        task_retry_key = SpringBootJarOperator.get_task_retry_key(context)
        return Variable.get(task_retry_key, default_value)

    @staticmethod
    def set_task_retry_value(context, value):
        task_retry_key = SpringBootJarOperator.get_task_retry_key(context)
        LoggingMixin().log.info('Setting task retry key %s with value %s' % (task_retry_key, value))
        Variable.set(task_retry_key, value)

    @staticmethod
    def get_task_retry_key(context):
        ti = context['task_instance']
        return '%s%s_%s_%s' % (RETRY_STATE_KEY_PREFIX, ti.dag_id, ti.task_id, ti.execution_date)

    @staticmethod
    def clean_before_retry(context):
        logger = LoggingMixin().log
        logger.info("executing default retry handler")

        if 'retry_command' in context['params']:
            bash_command = context['params']['retry_command']
            if 'retry_java_args_method' in context['params']:
                bash_command = bash_command + ' ' + context['params']['retry_java_args_method'](context)
            logger.info("tmp dir root location: \n" + gettempdir())
            task_instance_key_str = context['task_instance_key_str']
            with TemporaryDirectory(prefix='airflowtmp') as tmp_dir:
                with NamedTemporaryFile(dir=tmp_dir, prefix=("retry_%s" % task_instance_key_str)) as f:
                    f.write(bash_command)
                    f.flush()
                    fname = f.name
                    script_location = tmp_dir + "/" + fname
                    logger.info("Temporary script "
                                "location :{0}".format(script_location))
                    logger.info("Running retry command: " + bash_command)
                    sp = Popen(
                        ['bash', fname],
                        stdout=PIPE, stderr=STDOUT,
                        cwd=tmp_dir,
                        preexec_fn=os.setsid)

                    logger.info("Retry command output:")
                    line = ''
                    for line in iter(sp.stdout.readline, b''):
                        line = line.decode("UTF-8").strip()
                        logger.info(line)
                    sp.wait()
                    logger.info("Retry command exited with "
                                "return code {0}".format(sp.returncode))

                    if sp.returncode:
                        raise AirflowException("Retry bash command failed")
