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
        time = utils.time_utils.time_to_epoch(list(p[0] for p in c)[0 if is_start else -1])
        if not is_start:
            time + 60*60*24
        return time


class ImpalaDataCollection(DataCollection):
    def __init__(self, dir_path, data_class, table_name, connection):
        self._table_name = table_name
        self._connection = connection
        DataCollection.__init__(self, dir_path, data_class, table_name, connection)

    def _get_all_data_names(self):
        cursor = self._connection.cursor()
        cursor.execute('describe ' + self._table_name)
        return [field[0] for field in cursor if field[0].find('score') >= 0 and field[0] != 'eventscore']
