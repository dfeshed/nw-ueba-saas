import json
import os

from common import utils


class Store:
    def __init__(self, path):
        self._path = path
        if os.path.isfile(path):
            self._load()
        else:
            self._data = {}

    def _load(self):
        with open(self._path, 'r') as f:
            self._data = json.load(f)

    def _save(self):
        with utils.FileWriter(self._path) as f:
            json.dump(self._data, f)

    def set(self, name, value):
        self._data[name] = value
        self._save()

    def get(self, name):
        return self._data.get(name)

    def is_empty(self):
        return len(self._data) == 0

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return json.dumps(self._data, indent = 4, sort_keys = True)
