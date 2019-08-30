import Immutable from 'seamless-immutable';
import CONFIG from 'investigate-events/reducers/investigate/config';
import EventColumnGroups from '../data/subscriptions/column-group/findAll/data';

export const DEFAULT_LANGUAGES = [
  { count: 0, format: 'Text', metaName: 'a', flags: 2, displayName: 'A', formattedName: 'a (A)' },
  { count: 0, format: 'Text', metaName: 'b', flags: 2, displayName: 'B', formattedName: 'b (B)' },
  { count: 0, format: 'Text', metaName: 'c', flags: 3, displayName: 'C', formattedName: 'c (C)' },
  { count: 0, format: 'Text', metaName: 'c.1', flags: 3, displayName: 'C1', formattedName: 'c.1 (C 1)' },
  { count: 0, format: 'Text', metaName: 'c.2', flags: 3, displayName: 'C2', formattedName: 'c.2 (C 2)' },
  { count: 0, format: 'UInt8', metaName: 'medium', flags: -2147482541, displayName: 'Medium', formattedName: 'medium (Medium)' },
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

export const DEFAULT_ALIASES = {
  medium: {
    '1': 'Ethernet',
    '2': 'Tokenring',
    '3': 'FDDI',
    '4': 'HDLC',
    '5': 'NetWitness',
    '6': '802.11',
    '7': '802.11 Radio',
    '8': '802.11 AVS',
    '9': '802.11 PPI',
    '10': '802.11 PRISM',
    '11': '802.11 Management',
    '12': '802.11 Control',
    '13': 'DLT Raw',
    '32': 'Logs',
    '33': 'Correlation'
  },
  'ip.proto': {
    '1': 'ICMP',
    '6': 'TCP',
    '17': 'UDP',
    '27': 'RDP',
    '41': 'IPv6',
    '58': 'IPv6-ICMP',
    '92': 'MTP',
    '115': 'L2TP',
    '255': 'Reserved'
  }
};

const DEFAULT_PILLS_DATA = [{
  id: '1',
  meta: 'a',
  operator: '=',
  value: '\'x\'',
  type: 'query',
  isEditing: false,
  isFocused: false,
  isInvalid: false,
  isSelected: false,
  complexFilterText: undefined
}, {
  id: '2',
  meta: 'b',
  operator: '=',
  value: '\'y\'',
  type: 'query',
  isEditing: false,
  isFocused: false,
  isInvalid: false,
  isSelected: false,
  complexFilterText: undefined
}];

const COMPLEX_PILL_DATA = [{
  id: '1',
  meta: undefined,
  operator: undefined,
  value: undefined,
  type: 'complex',
  isEditing: false,
  isInvalid: false,
  isSelected: false,
  complexFilterText: 'foo = bar'
}];

const TEXT_PILL_DATA = [{
  id: '1',
  type: 'text',
  isEditing: false,
  isFocused: false,
  isInvalid: false,
  isSelected: false,
  isValidationInProgress: false,
  searchTerm: 'blahblahblah'
}];

const INVALID_PILL_DATA = [{
  id: '1',
  meta: 'ip.proto',
  operator: '=',
  value: '\'boom\'',
  type: 'query',
  isEditing: false,
  isInvalid: false,
  isSelected: false,
  isFocused: false,
  complexFilterText: undefined
}, {
  id: '2',
  meta: 'starttime',
  operator: '=',
  value: '\'boom\'',
  type: 'query',
  isEditing: false,
  isInvalid: true,
  isFocused: false,
  validationError: 'something not right',
  isSelected: false,
  complexFilterText: undefined
}];

const PILLS_WITH_PARENS = [{
  type: 'open-paren',
  id: '1',
  twinId: 'twinPill_11'
}, {
  id: '2',
  isEditing: false,
  isFocused: false,
  isInvalid: false,
  isSelected: false,
  isValidationInProgress: false,
  meta: 'action',
  operator: '=',
  value: '\'foo\'',
  type: 'query'
}, {
  type: 'close-paren',
  id: '3',
  twinId: 'twinPill_11'
}];

const PILLS_WITH_EMPTY_PARENS = [{
  type: 'open-paren',
  id: '1',
  twinId: 'twinPill_11'
}, {
  type: 'close-paren',
  id: '2',
  twinId: 'twinPill_11'
}];


const _optionsInitialState = {
  size: 20,
  threshold: 25000,
  metric: 'sessions',
  sortField: 'total',
  sortOrder: 'descending'
};

const values = {
  data: [
    {
      value: 'foo',
      count: 9821
    },
    {
      value: 'bar',
      count: 9638
    }
  ],
  status: 'complete',
  complete: true
};

const metaKeyStates = [{
  info: {
    count: 0,
    format: 'Text',
    metaName: 'action',
    flags: -2147482621,
    displayName: 'Action Event',
    formattedName: 'action (Action Event)',
    isOpen: true
  },
  values
}, {
  info: {
    count: 0,
    format: 'Text',
    metaName: 'ad.computer.src',
    flags: -2147482621,
    displayName: 'Active Directory Workstation Source',
    formattedName: 'ad.computer.src (Active Directory Workstation Source)',
    isOpen: true
  }
}, {
  info: {
    format: 'Float32',
    metaName: 'file.entropy',
    flags: -2147482877,
    displayName: 'File Entropy',
    formattedName: 'file.entrophy (File Entrophy)',
    isOpen: false
  }
}];

const emptyMetaKeyState = {
  meta: [],
  options: _optionsInitialState,
  metaPanelSize: 'default'
};

const metaState = {
  meta: metaKeyStates,
  options: _optionsInitialState,
  metaPanelSize: 'default'
};

const getRecentQueryObjects = (array) => {
  const obArray = array.map((st) => {
    return {
      id: 1,
      query: st,
      displayName: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15),
      createdOn: 'foo',
      createdBy: 'bar'
    };
  });
  return obArray;
};

const defaultRecentQueriesFilteredList = [
  'medium = 32',
  'medium = 32 || medium = 1',
  '(ip.dst = 10.2.54.11 && ip.src = 1.1.1.1 || ip.dst = 10.2.54.1 && ip.src = 1.1.3.3) && medium = 32'
];

const defaultRecentQueriesUnfilteredList = [
  'medium = 32',
  'medium = 32 || medium = 1',
  'sessionid = 1 && sessionid = 80',
  'action = \'GET\' || action = \'PUT\'',
  '(ip.dst = 10.2.54.11 && ip.src = 1.1.1.1 || ip.dst = 10.2.54.1 && ip.src = 1.1.3.3) && medium = 32',
  'service = 80 || service = 90',
  'foo = bar && bar = foo'
];

const DEFAULT_VALUE_SUGGESTIONS = [
  {
    displayName: 'foo',
    description: 'Suggestions'
  },
  {
    displayName: 'bar',
    description: 'Suggestions'
  },
  {
    displayName: 'foobar',
    description: 'Suggestions'
  }
];

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

  eventCount(count) {
    _set(this.state, 'eventCount.data', count);
    return this;
  }

  eventsQuerySort(field, dir) {
    _set(this.state, 'data.sortField', field);
    _set(this.state, 'data.sortDirection', dir);
    return this;
  }

  visibleColumns(cols = [{ field: 'time' }, { field: 'medium' }]) {
    _set(this.state, 'eventResults.visibleColumns', cols);
    return this;
  }

  sortableColumns(cols = ['time']) {
    _set(this.state, 'data.validEventSortColumns', cols);
    return this;
  }

  searchTerm(term = 'foo') {
    _set(this.state, 'eventResults.searchTerm', term);
    return this;
  }

  streamLimit(limit) {
    _set(this.state, 'eventResults.streamLimit', limit);
    return this;
  }

  streamBatch(batch) {
    _set(this.state, 'eventResults.streamBatch', batch);
    return this;
  }

  eventTimeSortOrder(sort = 'Ascending') {
    _set(this.state, 'data.eventAnalysisPreferences.eventTimeSortOrder', sort);
    return this;
  }

  queryView(view) {
    _set(this.state, 'queryNode.queryView', view);
    return this;
  }

  selectedColumnGroup(columnGroup) {
    _set(this.state, 'data.selectedColumnGroup', columnGroup);
    return this;
  }

  columnGroups(columnGroups = EventColumnGroups) {
    _set(this.state, 'columnGroup.columnGroups', columnGroups);
    return this;
  }

  getColumns(columnGroup = EventColumnGroups[0].id, columnGroups = EventColumnGroups) {
    this.selectedColumnGroup(columnGroup);
    this.columnGroups(columnGroups);
    return this;
  }

  reconSize(reconSize) {
    _set(this.state, 'data.reconSize', reconSize);
    return this;
  }

  eventsPreferencesConfig() {
    _set(this.state, 'data.eventsPreferencesConfig', CONFIG);
    _set(this.state, 'data.globalPreferences', {
      dateFormat: 'DD/MM/YYYY',
      timeFormat: 'hh:mm:ss.SSS a',
      timeZone: 'UTC',
      locale: 'en'
    });
    return this;
  }

  defaultEventAnalysisPreferences() {
    _set(this.state, 'data.eventAnalysisPreferences', CONFIG.defaultPreferences.eventAnalysisPreferences);
    return this;
  }

  setEventAnalysisPreferencesForDownload(log, network, meta) {
    _set(this.state, 'data.eventAnalysisPreferences.defaultLogFormat', log);
    _set(this.state, 'data.eventAnalysisPreferences.defaultPacketFormat', network);
    _set(this.state, 'data.eventAnalysisPreferences.defaultMetaFormat', meta);
    return this;
  }

  setAutoDownloadPreference(autoDownload) {
    _set(this.state, 'files.isAutoDownloadFile', autoDownload);
    return this;
  }

  setFileExtractStatus(status) {
    _set(this.state, 'files.fileExtractStatus', status);
    return this;
  }

  setFileExtractLink(link) {
    _set(this.state, 'files.fileExtractLink', link);
    return this;
  }

  isCanceled() {
    _set(this.state, 'eventResults.status', 'canceled');
    return this;
  }

  eventTimeSortOrderPreferenceWhenQueried(pref = 'Ascending') {
    _set(this.state, 'eventResults.eventTimeSortOrderPreferenceWhenQueried', pref);
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

  withSelectedEventIds(ids = { bar: 'bar' }) {
    _set(this.state, 'eventResults.selectedEventIds', ids);
    return this;
  }

  selectedEventIds(eventIds) {
    _set(this.state, 'eventResults.selectedEventIds', eventIds);
    return this;
  }

  eventResultsStatus(status) {
    _set(this.state, 'eventResults.status', status);
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
      _set(this.state, 'services.serviceData', [{ id: '1', displayName: 'concentrator', version: '11.4.0' }]);
      _set(this.state, 'services.summaryData', { startTime: 1506537600 });
    } else {
      _set(this.state, 'services.serviceData', undefined);
      _set(this.state, 'services.summaryData', undefined);
    }
    return this;
  }

  withoutMinimumCoreServicesVersionForColumnSorting() {
    _set(this.state, 'services.serviceData', [{ id: '1', displayName: 'concentrator', version: '11.3.0' }]);
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
      _set(this.state, 'services.serviceData', [{ id, displayName: id, name: id, version: '11.4.0.0' }]);
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
      _set(this.state, 'services.serviceData', [{ id, displayName: id, name: id, version: '11.4.0.0' }]);
      _set(this.state, 'services.summaryData', { startTime: 1 });
    } else {
      _set(this.state, 'queryNode.serviceId', id);
      _set(this.state, 'services.serviceData', [{ id, displayName: id, name: id, version: '11.4.0.0' }]);
      _set(this.state, 'services.summaryData', { startTime: 0 });
      _set(this.state, 'services.isServicesRetrieveError', false);
    }
    return this;
  }

  autoUpdateSummary(flag) {
    _set(this.state, 'services.autoUpdateSummary', flag);
    return this;
  }

  previouslySelectedTimeRanges() {
    _set(this.state, 'queryNode.previouslySelectedTimeRanges', { 1: 'LAST_24_HOURS' });
    return this;
  }

  language(language = DEFAULT_LANGUAGES) {
    _set(this.state, 'dictionaries.language', language);
    return this;
  }

  aliases(aliases = DEFAULT_ALIASES) {
    _set(this.state, 'dictionaries.aliases', aliases);
    return this;
  }

  metaFilter(metaFilter = DEFAULT_PILLS_DATA) {
    _set(this.state, 'queryNode.metaFilter', metaFilter);
    return this;
  }

  metaPanel({ init = false, customMeta }) {
    if (init) {
      _set(this.state, 'meta', metaState);
    } else if (customMeta) {
      _set(this.state, 'meta', customMeta);
    } else {
      _set(this.state, 'meta', emptyMetaKeyState);
    }
    return this;
  }

  setMetaPanelSize(size) {
    _set(this.state, 'meta.metaPanelSize', size);
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

  pillsDataText(pD = TEXT_PILL_DATA) {
    _set(this.state, 'queryNode.pillsData', pD);
    return this;
  }

  pillsDataWithParens(pD = PILLS_WITH_PARENS) {
    _set(this.state, 'queryNode.pillsData', pD);
    return this;
  }

  pillsDataWithEmptyParens(pD = PILLS_WITH_EMPTY_PARENS) {
    _set(this.state, 'queryNode.pillsData', pD);
    return this;
  }

  pillsDataVaried(pD) {
    if (!pD) {
      pD = [...DEFAULT_PILLS_DATA, ...COMPLEX_PILL_DATA, ...TEXT_PILL_DATA];
    }
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

  markFocused(pillIds = []) {
    this.state.queryNode.pillsData = this.state.queryNode.pillsData.map((pD) => {
      if (pillIds.includes(pD.id)) {
        pD = {
          ...pD,
          isFocused: true
        };
      }

      return pD;
    });
    return this;
  }

  markValidationInProgress(pillIds = []) {
    this.state.queryNode.pillsData = this.state.queryNode.pillsData.map((pD) => {
      if (pillIds.includes(pD.id)) {
        pD = {
          ...pD,
          isValidationInProgress: true
        };
      }

      return pD;
    });
    return this;
  }

  isQueryRunning(flag = true) {
    _set(this.state, 'queryNode.isQueryRunning', flag);
    return this;
  }

  isQueryExecutedBySort(flag = true) {
    _set(this.state, 'data.isQueryExecutedBySort', flag);
    return this;
  }

  isQueryExecutedByColumnGroup(flag = true) {
    _set(this.state, 'data.isQueryExecutedByColumnGroup', flag);
    return this;
  }

  withPreviousQuery(
    metaFilter = DEFAULT_PILLS_DATA,
    serviceId = '123',
    startTime = 1505672580000,
    endTime = 1505672580000
  ) {
    _set(this.state, 'queryNode.previousQueryParams', {
      serviceId,
      startTime,
      endTime,
      metaFilter
    });
    return this;
  }

  queryStats(description = 'foo') {
    _set(this.state, 'queryStats', {
      description,
      isConsoleOpen: false,
      percent: 0,
      errors: [],
      warnings: [],
      devices: []
    });
    return this;
  }

  queryStatsIsPartiallyComplete() {
    _set(this.state, 'eventResults.status', 'streaming');
    _set(this.state, 'eventResults.data', ['foo']);
    _set(this.state, 'eventResults.streamLimit', 100);
    _set(this.state, 'eventCount.data', 100);
    return this;
  }

  querySorting() {
    _set(this.state, 'eventResults.data', [{}]);
    _set(this.state, 'eventResults.status', 'sorting');
    _set(this.state, 'eventResults.data', []);
    return this;
  }

  queryCanceledWithResults() {
    _set(this.state, 'eventResults.status', 'canceled');
    _set(this.state, 'eventResults.data', ['fooo']);
    _set(this.state, 'eventCount.data', 100);
    return this;
  }

  queryCanceledWithNoResults() {
    _set(this.state, 'eventResults.status', 'canceled');
    _set(this.state, 'eventResults.data', []);
    return this;
  }

  queryStatsIsRetrieving() {
    _set(this.state, 'eventResults.status', 'streaming');
    _set(this.state, 'eventResults.data', ['foo']);
    _set(this.state, 'eventResults.streamLimit', 100);
    _set(this.state, 'eventCount.data', 100);
    _set(this.state.queryStats, 'percent', 100);
    _set(this.state.queryStats, 'devices', [{ serviceId: '1', on: true, elapsedTime: 2 }]);
    _set(this.state.queryStats, 'streamingStartedTime', 1505672582000);
    return this;
  }

  queryStatsIsComplete() {
    _set(this.state, 'eventResults.status', 'streaming');
    _set(this.state, 'eventResults.data', ['foo']);
    _set(this.state, 'eventResults.streamLimit', 100);
    _set(this.state, 'eventCount.data', 100);
    _set(this.state, 'queryStats.percent', 100);
    _set(this.state, 'queryStats.devices', [{ serviceId: '1', on: true, elapsedTime: 2 }]);
    _set(this.state, 'queryStats.queryStartedTime', 1505672580000);
    _set(this.state, 'queryStats.queryEndedTime', 1505672581000);
    _set(this.state, 'queryStats.streamingStartedTime', 1505672582000);
    _set(this.state, 'queryStats.streamingEndedTime', 1505672583000);
    return this;
  }

  queryStatsNoTime() {
    _set(this.state.queryStats, 'percent', 100);
    _set(this.state.queryStats, 'devices', [{ serviceId: '1', on: true }]);
    _set(this.state.queryStats, 'queryStartedTime', 1505672580000);
    _set(this.state.queryStats, 'queryEndedTime', 1505672581000);
    return this;
  }

  isMixedMode() {
    _set(this.state, 'queryStats.devices', [
      {
        serviceId: 'doesNotExist',
        on: true,
        elapsedTime: 2
      }
    ]);
    return this;
  }

  queryStatsWithHierarcy() {
    _set(this.state.queryStats, 'devices', [
      {
        serviceId: '1',
        on: true,
        elapsedTime: 2,
        devices: [
          {
            serviceId: '1',
            on: true,
            elapsedTime: 2
          }
        ]
      }
    ]);
    return this;
  }

  queryStatsWithOffline() {
    _set(this.state, 'queryStats', {
      devices: [{ serviceId: '1', on: false }, ...this.state.queryStats.devices]
    });
    return this;
  }

  queryStatsWithMultipleOffline() {
    _set(this.state, 'queryStats', {
      devices: [{ serviceId: '1', on: false }, { serviceId: '1', on: false }, ...this.state.queryStats.devices]
    });
    return this;
  }

  queryStatsHasWarning() {
    _set(this.state.queryStats, 'description', 'warning');
    _set(this.state.queryStats, 'warnings', [{
      serviceId: '1',
      warning: 'warning'
    }]);
    return this;
  }

  queryIsQueued() {
    _set(this.state.queryStats, 'description', 'queued');
    _set(this.state.queryStats, 'percent', 0);
    return this;
  }

  queryStatsHasError() {
    _set(this.state.queryStats, 'errors', [{
      serviceId: '1',
      error: 'error'
    }]);
    return this;
  }

  queryStatsHasErrorWithoutId() {
    _set(this.state.queryStats, 'errors', [{
      error: 'error'
    }]);
    return this;
  }

  queryStatsIsEmpty() {
    _set(this.state.queryStats, 'description', null);
    return this;
  }

  queryStatsIsOpen() {
    _set(this.state.queryStats, 'isConsoleOpen', true);
    return this;
  }

  recentQueriesCallInProgress(flag) {
    if (flag) {
      _set(this.state, 'queryNode.recentQueriesCallInProgress', flag);
    } else {
      _set(this.state, 'queryNode.recentQueriesCallInProgress', false);
    }
    return this;
  }

  recentQueriesFilterText(text) {
    _set(this.state, 'queryNode.recentQueriesFilterText', text);
    return this;
  }

  recentQueriesFilteredList() {
    const recentQueriesArray = getRecentQueryObjects(defaultRecentQueriesFilteredList);
    _set(this.state, 'queryNode.recentQueriesFilteredList', recentQueriesArray);
    return this;
  }

  recentQueriesUnfilteredList() {
    const recentQueriesArray = getRecentQueryObjects(defaultRecentQueriesUnfilteredList);
    _set(this.state, 'queryNode.recentQueriesUnfilteredList', recentQueriesArray);
    return this;
  }

  populateValueSuggestions(list = DEFAULT_VALUE_SUGGESTIONS) {
    _set(this.state, 'queryNode.valueSuggestions', list);
    return this;
  }
}
