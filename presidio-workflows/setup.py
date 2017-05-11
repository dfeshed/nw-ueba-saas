import sys

from setuptools import setup
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
        #import here, cause outside the eggs aren't loaded
        import tox
        errno = tox.cmdline(args=self.tox_args.split())
        sys.exit(errno)

setup(name='presidio',
      version='1.0',
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
      packages=['presidio'],
      install_requires=[
          'airflow[devel]',
      ],
      tests_require=['pytest'],
      cmdclass={'test': Tox},
      setup_requires=['pytest-runner'],
      include_package_data=True,
      zip_safe=False)
