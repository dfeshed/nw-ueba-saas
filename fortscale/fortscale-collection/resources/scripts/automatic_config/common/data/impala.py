from data import Data, DataCollection
from .. import utils


class ImpalaData(Data):
    def __init__(self, dir_path, table_name, name, connection):
        self._connection = connection
        self._table_name = table_name
        Data.__init__(self, dir_path, name)

    def _find_boundary_time(self, is_start):
        c = self._connection.cursor()
        c.execute('show partitions ' + self._table_name)
        time = utils.time_utils.get_epochtime(list(p[0] for p in c if p[0] != 'Total')[0 if is_start else -1])
        if not is_start:
            time += 60*60*24
        return time


class ImpalaDataCollection(DataCollection):
    def __init__(self, dir_path, data_class, table_name, connection):
        self._table_name = table_name
        self._connection = connection
        DataCollection.__init__(self, dir_path, data_class, table_name, connection)
