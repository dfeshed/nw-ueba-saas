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
      context: this.state
    });
    this.setState(state);
    return state.asMutable();
  }

  setData(path, data) {
    _set(this.state, path, data);
    return this;
  }

  setActiveTabName(data) {
    _set(this.state, 'tabs.activeTabName', data);
    return this;
  }

  initializeContextPanel(data) {
    _set(this.state, 'context', data);
    return this;
  }

  setDataSources(data) {
    _set(this.state, 'tabs.dataSources', data);
    return this;
  }

  setLookupData(data) {
    _set(this.state, 'context.lookupData', data);
    return this;
  }

  setHeaderButtons(data) {
    _set(this.state, 'tabs.headerButtons', data);
    return this;
  }

}
