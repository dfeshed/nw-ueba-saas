import Immutable from 'seamless-immutable';
import { config } from '../data/data';

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

  defaultConfig(configData = config) {
    _set(this.state, 'defaultPackagerConfig', configData);
    return this;
  }

  setData(path, data) {
    _set(this.state, path, data);
    return this;
  }

  // Trigger setState, also return the resulting state
  // in case it needs to be used/checked
  build() {
    const state = Immutable.from({
      packager: this.state
    });
    this.setState(state);
    return state.asMutable();
  }

}
