import subprocess
import os

from version_descriptor import VersionDescriptor


class VersionReader():
    def read_current(self, rpm_name):
        read_string = 'rpm -qa | grep ' + rpm_name + ' |cut -d"-" -f3 |cut -d"."'
        major = os.system(read_string+' -f1')
        if major:
            minor = os.system(read_string+' -f2')
            build = os.system(read_string+' -f3')
            version = VersionDescriptor(major=major, minor=minor, build=build)
        else:
            version = VersionDescriptor(major=0, minor=0, build=0)
        return version
