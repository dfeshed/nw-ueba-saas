import Immutable from 'seamless-immutable';
import CONFIG from 'investigate-events/reducers/investigate/config';

export const DEFAULT_LANGUAGES = [
  { count: 0, format: 'Text', metaName: 'a', flags: 1, displayName: 'A', formattedName: 'a (A)' },
  { count: 0, format: 'Text', metaName: 'b', flags: 2, displayName: 'B', formattedName: 'b (B)' },
  { count: 0, format: 'Text', metaName: 'c', flags: 3, displayName: 'C', formattedName: 'c (C)' },
  { count: 0, format: 'Text', metaName: 'c.1', flags: 3, displayName: 'C1', formattedName: 'c.1 (C 1)' },
  { count: 0, format: 'Text', metaName: 'c.2', flags: 3, displayName: 'C2', formattedName: 'c.2 (C 2)' },
  { count: 0, format: 'Text', metaName: 'medium', flags: 3, displayName: 'Medium', formattedName: 'medium (Medium)' },
  { count: 0, format: 'Text', metaName: 'filename', flags: 3, displayName: 'File Name', formattedName: 'filename (File Name)' },
  { count: 0, format: 'Text', metaName: 'user.dst', flags: 3, displayName: 'User DST', formattedName: 'user.dst (User DST)' },
  { count: 0, format: 'Float32', metaName: 'file.entropy', flags: -2147482877, displayName: 'File Entropy', formattedName: 'file.entrophy (File Entrophy)' },
  { count: 0, format: 'IPv4', metaName: 'alias.ip', flags: -2147482621, displayName: 'IP Aliases', formattedName: 'alias.ip (IP Aliases)' },
  { count: 0, format: 'IPv6', metaName: 'alias.ipv6', flags: -2147482621, displayName: 'IPv6 Aliases', formattedName: 'alias.ipv6 (IPv6 Aliases)' },
  { count: 0, format: 'MAC', metaName: 'alias.mac', flags: -2147482621, displayName: 'MAC Aliases', formattedName: 'alias.mac (MAC Aliases)' },
  { count: 0, format: 'Text', metaName: 'alert', flags: -2147483133, displayName: 'Alerts', formattedName: 'alert (Alerts)' },
  { count: 0, format: 'TimeT', metaName: 'starttime', flags: -2147482621, displayName: 'Time Start', formattedName: 'starttime (Time Start)' },
  { count: 0, format: 'UInt8', metaName: 'ip.proto', flags: -2147482541, displayName: 'IP Protocol', formattedName: 'ip.proto (IP Protocol)' },
  { count: 0, format: 'UInt16', metaName: 'eth.type', flags: -2147482541, displayName: 'Ethernet Protocol', formattedName: 'eth.type (Ethernet Protocol)' },
  { count: 0, format: 'UInt32', metaName: 'bytes.src', flags: -2147482878, displayName: 'Bytes Sent', formattedName: 'bytes.src (Bytes Sent)' },
  { count: 0, format: 'UInt64', metaName: 'filename.size', flags: -2147482878, displayName: 'File Size', formattedName: 'filename.size (File Size)' },
  { count: 0, format: 'Text', metaName: 'referer', flags: -2147482878, displayName: 'Referer', formattedName: 'referer (Referer)' },
  { count: 0, format: 'UInt64', metaName: 'sessionid', flags: -2147483631, displayName: 'Session ID', formattedName: 'sessionid (Session ID)' }
];

const DEFAULT_PILLS_DATA = [{
  id: '1',
  meta: 'a',
  operator: '=',
  value: '\'x\'',
  isEditing: false,
  isInvalid: false,
  isSelected: false,
  complexFilterText: undefined
}, {
  id: '2',
  meta: 'b',
  operator: '=',
  value: '\'y\'',
  isEditing: false,
  isInvalid: false,
  isSelected: false,
  complexFilterText: undefined
}];

const COMPLEX_PILL_DATA = [{
  id: '1',
  meta: undefined,
  operator: undefined,
  value: undefined,
  isEditing: false,
  isInvalid: false,
  isSelected: false,
  complexFilterText: 'foo = bar'
}];

const INVALID_PILL_DATA = [{
  id: 1,
  meta: 'ip.proto',
  operator: '=',
  value: '\'boom\'',
  isEditing: false,
  isInvalid: false,
  isSelected: false,
  complexFilterText: undefined
}, {
  id: 2,
  meta: 'starttime',
  operator: '=',
  value: '\'boom\'',
  isEditing: false,
  isInvalid: true,
  validationError: 'something not right',
  isSelected: false,
  complexFilterText: undefined
}];

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
    if (this.setState) {
      this.setState(state);
    }
    return state.asMutable();
  }

  serviceId(id = '123') {
    _set(this.state, 'queryNode.serviceId', id);
    return this;
  }

  startTime(time = 'early') {
    _set(this.state, 'queryNode.startTime', time);
    return this;
  }

  endTime(time = 'late') {
    _set(this.state, 'queryNode.endTime', time);
    return this;
  }

  // event-count
  eventCount(count) {
    _set(this.state, 'eventCount.data', count);
    return this;
  }

  queryView(view) {
    _set(this.state, 'queryNode.queryView', view);
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

  allEventsSelected(val) {
    _set(this.state, 'eventResults.allEventsSelected', val);
    return this;
  }

  withSelectedEventIds() {
    _set(this.state, 'eventResults.selectedEventIds', ['bar']);
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

  atLeastOneQueryIssued(flag) {
    _set(this.state, 'queryNode.atLeastOneQueryIssued', flag);
    return this;
  }

  hasIncommingQueryParams(flag) {
    _set(this.state, 'queryNode.hasIncommingQueryParams', flag);
    return this;
  }

  canQueryGuided(flag = true) {
    this.hasRequiredValuesToQuery(flag);
    if (!flag) {
      // Add an invalid pill
      _set(this.state, 'queryNode.pillsData', INVALID_PILL_DATA);
    }
    return this;
  }

  hasRequiredValuesToQuery(flag) {
    _set(this.state, 'queryNode.metaFilter', []);
    _set(this.state, 'queryNode.previouslySelectedTimeRanges', {});
    _set(this.state, 'queryNode.serviceId', '1');
    _set(this.state, 'queryNode.queryView', 'guided');
    _set(this.state, 'queryNode.pillsData', []);
    if (flag) {
      _set(this.state, 'services.serviceData', [{ id: '1' }]);
      _set(this.state, 'services.summaryData', { startTime: 1506537600 });
    } else {
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

  hasFatalSummaryError(isError) {
    _set(this.state, 'services.isSummaryRetrieveError', isError);
    if (isError) {
      _set(this.state, 'services.summaryErrorCode', 3);
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
      _set(this.state, 'services.serviceData', [{ id, displayName: id, name: id, version: '11.1.0.0' }]);
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
      _set(this.state, 'services.serviceData', [{ id, displayName: id, name: id, version: '11.1.0.0' }]);
      _set(this.state, 'services.summaryData', { startTime: 1 });
    } else {
      _set(this.state, 'queryNode.serviceId', id);
      _set(this.state, 'services.serviceData', [{ id, displayName: id, name: id, version: '11.1.0.0' }]);
      _set(this.state, 'services.summaryData', { startTime: 0 });
      _set(this.state, 'services.isServicesRetrieveError', false);
    }
    return this;
  }

  language(language = DEFAULT_LANGUAGES) {
    _set(this.state, 'dictionaries.language', language);
    return this;
  }

  // GUIDED

  pillsDataEmpty() {
    _set(this.state, 'queryNode.pillsData', []);
    return this;
  }

  pillsDataPopulated(pD = DEFAULT_PILLS_DATA) {
    _set(this.state, 'queryNode.pillsData', pD);
    return this;
  }

  pillsDataComplex(pD = COMPLEX_PILL_DATA) {
    _set(this.state, 'queryNode.pillsData', pD);
    return this;
  }

  invalidPillsDataPopulated(pD = INVALID_PILL_DATA) {
    _set(this.state, 'queryNode.pillsData', pD);
    return this;
  }

  updatedFreeFormTextPill(text = 'foo') {
    _set(this.state, 'queryNode.updatedFreeFormTextPill', text);
    return this;
  }

  markInvalid(pillIds = []) {
    this.state.queryNode.pillsData = this.state.queryNode.pillsData.map((pD) => {
      if (pillIds.includes(pD.id)) {
        pD = {
          ...pD,
          isInvalid: true,
          validationError: 'something is up'
        };
      }

      return pD;
    });
    return this;
  }

  markSelected(pillIds = []) {
    this.state.queryNode.pillsData = this.state.queryNode.pillsData.map((pD) => {
      if (pillIds.includes(pD.id)) {
        pD = {
          ...pD,
          isSelected: true
        };
      }

      return pD;
    });
    return this;
  }

  markEditing(pillIds = []) {
    this.state.queryNode.pillsData = this.state.queryNode.pillsData.map((pD) => ({
      ...pD,
      isEditing: pillIds.includes(pD.id)
    }));
    return this;
  }
}
