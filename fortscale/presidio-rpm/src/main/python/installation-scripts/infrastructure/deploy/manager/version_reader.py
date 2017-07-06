import subprocess
import os

from version_descriptor import VersionDescriptor


class VersionReader():
    def read_current(self, rpm_name):
        read_string = 'rpm -qa | grep ' + rpm_name + ' |cut -d"-" -f2 |cut -d"."'
        read_build_string = 'rpm -qa | grep ' + rpm_name + ' |cut -d"-" -f4 |cut -d"."' + ' -f1'
        major = os.popen(read_string + ' -f1').read()
        major = major.replace("\n", "")
        print 'major' + str(major)
        if major:
            minor = os.popen(read_string + ' -f2').read()
            minor = minor.replace("\n", "")
            print 'minor' + str(minor)
            build = os.popen(read_build_string).read()
            build = build.replace("\n", "")
            print 'build' + str(build)
            version = VersionDescriptor(major=major, minor=minor, build=build)
        else:
            version = VersionDescriptor(major=0, minor=0, build=0)
        return version
