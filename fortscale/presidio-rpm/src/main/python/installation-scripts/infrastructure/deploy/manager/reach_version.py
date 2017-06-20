from version_descriptor import VersionDescriptor


class ReachVersion:
    full_version_path=None
    version_description=None

    def __init__(self,full_version_path,version_description):
        """

        :type full_version_path:PosixPath
        :type version_description:VersionDescriptor
        """
        self.full_version_path=full_version_path
        self.version_description=version_description