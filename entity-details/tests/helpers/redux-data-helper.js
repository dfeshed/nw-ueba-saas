import Immutable from 'seamless-immutable';
import userAlerts from '../data/presidio/user_alerts';
import indicatorEvents from '../data/presidio/indicator-events';
import indicatorCount from '../data/presidio/indicator-count';


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
        entityId: 'user-1',
        entityType: 'user',
        entityDetails: null
      },
      alerts: {
        selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
        alerts: userAlerts.data,
        sortBy: { id: 'severity' }
      },
      indicators: {
        selectedIndicatorId: '8614aa7f-c8ee-4824-9eaf-e0bb199cd006',
        events: indicatorEvents.data,
        historicalData: indicatorCount.data,
        totalEvents: 100,
        eventFilter: {
          page: 1,
          size: 100,
          sort_direction: 'DESC'
        }
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

  entityId(entityId) {
    _set(this.state, 'entity.entityId', entityId);
    return this;
  }

  entityType(entityType) {
    _set(this.state, 'entity.entityType', entityType);
    return this;
  }

  entityDetails(entityDetails) {
    _set(this.state, 'entity.entityDetails', entityDetails);
    return this;
  }

  selectedAlertId(alertId) {
    _set(this.state, 'alerts.selectedAlertId', alertId);
    return this;
  }

  alerts(alerts) {
    _set(this.state, 'alerts.alerts', alerts);
    return this;
  }

  alertsSortBy(sortBy) {
    _set(this.state, 'alerts.sortBy', { id: sortBy });
    return this;
  }

  selectedIndicatorId(indicatorId) {
    _set(this.state, 'indicators.selectedIndicatorId', indicatorId);
    return this;
  }

  events(events) {
    _set(this.state, 'indicators.events', events);
    return this;
  }

  historicalData(historicalData) {
    _set(this.state, 'indicators.historicalData', historicalData);
    return this;
  }
  totalEvents(totalEvents) {
    _set(this.state, 'indicators.totalEvents', totalEvents);
    return this;
  }
  eventFilter(eventFilter) {
    _set(this.state, 'indicators.eventFilter', eventFilter);
    return this;
  }

}
