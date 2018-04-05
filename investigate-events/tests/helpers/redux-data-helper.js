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

  eventResults(data) {
    _set(this.state, 'eventResults.data', data);
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

  atLeastOneQueryIssued(flag) {
    _set(this.state, 'queryNode.atLeastOneQueryIssued', flag);
    return this;
  }

  hasIncommingQueryParams(flag) {
    _set(this.state, 'queryNode.hasIncommingQueryParams', flag);
    return this;
  }

  hasRequiredValuesToQuery(flag) {
    _set(this.state, 'queryNode.metaFilter', { conditions: [] });
    _set(this.state, 'queryNode.previouslySelectedTimeRanges', {});
    _set(this.state, 'queryNode.serviceId', '1');
    if (flag) {
      _set(this.state, 'queryNode.isDirty', true);
      _set(this.state, 'services.serviceData', [{ id: '1' }]);
      _set(this.state, 'services.summaryData', { startTime: 1506537600 });
    } else {
      _set(this.state, 'queryNode.isDirty', false);
      _set(this.state, 'services.serviceData', undefined);
      _set(this.state, 'services.summaryData', undefined);
    }
    return this;
  }

  isServicesLoading(flag) {
    _set(this.state, 'services.isServicesLoading', flag);
    return this;
  }

  isServicesRetrieveError(flag) {
    _set(this.state, 'services.isServicesRetrieveError', flag);
    return this;
  }

  isEventResultsError(isError, message = 'This is an error message') {
    _set(this.state, 'eventResults.status', isError ? 'error' : 'complete');
    if (isError === true) {
      _set(this.state, 'eventResults.message', message);
    }
    return this;
  }

  isSummaryLoading(flag) {
    _set(this.state, 'services.isSummaryLoading', flag);
    return this;
  }

  isSummaryDataInvalid(flag, id, errorMessage) {
    if (flag) {
      _set(this.state, 'queryNode.serviceId', id);
      _set(this.state, 'services.serviceData', [{ id, displayName: id, name: id, version: '0.0.0' }]);
      _set(this.state, 'services.isSummaryRetrieveError', true);
      _set(this.state, 'services.summaryErrorMessage', errorMessage);
    } else {
      this.hasSummaryData(true, id);
    }
    return this;
  }

  hasSummaryData(flag, id) {
    if (flag) {
      _set(this.state, 'queryNode.serviceId', id);
      _set(this.state, 'services.serviceData', [{ id, displayName: id, name: id, version: '0.0.0' }]);
      _set(this.state, 'services.summaryData', { startTime: 1 });
    } else {
      _set(this.state, 'queryNode.serviceId', id);
      _set(this.state, 'services.serviceData', [{ id, displayName: id, name: id, version: '0.0.0' }]);
      _set(this.state, 'services.summaryData', { startTime: 0 });
      _set(this.state, 'services.isServicesRetrieveError', false);
    }
    return this;
  }

  setQueryFiltersMeta(withConditions) {
    const nowSeconds = parseInt(+(new Date()) / 1000, 10);
    _set(this.state, 'queryNode.previouslySelectedTimeRanges', {});
    _set(this.state, 'queryNode.serviceId', '1');
    _set(this.state, 'queryNode.isDirty', true);
    _set(this.state, 'queryNode.startTime', nowSeconds);
    _set(this.state, 'queryNode.endTime', nowSeconds);
    if (withConditions) {
      _set(this.state, 'queryNode.metaFilter', { conditions: [
        { key: 'foo',
          operator: '=',
          value: 'bar'
        },
        { key: 'foo',
          operator: '=',
          value: 'bar'
        },
        { key: 'foo',
          operator: '=',
          value: 'bar'
        }]
      });
    } else {
      _set(this.state, 'queryNode.metaFilter', { conditions: [] });
    }
    return this;
  }
}
