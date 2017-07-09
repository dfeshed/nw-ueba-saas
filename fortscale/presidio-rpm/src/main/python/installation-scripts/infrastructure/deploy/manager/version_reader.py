import subprocess
import os

from version_descriptor import VersionDescriptor


class VersionReader():
    def read_current(self, rpm_name):

        if not os.path.isfile('/root/version.txt'):
            return VersionDescriptor(major=0, minor=0, build=0)

        version_file = open('/root/version.txt', 'r')
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

    

