import json
import os


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

    def save(self):
        with open(self._path, 'w') as f:
            json.dump(self._data, f)

    def set(self, name, value):
        self._data[name] = value
        self.save()

    def get(self, name):
        return self._data.get(name)

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return json.dumps(self._data, indent = 4, sort_keys = True)
