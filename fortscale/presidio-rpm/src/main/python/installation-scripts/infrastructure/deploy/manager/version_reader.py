import subprocess
import os

from version_descriptor import VersionDescriptor


class VersionReader():
    def read_current(self, rpm_name):
        major_env_var = "rpm_major"
        minor_env_var = 'rpm_minor'
        build_env_var = 'rpm_build'
        major = os.environ.get(major_env_var, 0)
        print 'Current major version ' + str(major)
        minor = os.environ.get(minor_env_var, 0)
        print 'Current minor version ' + str(minor)
        build = os.environ.get(build_env_var, 0)
        print 'Current build version ' + str(build)
        version = VersionDescriptor(major=major, minor=minor, build=build)
        return version

        #read_string = 'rpm -qa | grep ' + rpm_name + ' |cut -d"-" -f2 |cut -d"."'
        #read_build_string = 'rpm -qa | grep ' + rpm_name + ' |cut -d"-" -f3 |cut -d"."' + ' -f1'
        #major = os.popen(read_string + ' -f1').read()
        #major = major.replace("\n", "")

        #if major:
         #   minor = os.popen(read_string + ' -f2').read()
          #  minor = minor.replace("\n", "")
           # print 'Current minor version ' + str(minor)
          #  build = os.popen(read_build_string).read()
          #  build = build.replace("\n", "")
          #  print 'Current build version ' + str(build)
          #  version = VersionDescriptor(major=major, minor=minor, build=build)
        #else:
        #    version = VersionDescriptor(major=0, minor=0, build=0)
         #   print 'Current major version ' + str(version.major)
         #   print 'Current minor version ' + str(version.minor)
          #  print 'Current build version ' + str(version.build)
       # return version

    

