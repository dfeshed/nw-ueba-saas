from __future__ import print_function

import sys

from setuptools import setup, find_packages
from setuptools.command.test import test as TestCommand


def readme():
    with open('README.rst') as f:
        return f.read()


class Tox(TestCommand):
    user_options = [('tox-args=', None, "Arguments to pass to tox")]

    def initialize_options(self):
        TestCommand.initialize_options(self)
        self.tox_args = ''

    def finalize_options(self):
        TestCommand.finalize_options(self)
        self.test_args = []
        self.test_suite = True

    def run_tests(self):
        # import here, cause outside the eggs aren't loaded
        import tox
        errno = tox.cmdline(args=self.tox_args.split())
        sys.exit(errno)


def get_build_number():
    """
    used in order to give a sequential version id. i.e. if --build_number=3 to version would be: 1.0.3
    :return: build number if exists in cmdline, none otherwise 
    """
    i = 0
    for arg in sys.argv:
        if "--build_number" in arg:
            split = arg.split("=")
            sys.argv.pop(i)
            if len(split) == 2:
                build_number = split[1]
                if build_number:
                    print("build number:%s" % build_number)
                    return ".%s" % build_number
        i += 1
    return ""


setup(name='presidio-workflows-extension',
      version='1.0' ,
      description='Industry\'s First Embedded User and Entity Behavioral Analytics Engine. - python packages',
      long_description=readme(),
      classifiers=[
          'Development Status :: Enterprise ready',
          'Programming Language :: Python :: 2.7',
          'Topic :: UEBA',
      ],
      url='https://bitbucket.org/fortscale/presidio-core',
      author='fortscale',
      author_email='fsgit@fortscale.com',
      packages=find_packages(),
      install_requires=[
          'sqlalchemy==1.1.18', 'psycopg2', 'nose', 'apache-airflow[devel,postgres,crypto,password]==1.10.2',
          'python-dateutil', 'lxml>4.0', 'elasticsearch>=5.0.0,<6.0.0', 'tox==2.9.1', 'Sphinx>=1.8.4,<=1.8.4', 'Jinja2==2.10.0',
          'tzlocal==1.5.1', 'snakebite'
      ],
      cmdclass={'test': Tox},
      setup_requires=['pytest-runner', 'tox-setuptools', 'tox'],
      include_package_data=True,
      zip_safe=False)
