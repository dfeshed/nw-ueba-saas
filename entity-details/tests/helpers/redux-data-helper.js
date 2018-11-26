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
    this.state = {
      entity: {
        entityId: null,
        entityType: null,
        entityDetails: null
      },
      alerts: {
        alertId: null
      },
      indicators: {
        indicatorId: null
      }
    };
    this.setState = setState;
  }

  // Trigger setState, also return the resulting state
  // in case it needs to be used/checked
  build() {
    const state = Immutable.from({
      ...this.state
    });
    if (this.setState) {
      this.setState(state);
    }
    return state.asMutable();
  }

  entityId(entityDetails) {
    _set(this.state, 'entity', entityDetails);
    return this;
  }
  entityDetails(entityDetails) {
    _set(this.state, 'entity.entityDetails', entityDetails);
    return this;
  }
  alertId(alertId) {
    _set(this.state, 'alerts.alertId', alertId);
    return this;
  }
  indicatorId(indicatorId) {
    _set(this.state, 'indicators.indicatorId', indicatorId);
    return this;
  }
}
