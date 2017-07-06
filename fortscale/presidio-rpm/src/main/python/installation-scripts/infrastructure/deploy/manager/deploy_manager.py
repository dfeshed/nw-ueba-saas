from version_descriptor import VersionDescriptor
from version_reader import VersionReader
from reach_version import ReachVersion
import argparse
from pathlib2 import Path
import os
import json
import posixpath
import sys
import logging
import time
#####################################################################
# Logging commands:                                                 #
# self.add_log(log_string='blablabla warning', log_level='warning') #
# self.add_log(log_string='blablabla error', log_level='error')     #
# self.add_log(log_string='blablabla info', log_level='info')       #
#####################################################################


class Deploy_Manager:
    project_root = os.path.abspath(os.path.dirname(__file__))
    version_directory = os.path.join(project_root, '../../../', 'version')
    mongo_prefix_command = 'mongo localhost:27017/presidio '
    log_file_path = '/var/log/presidio/presidio_rpm-' + time.strftime("%m_%d_%Y") + '.log'
    logger = logging.getLogger('Presidio-RPM-log')
    stdout_logger = logging.getLogger('Presiodio-RPM-stdout')


    def __init__(self, dest_version_descriptor, is_uninstall, rpm_name):
        self.dest_version = dest_version_descriptor
        self.is_uninstall = is_uninstall
        self.rpm_name = rpm_name
        self.current_version = ReachVersion(full_version_path="", version_description=VersionReader().read_current(rpm_name=rpm_name))
        self.create_logger()

    def print_banner(self):
        sec_to_sleep = 0.5
        long_sleep = 3
        time.sleep(long_sleep)
        print " ___   __   ___   ____  ___   __   __   __    ___ "
        print "(  _) /  \ (  ,) (_  _)/ __) / _) (  ) (  )  (  _)"
        print " ) _)( () ) )  \   )(  \__ \( (_  /__\  )(__  ) _)"
        print "(_)   \__/ (_)\_) (__) (___/ \__)(_)(_)(____)(___)"
        time.sleep(sec_to_sleep)
        time.sleep(sec_to_sleep)
        print "8 888888888o   8 888888888o.   8 8888888888      d888888o.    8 8888 8 888888888o.       8 8888     ,o888888o.     "
        time.sleep(sec_to_sleep)
        print "8 8888    `88. 8 8888    `88.  8 8888          .`8888:' `88.  8 8888 8 8888    `^888.    8 8888  . 8888     `88.  "
        time.sleep(sec_to_sleep)
        print "8 8888     `88 8 8888     `88  8 8888          8.`8888.   Y8  8 8888 8 8888        `88.  8 8888 ,8 8888       `8b  "
        time.sleep(sec_to_sleep)
        print "8 8888     ,88 8 8888     ,88  8 8888          `8.`8888.      8 8888 8 8888         `88  8 8888 88 8888        `8b"
        time.sleep(sec_to_sleep)
        print "8 8888.   ,88' 8 8888.   ,88'  8 888888888888   `8.`8888.     8 8888 8 8888          88  8 8888 88 8888         88"
        time.sleep(sec_to_sleep)
        print "8 888888888P'  8 888888888P'   8 8888            `8.`8888.    8 8888 8 8888          88  8 8888 88 8888         88 "
        time.sleep(sec_to_sleep)
        print "8 8888         8 8888`8b       8 8888             `8.`8888.   8 8888 8 8888         ,88  8 8888 88 8888        ,8P "
        time.sleep(sec_to_sleep)
        print "8 8888         8 8888 `8b.     8 8888         8b   `8.`8888.  8 8888 8 8888        ,88'  8 8888 `8 8888       ,8P  "
        time.sleep(sec_to_sleep)
        print "8 8888         8 8888   `8b.   8 8888         `8b.  ;8.`8888  8 8888 8 8888    ,o88P'    8 8888  ` 8888     ,88'   "
        time.sleep(sec_to_sleep)
        print "8 8888         8 8888     `88. 8 888888888888  `Y8888P ,88P'  8 8888 8 888888888P'       8 8888     `8888888P'"
        print ""
        print ""
        time.sleep(long_sleep)




    def create_logger(self):
        #log file logger
        logger_handler = logging.FileHandler(self.log_file_path)
        logger_formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
        logger_handler.setFormatter(logger_formatter)
        self.logger.addHandler(logger_handler)
        self.logger.setLevel(logging.INFO)
        # #################### logging samples ###############################
        # logger.error('We have a problem')
        # logger.info('While this is just chatty')
        # logger.debug('...')
        # logger.warning('...')
        # ####################################################################

        #stdout logger
        stdout_habdler = logging.StreamHandler(sys.stdout)
        stdout_habdler.setFormatter(logging.Formatter('%(asctime)s %(levelname)s %(message)s'))
        stdout_habdler.setLevel(logging.INFO)
        self.stdout_logger.addHandler(stdout_habdler)
        self.stdout_logger.setLevel(logging.INFO)
        # #################### logging samples ###############################
        # stdout_logger.error('We have a problem')
        # stdout_logger.info('While this is just chatty')
        # stdout_logger.debug('...')
        # stdout_logger.warning('...')
        # ####################################################################

    def add_log(self, log_string, log_level):
        if log_level == 'info':
            self.stdout_logger.info(log_string)
            self.logger.info(log_string)

        elif log_level == 'warning':
            self.stdout_logger.warning(log_string)
            self.logger.warning(log_string)

        elif log_level == 'error':
            self.stdout_logger.error(log_string)
            self.logger.error(log_string)

    def uninstall(self, rpm_name):
        """
        will execute while uninstalling rpm
        :param rpm_name:
        :return:
        :TODO: execute uninstall step
        """
        self.add_log(log_string='Uninstalling mig steps files', log_level='info')
        versions_list = self.get_versions_list()
        for version in versions_list:
            mig_step_json, mig_steps_folder = self.read_mig_step(version)

            if len(mig_step_json) > 0:
                mig_steps_str = str(mig_step_json[0])

                with open(mig_steps_str) as json_steps_file:
                    steps = json.load(json_steps_file)

                for step in steps['migration_steps']:
                    """
                    Execute the step 

                    """

                    if step.get('has_uninstall') == 'true' or step.get('has_uninstall') == 'True':
                        self.add_log(log_string=step.get('uninstall_description'), log_level='info')
                        self.exec_file(mig_steps_folder + step.get('filePath') + " " + step.get('uninstall_param'))

                self.current_version = version
            else:
                self.add_log(
                    log_string="No migration steps for version to uninstall: " + str(version.version_description.major) + '.' + str(
                        version.version_description.minor), log_level='warning')
                self.current_version = version



    def run(self, rpm_name):
        self.print_banner()
        versions_list = self.get_versions_list()

        for version in versions_list:
            self.add_log(log_string='Checking installed version before executing migration steps', log_level='info')
            if (str(self.current_version.version_description.major) < version.version_description.major) or (str(self.current_version.version_description.major) == version.version_description.major and str(self.current_version.version_description.minor) < version.version_description.minor) or (str(self.current_version.version_description.major) == version.version_description.major and str(self.current_version.version_description.minor) == version.version_description.minor and str(self.current_version.version_description.build) < str(args.build)) or (str(self.current_version.version_description.major) == version.version_description.major and str(self.current_version.version_description.minor) == version.version_description.minor and args.build == 0):
                """ 
                execute vresion migration scripts
                """
                mig_step_json, mig_steps_folder = self.read_mig_step(version)
                if len(mig_step_json) > 0:
                    mig_steps_str = str(mig_step_json[0])

                    with open(mig_steps_str) as json_steps_file:
                        steps = json.load(json_steps_file)

                    for step in steps['migration_steps']:
                        """
                        Execute the step 
                        """

                        if step.get('executor') == 'mongo':
                            self.add_log(log_string=step.get('description'), log_level='info')
                            self.exec_mongo_file(mig_steps_folder + step.get('filePath'))

                        else:
                            self.add_log(log_string=step.get('description'), log_level='info')

                            self.exec_file(mig_steps_folder + step.get('filePath'))
                            #mig_step_folder_path = os.path.join(mig_steps_folder, step.get('filePath'))
                            #mig_step_folder_path = Path(mig_steps_folder)
                    self.current_version = version
                else:
                    self.add_log(log_string="No migration steps for version: " + str(version.version_description.major) + '.' + str(version.version_description.minor), log_level='warning')
                    self.current_version = version
            #else:
             #   self.add_log(log_string='RPM version is lower or equal to current install version, Nothing To Do',
              #               log_level='warning')
               # """
                #  alredy installed- nothing to do
                #"""

    def exec_file(self, file_to_exec):
        self.add_log(log_string='Preparing Presidio python project ', log_level='info')
        self.add_log(log_string='[0/1] ', log_level='info')
        os.system('chmod +x ' + file_to_exec)
        self.add_log(log_string='[1/1] ', log_level='info')
        self.add_log(log_string='Executing project ', log_level='info')
        os.system(file_to_exec)

    def exec_mongo_file(self, file_to_exec):
        self.add_log(log_string='Mongo step recognized.. Executing step ', log_level='info')
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


def parse_args():
    global args
    parser = argparse.ArgumentParser(description='parses args from spec file')
    parser.add_argument('--rpm_name', help='the name of rpm going to be installed')
    #parser.add_argument('--time', help='pre or post jar deployment')
    parser.add_argument('--major', type=int, help='major version to install')
    parser.add_argument('--minor', type=int, help='minor version to install')
    parser.add_argument('--build', type=int, help='build number')
    parser.add_argument('--uninstall', help='if given: uninstall process will initiate', action='store_true')
    parser.add_argument('--user', help='if not given: will be "admin" ')
    parser.add_argument('--pass', help='if not given: will be "admin"')

    args = parser.parse_args()


if __name__ == "__main__":
    # todo: get args from cmdline
    parse_args()
    dest_version = VersionDescriptor(major=args.major, minor=args.minor, build=args.build)
    mng = Deploy_Manager(dest_version_descriptor=dest_version, is_uninstall=args.uninstall, rpm_name=args.rpm_name)
    if args.uninstall:
        mng.uninstall(args.rpm_name)
    else:
        mng.run(args.rpm_name)
