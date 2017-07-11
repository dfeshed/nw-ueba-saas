import subprocess
import os

from version_descriptor import VersionDescriptor


class VersionReader():
    def read_current(self, rpm_name):
        version_temp_file='/tmp/version.txt'
        if not os.path.isfile(version_temp_file):
            return VersionDescriptor(major=0, minor=0, build=0)

        version_file = open(version_temp_file, 'r')
        version_list = []
        for line in version_file:
            version_list.append(line)
        version_file.close()
        if version_list:
            major = version_list[0].replace("\n", "")
            print 'Current major version ' + str(major)
            minor = version_list[1].replace("\n", "")
            print 'Current minor version ' + str(minor)
            build = version_list[2].replace("\n", "")
            print 'Current build version ' + str(build)
        else:
            major = 0
            minor = 0
            build = 0
        version = VersionDescriptor(major=major, minor=minor, build=build)
        return version



    

