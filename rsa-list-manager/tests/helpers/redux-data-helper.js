import Immutable from 'seamless-immutable';

const _set = (obj, key, val) => {
  if (obj[key]) {
    obj[key] = val;
    return;
  }

  const keys = key.split('.');
  const firstKey = keys.shift();

  if (!obj[firstKey]) {
    obj[firstKey] = {};
  }

  if (keys.length === 0) {
    obj[firstKey] = val;
    return;
  } else {
    _set(obj[firstKey], keys.join('.'), val);
  }
};

export default class DataHelper {
  constructor(setState) {
    this.state = {};
    this.setState = setState;
  }

  // Trigger setState, also return the resulting state
  // in case it needs to be used/checked
  build() {
    const state = Immutable.from({
      listManager: this.state
    });
    if (this.setState) {
      this.setState(state);
    }
    return state.asMutable();
  }

  highlightedIndex(index = -1) {
    _set(this.state, 'highlightedIndex', index);
    return this;
  }

  listLocation(location) {
    _set(this.state, 'listLocation', location);
    return this;
  }

  listName(name) {
    _set(this.state, 'listName', name);
    return this;
  }
}
