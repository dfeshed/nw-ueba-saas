import re
import subprocess

INSTALLED_PRESIDIO_VERSION_FILE_NAME = "installed_presidio_version.txt"
PRESIDIO_NOT_INSTALLED_FLAG = "presidio_not_installed"


def get_installed_presidio_version():
    """
    :return: The version of Presidio that is currently installed, according to the RPMs (e.g. 11.2.0.5)
    :rtype: str
    """
    output = subprocess.check_output(["rpm", "-qa"]).split("\n")
    output = [rpm for rpm in output if "presidio-core" in rpm]

    if len(output) == 0:
        return PRESIDIO_NOT_INSTALLED_FLAG
    elif len(output) == 1:
        regular_expression = re.compile("^.*-presidio-core-(\d+\.\d+\.\d+\.\d+)-.*$")
        match = regular_expression.match(output[0])
        return match.group(1)
    else:
        raise ValueError("Expected to find at most one Presidio Core RPM installed")


def write_installed_presidio_version(version):
    """
    :param version: The version of Presidio that should be written to a predefined file (e.g. 11.2.0.5)
    :type version: str
    """
    installed_presidio_version_file = open(INSTALLED_PRESIDIO_VERSION_FILE_NAME, "w")
    installed_presidio_version_file.write(version)
    installed_presidio_version_file.close()


def read_installed_presidio_version():
    """
    :return: The version of Presidio that was written to a predefined file (e.g. 11.2.0.5)
    :rtype: str
    """
    installed_presidio_version_file = open(INSTALLED_PRESIDIO_VERSION_FILE_NAME, "r")
    version = installed_presidio_version_file.readline()
    installed_presidio_version_file.close()
    return version.strip()


def version_comparator(first, second):
    """
    :param first: The first Presidio version
    :type first: str
    :param second: The second Presidio version
    :type second: str
    :return: A negative int, zero or a positive int as first is less than, equal to or greater than second
    :rtype: int
    """
    first = first.split(".")
    second = second.split(".")

    if len(first) != 4 or len(second) != 4:
        raise ValueError("Presidio versions are expected to have four parts (e.g. 11.2.0.5)")

    for i in range(4):
        diff = int(first[i]) - int(second[i])

        if diff != 0:
            return diff

    return 0
