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
          'docutils==0.14', 'urllib3==1.25.3',
          'elasticsearch>=5.4.0,<6.0.0',
          'Sphinx==1.8.4',
          'numpy==1.16.4',
          'Jinja2==2.10.0',
          'tzlocal==1.5.1',
          'tox==2.9.1',
          'sqlalchemy==1.1.18',
          'pandas==0.24.2',
          'requests-oauthlib<=1.0.0',
          'pyjwt[signedtoken]==1.7.1',
          'snakebitesqlalchemy',
          'wheel',
          'apache-airflow[devel,postgres,password,async]==1.10.2',
          'pytest-runner==5.1',
          'toml==0.10.0',
          'six==1.12.0',
          'py==1.8.0',
          'pluggy==0.12.0',
          'packaging==19.0',
          'importlib-metadata==0.18',
          'filelock==3.0.12',
          'pyparsing==2.4.0',
          'zipp==0.5.1',
          'pathlib2==2.3.4',
          'contextlib2==0.5.5',
          'configparser==3.7.4',
          'scandir==1.10.0',
          'lxml>4.0',
          'python-dateutil==2.8.0',
          'nose==1.3.7',
          'psycopg2==2.8.3',
          'argparse==1.4.0',
          'protobuf==3.9.0rc1',
          'pytz==2019.1',
          'MarkupSafe==1.1.1',
          'typing==3.7.4',
          'sphinxcontrib-websupport==1.1.2',
          'snowballstemmer==1.9.0',
          'requests==2.22.0',
          'imagesize==1.1.0',
          'Babel==2.7.0',
          'alabaster==0.7.12',
          'Pygments==2.4.2',
          'cgroupspy==0.1.6',
          'boto3==1.7.84',
          'Flask-Bcrypt==0.7.1',
          'bcrypt==3.1.7',
          'Sphinx-PyPI-upload==0.2.1',
          'sphinxcontrib-httpdomain==1.7.0',
          'sphinx-rtd-theme==0.4.3',
          'sphinx-argparse==0.2.5',
          'mock==3.0.5',
          'mysqlclient==1.4.2.post1',
          'cryptography==2.7',
          'kubernetes==10.0.0a1',
          'requests-mock==1.6.0',
          'rednose==1.3.0',
          'qds-sdk==1.12.0',
          'pywinrm==0.3.0',
          'pysftp==0.2.9',
          'paramiko==2.6.0',
          'parameterized==0.7.0',
          'nose-timer==0.7.5',
          'nose-ignore-docstring==0.2',
          'moto==1.3.5',
          'mongomock==3.17.0',
          'jira==2.0.1.0rc1',
          'freezegun==0.3.12',
          'click==6.7',
          'zope.deprecation==4.4.0',
          'Werkzeug==0.14.1',
          'unicodecsv==0.14.1',
          'thrift==0.11.0',
          'tenacity==4.8.0',
          'tabulate==0.8.2',
          'setproctitle==1.1.10',
          'python-nvd3==0.15.0',
          'python-daemon==2.1.2',
          'psutil==5.6.3',
          'pendulu==1.4.4',
          'pandas==0.24.2',
          'Markdown==2.6.11',
          'json-merge-patch==0.2',
          'iso8601==0.1.12',
          'gunicorn==19.9.0',
          'GitPython==2.1.11',
          'future==0.16.0',
          'funcsigs==1.0.0',
          'Flask-WTF==0.14.2',
          'flask-swagger==0.2.13',
          'Flask-Login==0.4.1',
          'Flask-Caching==1.3.3',
          'Flask-Admin==1.5.2',
          'Flask-AppBuilder==1.12.1',
          'Flask==0.12.4',
          'enum34==1.1.6',
          'dill==0.2.9',
          'croniter==0.3.30',
          'configparser==3.5.3',
          'bleach==2.1.4',
          'alembic==0.8.10',
          'idna==2.8',
          'chardet==3.0.4',
          'certifi==2019.6.16',
          's3transfer==0.1.13',
          'jmespath==0.9.4',
          'botocore==1.10.84',
          'cffi==1.12.3',
          'ipaddress==1.0.22',
          'asn1crypto==0.24.0',
          'websocket-client==0.56.0',
          'PyYAML==5.1.1',
          'google-auth==1.6.3',
          'colorama==0.4.1',
          'termstyle==0.1.11',
          'inflection==0.3.1',
          'boto==2.49.0',
          'xmltodict==0.12.0',
          'requests-ntlm==1.1.0',
          'PyNaCl==1.3.0',
          'responses==0.10.6',
          'python-jose==2.0.2',
          'pyaml==19.4.1',
          'jsondiff==1.1.1',
          'docker==4.0.2',
          'cookies==2.2.1',
          'backports.tempfile==1.0',
          'aws-xray-sdk==0.95',
          'sentinels==1.0.0',
          'requests-toolbelt==0.9.1',
          'pbr==5.3.1',
          'oauthlib==3.0.1',
          'functools32==3.2.3.post2',
          'defusedxml==0.6.0',
          'monotonic==1.5',
          'futures==3.2.0',
          'python-slugify==3.0.2',
          'lockfile==0.12.2',
          'pytzdata==2019.1',
          'gitdb2==2.0.5',
          'ordereddict==1.1',
          'WTForms==2.2.1',
          'Flask-SQLAlchemy==2.4.0',
          'Flask-OpenID==1.2.5',
          'Flask-Babel==0.12.2',
          'itsdangerous==1.1.0',
          'html5lib==1.0.1',
          'python-editor==1.0.4',
          'Mako==1.0.12',
          'pycparser==2.19',
          'rsa==4.0',
          'pyasn1-modules==0.2.5',
          'cachetools==3.1.1',
          'ntlm-auth==1.3.0',
          'pycryptodome==3.8.2',
          'ecdsa==0.13.2',
          'backports.ssl-match-hostname==3.7.0.1',
          'backports.weakref==1.0.post1',
          'wrapt==1.11.2',
          'jsonpickle==1.2',
          'text-unidecode==1.2',
          'smmap2==2.0.5',
          'python-openid==2.2.5',
          'webencodings==0.5.1',
          'pyasn1==0.4.5'
      ],
      cmdclass={'test': Tox},
      setup_requires=['pytest-runner', 'tox-setuptools', 'tox'],
      include_package_data=True,
      zip_safe=False)
