import os
import subprocess
import utils

from_version = utils.read_installed_presidio_version()
to_version = utils.get_installed_presidio_version()

if from_version != utils.PRESIDIO_NOT_INSTALLED_FLAG:
    # Get a list of all the versions that have an upgrade script
    # (a file named "11.2.0.5.py" is an upgrade script TO version 11.2.0.5)
    versions = [version[:-3] for version in os.listdir("versions")]
    # Filter out the upgrade scripts to versions less than or equal to the previously installed version
    versions = [version for version in versions if utils.version_comparator(from_version, version) < 0]
    # Filter out the upgrade scripts to versions greater than the currently installed version
    versions = [version for version in versions if utils.version_comparator(version, to_version) <= 0]
    # Sort the remaining upgrade scripts according to their version
    versions.sort(cmp=utils.version_comparator)

    # Execute the upgrade scripts in order
    for version in versions:
        subprocess.call(["python", "versions/%s.py" % version])
