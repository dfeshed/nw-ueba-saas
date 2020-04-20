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

  downloadId(downloadId) {
    _set(this.state, 'downloadId', downloadId);
    return this;
  }

  loading(loading) {
    _set(this.state, 'loading', loading);
    return this;
  }

  testConfigLoader(loading) {
    _set(this.state, 'testConfigLoader', loading);
    return this;
  }

  defaultRARConfig(data) {
    _set(this.state, 'defaultRARConfig.rarConfig', data);
    return this;
  }

  initialRARConfig(data) {
    _set(this.state, 'initialRARConfig.rarConfig', data);
    return this;
  }

  setEnableRARStatus(data) {
    _set(this.state, 'isEnabled', data);
    return this;
  }

  // Trigger setState, also return the resulting state
  // in case it needs to be used/checked
  build() {
    const state = Immutable.from({
      rar: this.state
    });
    this.setState(state);
    return state.asMutable();
  }

}
