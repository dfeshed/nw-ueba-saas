import Immutable from 'seamless-immutable';
import CONFIG from 'investigate-events/reducers/investigate/config';

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

  columnGroup(columnGroup) {
    _set(this.state, 'data.columnGroup', columnGroup);
    return this;
  }

  columnGroups(columnGroups) {
    _set(this.state, 'data.columnGroups', columnGroups);
    return this;
  }

  reconSize(reconSize) {
    _set(this.state, 'data.reconSize', reconSize);
    return this;
  }

  eventsPreferencesConfig() {
    _set(this.state, 'data.eventsPreferencesConfig', CONFIG);
    return this;
  }

  eventThreshold(threshold) {
    _set(this.state, 'eventCount.threshold', threshold);
    return this;
  }

  eventCountStatus(status) {
    _set(this.state, 'eventCount.status', status);
    return this;
  }

  eventCountReason(code) {
    _set(this.state, 'eventCount.reason', code);
    return this;
  }

  isInvalidQuery(flag) {
    if (flag) {
      _set(this.state, 'eventCount.status', 'rejected');
      _set(this.state, 'eventCount.reason', 11);
      return this;
    } else {
      _set(this.state, 'eventCount.status', 'resolved');
      _set(this.state, 'eventCount.reason', 0);
      return this;
    }
  }

}
