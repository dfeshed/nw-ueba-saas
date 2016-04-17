import json
import os

from .. import utils


class Store:
    def __init__(self, path):
        self._path = path
        if os.path.isfile(path):
            with open(path, 'r') as f:
                self._data = json.load(f)
        else:
            self._data = {}

    def set(self, name, value):
        self._data[name] = value
        with utils.io.FileWriter(self._path) as f:
            json.dump(self._data, f)

    def get(self, name, default_value = None):
        return self._data.get(name, default_value)

    def is_empty(self):
        return len(self._data) == 0

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return json.dumps(self._data, indent = 4, sort_keys = True)
