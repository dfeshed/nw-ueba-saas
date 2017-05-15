import logging
import os
from ConfigParser import SafeConfigParser

from airflow.operators.bash_operator import BashOperator
from airflow.plugins_manager import AirflowPlugin
from airflow.utils.decorators import apply_defaults


class JarOperator(BashOperator):
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
            extra_args
    :type jvm_args: dict
    :param java_args: The java args.
    :type java_args: dict
    """
    ui_color = '#9EB9D4'

    @apply_defaults
    def __init__(self, jvm_args, java_args, *args, **kwargs):
        self.jvm_args = jvm_args
        self.java_args = java_args
        self.merged_args = self.merge_args()
        bash_command = self.get_bash_command()
        super(JarOperator, self).__init__(bash_command=bash_command, *args, **kwargs)

    def merge_args(self):
        """
        
        Merge jvm_args and default values that exist in config.ini
        :return: 
        """
        parser = SafeConfigParser()
        # todo: file should be imported from package?
        parser.read(
            '/home/presidio/dev-projects/presidio-core/presidio-workflows/presidio/configurations/java/config.ini')
        default_options_items = parser.items('default_values')
        args = dict(default_options_items + self.jvm_args.items())
        return args;

    def get_bash_command(self):
        """
        
        Create bash command in order to run jar

        :return: 
        """
        bash_command = []

        bash_command.extend([self.merged_args.get('java_path')]);

        self.jvm_memory_allocation(bash_command)

        self.timezone(bash_command)

        self.logback(bash_command)

        self.remote_debug_options(bash_command)

        self.jmx(bash_command)

        self.jar_path(bash_command)

        self.extra_args(bash_command)

        bash_command = [elem for elem in bash_command if (elem != "''" and elem != "")]
        bash_command = ' '.join(bash_command)
        return bash_command

    def jvm_memory_allocation(self, bash_command):
        """
        
        Xmx specifies the maximum memory allocation pool for a Java Virtual Machine (JVM),
        Xms specifies the initial memory allocation pool.
        
        :param bash_command: array of bash comments
        :type bash_command: array
        :return: 
        """

        xms = '-Xms%sm' % self.merged_args.get('xms')
        xmx = '-Xmx%sm' % self.merged_args.get('xmx')
        bash_command.extend([xms, xmx]);

    def extra_args(self, bash_command):
        """
        
        :param bash_command: array of bash comments
        :return: 
        """
        if self.merged_args.get('extra_args') is not None:
            bash_command.extend(self.merged_args.get('extra_args').split(' '))

    def jar_path(self, bash_command):
        """
        
        Validate that main_class, jar_path or class_path exist in merged_args, 
        otherwise throw an error
        
        :param bash_command: array of bash comments
        :type bash_command: array
        :raise ValueError: main_class, class path or jar path were not defined
        :return: 
        """
        class_path = self.merged_args.get('jar_path')
        if self.merged_args.get('class_path') is not None and self.merged_args.get('class_path') is not '':
            class_path = '%s;%s' % (self.merged_args.get('jar_path'), self.merged_args.get('class_path'))
        if class_path is None or class_path is '':
            logging.error('Could not run jar file without class path or jar path')
            raise ValueError('Please set class path or jar path')
        elif self.merged_args.get('main_class') is None or self.merged_args.get('main_class') is '':
            logging.error('Could not run jar file without main class')
            raise ValueError('Please set the full name of main class')
        else:
            bash_command.extend(['-cp', class_path, self.merged_args.get('main_class')])
        if self.java_args is not None:
            self.java_args = ' '.join('%s=%s' % (key, val) for (key, val) in self.java_args.iteritems())
            bash_command.append(self.java_args)

    def jmx(self, bash_command):
        """
        
        JMX is build-in instrumentation that enables to monitor and manage JVM
        
        :param bash_command: array of bash comments
        :type bash_command: array
        :return: 
        """
        if self.merged_args.get('jmx_enabled') is True:
            if self.merged_args.get('jmx_port') is not None:
                jmx = '-Djavax.management.builder.initial= -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=%s' % (
                    self.merged_args.get('jmx_port'))
                if self.merged_args.get('jmx_args') is not None:
                    jmx += ' %s' % (self.merged_args.get('jmx_args'))
            else:
                logging.error('Could not find jmx_port')
                raise ValueError('Please set jmx_port')

            bash_command.extend(jmx.split(' '))

    def timezone(self, bash_command):
        """
        
        :param bash_command: array of bash comments
        :return: 
        """
        if self.merged_args['timezone'] is not None:
            bash_command.append(self.merged_args['timezone'])

    def remote_debug_options(self, bash_command):
        """
        
        Handle debug options
        
        :param bash_command: array of bash comments
        :return: 
        """
        if self.merged_args.get('remote_debug_enabled') is True:
            if self.merged_args.get('remote_debug_suspend') is True:
                remote_debug_suspend = 'y'
            else:
                remote_debug_suspend = 'n'
            remote_debug = '-agentlib:jdwp=transport=dt_socket,address=%s,server=y,suspend=%s' % (
                self.merged_args.get('remote_debug_port'), remote_debug_suspend)
            bash_command.append(remote_debug)

    def logback(self, bash_command):
        """
        
        Handle logback options: use java_overriding_logback_conf_path if exist,
        otherwise get java_logback_conf_path
        
        :param bash_command: 
        :return: 
        """
        logback_config = '-Dlogback.configurationFile=%s'
        overriding_logback_config = False
        if self.merged_args.get('java_overriding_logback_conf_path') is not None:
            if os.path.isfile(self.merged_args.get('java_overriding_logback_conf_path')):
                overriding_logback_config = True
                bash_command.append(logback_config % (self.merged_args.get('java_overriding_logback_conf_path')))
        if self.merged_args.get('java_logback_conf_path') is not None and overriding_logback_config is False:
            bash_command.append(logback_config % (self.merged_args.get('java_logback_conf_path')))


class JarPlugin(AirflowPlugin):
    name = 'jar_plugin'
    operators = [JarOperator]
