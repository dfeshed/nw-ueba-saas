import os

from data import Data
from .. import utils


class ImpalaData(Data):
    def __init__(self, dir_path, connection, table_name):
        self._connection = connection
        self._table_name = table_name
        Data.__init__(self, os.path.join(dir_path, table_name))

    def _find_boundary_time(self, is_start):
        c = self._connection.cursor()
        c.execute('show partitions ' + self._table_name)
        time = utils.string_to_epoch(list(p[0] for p in c)[0 if is_start else -1])
        if not is_start:
            time + 60*60*24
        return time
