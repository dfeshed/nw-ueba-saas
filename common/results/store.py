import json

from common import utils


class Store:
    def __init__(self, path):
        self._path = path

    def _load(self):
        with open(self._path, 'r') as f:
            return json.load(f)

    def _save(self, data):
        with utils.FileWriter(self._path) as f:
            json.dump(data, f)

    def set(self, name, value):
        data = self._load()
        data[name] = value
        self._save(data)

    def get(self, name, default_value = None):
        return self._load().get(name, default_value)

    def is_empty(self):
        return len(self._load()) == 0

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return json.dumps(self._load(), indent = 4, sort_keys = True)
