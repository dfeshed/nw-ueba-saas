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
      processAnalysis: this.state
    });
    this.setState(state);
    return state.asMutable();
  }

  queryInput(data = {}) {
    _set(this.state, 'processTree.queryInput', data);
    return this;
  }

  path(path) {
    _set(this.state, 'processTree.path', path);
    return this;
  }
  error(value = false) {
    _set(this.state, 'processTree.error', value);
    return this;
  }
  processProperties(processProperties) {
    _set(this.state, 'processProperties.hostDetails', processProperties);
    return this;
  }
  detailsTabSelected(detailsTabSelected) {
    _set(this.state, 'processVisuals.detailsTabSelected', detailsTabSelected);
    return this;
  }
  eventsData(data = []) {
    _set(this.state, 'processTree.eventsData', data);
    return this;
  }
}
