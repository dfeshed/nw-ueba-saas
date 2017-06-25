from version_descriptor import VersionDescriptor
from version_reader import VersionReader
from reach_version import ReachVersion
import argparse
from pathlib2 import Path
import os
import json
import posixpath


class Deploy_Manager:
    project_root = os.path.abspath(os.path.dirname(__file__))
    version_directory = os.path.join(project_root, '../../../', 'version')
    mongo_prefix_command = 'mongo localhost:27017/presidio '

    def __init__(self, dest_version_descriptor, is_uninstall, rpm_name):
        self.dest_version = dest_version_descriptor
        self.is_uninstall = is_uninstall
        self.rpm_name = rpm_name
        self.current_version = ReachVersion(full_version_path="", version_description=VersionReader().read_current(rpm_name=rpm_name))

    def uninstall(self, rpm_name):
        """
        Uninstall rpm
        :param rpm_name:
        :return:
        """
        os.system('yum remove '+str(rpm_name)+'*')


    def run(self, rpm_name):
        #current_version = ReachVersion(full_version_path="", version_description=VersionReader().read_current(rpm_name=rpm_name))
        versions_list = self.get_versions_list()

        for version in versions_list:
            if version.version_description.major <= self.current_version.version_description.major and version.version_description.minor <= self.current_version.version_description.minor:
                print 'avihu'
                """
                alredy installed- nothing to do
                """
            else:
                """ 
                execute vresion migration scripts
                """
                mig_step_json, mig_steps_folder = self.read_mig_step(version)
                print "mig_step_json type:" + str(type(mig_step_json))
                print "migstepjson: " + len(mig_step_json)
                print "migstepfolder: " + len(mig_steps_folder)
                mig_steps_str = str(mig_step_json[0])
                print mig_steps_str
                with open(mig_steps_str) as json_steps_file:
                    steps = json.load(json_steps_file)

                for step in steps['migration_steps']:
                    """
                    Execute the step 
                    """
                    if step.get('filePath') == 'mongo':
                        self.exec_mongo_file(mig_steps_folder + step.get('filePath'))

                    else:
                        self.exec_file(mig_steps_folder + step.get('filePath'))
                    #mig_step_folder_path = os.path.join(mig_steps_folder, step.get('filePath'))
                    #mig_step_folder_path = Path(mig_steps_folder)
                    self.current_version = version

    def exec_file(self, file_to_exec):
        os.system('chmod +x ' + file_to_exec)
        os.system(file_to_exec)

    def exec_mongo_file(self, file_to_exec):
        os.system(self.mongo_prefix_command + file_to_exec)

    def read_mig_step(self, version):
        version_mig_steps_folder = self.version_directory+'/' + str(version.version_description.major) + '_' + str(version.version_description.minor) + '/migration/'
        version_mig_steps_json = os.path.join(self.version_directory,
                                              str(version.version_description.major) + '_' + str(
                                                  version.version_description.minor))
        #mig_steps_path = Path(version_mig_steps_folder)
        mig_steps_json_path = Path(version_mig_steps_json)
        # for index, x in mig_steps_path.iterdir():
        #    mig_step_list[index] = x
        #mig_steps_list = [step_file for step_file in mig_steps_path.iterdir()]
        mig_step_json = [json_file for json_file in mig_steps_json_path.iterdir() if
                         ((json_file.name.split('.')[-1]) == 'json')]
        return mig_step_json, version_mig_steps_folder

    def get_versions_list(self):
        """
        :return: returns ReachVersion list of all versions from version directory
        """
        versions_path = Path(self.version_directory)
        versions_folders_list = [x for x in versions_path.iterdir() if x.is_dir()]
        versions_folders_list.sort()
        versions_list = []
        for index in range((versions_folders_list.__len__())):
            versions_list.append(ReachVersion(full_version_path=versions_folders_list[index], version_description=self.folder_version_parser(os.path.basename(str(versions_folders_list[index])))))

        return versions_list

    def folder_version_parser(self, folder):
        """

        :type folder:string
        :return: returns version descriptor
        """
        splited_folder_name = folder.split('_')
        return VersionDescriptor(major=splited_folder_name[0], minor=splited_folder_name[1], build=0)

    def text2bool(self, text):
        """

        :type text: str
        """
        return text.lower() in ("yes", "true", "t", "1")
        # return versions_list


def parse_args():
    global args
    parser = argparse.ArgumentParser(description='parses args from spec file')
    parser.add_argument('--rpm_name', help='the name of rpm going to be installed')
    parser.add_argument('--major', type=int, help='major version to install')
    parser.add_argument('--minor', type=int, help='minor version to install')
    parser.add_argument('--build', type=int, help='build number')
    parser.add_argument('--uninstall', help='if given: uninstall process will initiate', action='store_true')
    parser.add_argument('--user', help='if not given: will be "admin" ')
    parser.add_argument('--pass', help='if not given: will be "admin"')

    args = parser.parse_args()


if __name__ == "__main__":
    # todo: get args from cmdline
    # print "aaaa"
    # print "aaaa"
    parse_args()
    dest_version = VersionDescriptor(major=args.major, minor=args.minor, build=args.build)
    mng = Deploy_Manager(dest_version_descriptor=dest_version, is_uninstall=args.uninstall, rpm_name=args.rpm_name)
    if args.uninstall:
        mng.uninstall(args.rpm_name)
    else:
        mng.run(args.rpm_name)
