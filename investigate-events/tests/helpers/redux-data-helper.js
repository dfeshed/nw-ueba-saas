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
      investigate: this.state
    });
    this.setState(state);
    return state.asMutable();
  }

  // event-count
  eventCount(count) {
    _set(this.state, 'eventCount.data', count);
    return this;
  }

  // event-count
  eventThreshold(threshold) {
    _set(this.state, 'eventCount.threshold', threshold);
    return this;
  }

}
