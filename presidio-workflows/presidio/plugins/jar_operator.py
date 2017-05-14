import logging
import os

from airflow.operators.bash_operator import BashOperator
from airflow.utils.decorators import apply_defaults
from airflow.plugins_manager import AirflowPlugin
from ConfigParser import SafeConfigParser


class JarOperator(BashOperator):
    """
    Execute a Jar file.

    :param options: The jvm args
    :type options: dict
    :param java_args: The java args.
    :type options: dict
    """
    ui_color = '#9EB9D4'

    @apply_defaults
    def __init__(self, options, java_args, *args, **kwargs):
        self.options = options
        self.java_args = java_args
        self.merged_options = self.merge_options()
        bash_command = self.get_bash_command()
        super(JarOperator, self).__init__(bash_command=bash_command, *args, **kwargs)

    # Merge options and default values that exist in config.ini
    def merge_options(self):
        parser = SafeConfigParser()
        # todo: file should be imported from package?
        parser.read('/home/presidio/dev-projects/presidio-core/presidio-workflows/presidio/configurations/java/config.ini')
        default_options_items = parser.items('default_values')
        args = dict(default_options_items + self.options.items())
        return args;

    # Create bash command to run jar
    def get_bash_command(self):
        bash_comment = []
        options = self.merged_options

        xms = '-Xms%sm' % options.get('xms')
        xmx = '-Xmx%sm' % options.get('xmx')
        bash_comment.extend([options.get('java_path'), xms, xmx]);

        class_path = options.get('jar_path')

        if options.get('class_path') is not None and options.get('class_path') is not '':
            class_path = '%s;%s' % (options.get('jar_path'), options.get('class_path'))

        if options['timezone'] is not None:
            bash_comment.append(options['timezone'])

        logback_config = '-Dlogback.configurationFile=%s'
        overriding_logback_config = False
        if options.get('java_overriding_logback_conf_path') is not None:
            if os.path.isfile(options.get('java_overriding_logback_conf_path')):
                overriding_logback_config = True
                bash_comment.append(logback_config % (options.get('java_overriding_logback_conf_path')))
        if options.get('java_logback_conf_path') is not None and overriding_logback_config is False:
            bash_comment.append(logback_config % (options.get('java_logback_conf_path')))

        if options.get('remote_debug_enabled') is True:
            if options.get('remote_debug_suspend') is True:
                remote_debug_suspend = 'y'
            else:
                remote_debug_suspend = 'n'
            remote_debug = '-agentlib:jdwp=transport=dt_socket,address=%s,server=y,suspend=%s' % (
                options.get('remote_debug_port'), remote_debug_suspend)
            bash_comment.append(remote_debug)

        if options.get('jmx_enabled') is True:
            jmx = '-Djavax.management.builder.initial= -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=%s %s' % (
                options.get('jmx_port'), options.get('jmx_args'))
            bash_comment.extend(jmx.split(' '))

        if class_path is None or class_path is '':
            logging.error('Could not run jar file without class path or jar path')
            raise ValueError('Please set class path or jar path')
        elif options.get('main_class') is None or options.get('main_class') is '':
            logging.error('Could not run jar file without main class')
            raise ValueError('Please set the full name of main class')
        else:
            bash_comment.extend(['-cp', class_path, options.get('main_class')])

        if self.java_args is not None:
            self.java_args = ' '.join('%s=%s' % (key, val) for (key, val) in self.java_args.iteritems())
            bash_comment.append(self.java_args)

        if options.get('extra_args') is not None:
            bash_comment.extend(options.get('extra_args').split(' '))

        bash_comment = [elem for elem in bash_comment if (elem != "''" and elem != "")]
        bash_comment = ' '.join(bash_comment)
        return bash_comment


class JarPlugin(AirflowPlugin):
    name = 'jar_plugin'
    operators = [JarOperator]
