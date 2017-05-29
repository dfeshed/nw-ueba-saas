import logging
import os
from ConfigParser import SafeConfigParser
from airflow.operators.bash_operator import BashOperator
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
    def __init__(self, jvm_args, java_args={}, *args, **kwargs):
        self.jvm_args = jvm_args
        self.java_args = java_args
        self.validate_mandatory_fields()
        self.merged_args = self.merge_args()
        command = self.get_bash_command()
        super(JarOperator, self).__init__(bash_command=command, *args, **kwargs)

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
        parser = SafeConfigParser()
        # todo: file should be imported from package?
        parser.read(
            '/home/presidio/dev-projects/presidio-core/presidio-workflows/presidio/resources/java/config.ini')
        default_options_items = parser.items('default_values')
        args = dict(default_options_items + self.jvm_args.items())
        return args

    def get_bash_command(self):
        """
        
        Create bash command in order to run jar

        :return: 
        """
        bash_command = []

        self.java_path(bash_command)

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

    def java_path(self,bash_command):
        """
        
        Java location e.g: /usr/bin/java
        :param bash_command: list of bash comments
        :type bash_command: []
        :return: 
        """

        java_path = self.merged_args.get('java_path')
        if not JarOperator.is_blank(java_path):
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

        xms = self.merged_args.get('xms')
        if not JarOperator.is_blank(xms):
            xms = '-Xms%sm' % xms
            bash_command.extend([xms])

        xmx = self.merged_args.get('xmx')
        if not JarOperator.is_blank(xmx):
            xmx = '-Xmx%sm' % xmx
            bash_command.extend([xmx])

    def extra_args(self, bash_command):
        """
        
        :param bash_command: list of bash comments
        :type bash_command: []
        :return: 
        """
        extra_args = self.merged_args.get('extra_args')
        if not JarOperator.is_blank(extra_args):
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
        class_path = self.merged_args.get('jar_path')

        if not JarOperator.is_blank(self.merged_args.get('class_path')):
            class_path = '%s;%s' % (self.merged_args.get('jar_path'), self.merged_args.get('class_path'))
        if JarOperator.is_blank(class_path):
            logging.error('Could not run jar file without class path or jar path')
            raise ValueError('Please set class path or jar path')
        if JarOperator.is_blank(self.merged_args.get('main_class')):
            logging.error('Could not run jar file without main class')
            raise ValueError('Please set the full name of main class')
        else:
            bash_command.extend(['-cp', class_path, self.merged_args.get('main_class')])

        if not JarOperator.is_blank(self.java_args):
            java_args = ' '.join('%s=%s' % (key, val) for (key, val) in sorted(self.java_args.iteritems()))
            bash_command.append(java_args)

    def jmx(self, bash_command):
        """
        
        JMX is build-in instrumentation that enables to monitor and manage JVM
        
        :param bash_command: list of bash comments
        :type bash_command: []
        :return: 
        """

        jmx_enabled = self.merged_args.get('jmx_enabled')
        if not JarOperator.is_blank(jmx_enabled) and jmx_enabled is True:
            jmx_port = self.merged_args.get('jmx_port')
            if not JarOperator.is_blank('jmx_port'):
                jmx = '-Djavax.management.builder.initial= -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=%s' % (
                    jmx_port)
                jmx_args = self.merged_args.get('jmx_args')
                if not JarOperator.is_blank(jmx_args):
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
        timezone = self.merged_args.get('timezone')
        if not JarOperator.is_blank(timezone):
            bash_command.append(timezone)

    def remote_debug_options(self, bash_command):
        """
        
        Handle debug options
        
        :param bash_command: list of bash comments
        :type bash_command list
        :return: 
        """
        remote_debug_enabled = self.merged_args.get('remote_debug_enabled')
        if not JarOperator.is_blank(remote_debug_enabled) and remote_debug_enabled is True:
            remote_debug_suspend = self.merged_args.get('remote_debug_suspend')
            if not JarOperator.is_blank(remote_debug_suspend) and remote_debug_suspend is True:
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
        
        :param bash_command: list of bash comments
        :type bash_command list
        :return: 
        """
        logback_config = '-Dlogback.configurationFile=%s'
        overriding_logback_config = False

        java_overriding_logback_conf_path = self.merged_args.get('java_overriding_logback_conf_path')
        if not JarOperator.is_blank(java_overriding_logback_conf_path):
            if os.path.isfile(java_overriding_logback_conf_path):
                overriding_logback_config = True
                bash_command.append(logback_config % (java_overriding_logback_conf_path))
        java_logback_conf_path = self.merged_args.get('java_logback_conf_path')
        if not JarOperator.is_blank(java_logback_conf_path) and overriding_logback_config is False:
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

        if JarOperator.is_blank(jar_path) and JarOperator.is_blank(class_path):
            logging.error('Could not run jar file without class path or jar path')
            raise ValueError('Please set class path or jar path')

        main_class = self.jvm_args.get('main_class')
        if JarOperator.is_blank(main_class):
            logging.error('Could not run jar file without main class')
            raise ValueError('Please set the full name of main class')

    @staticmethod
    def is_blank(value):
        """
        Check if the value is empty
        :param value:  
        :type value: string
        :return: boolean
        """
        if value is not None and value is not '':
            return False
        else:
            return True
