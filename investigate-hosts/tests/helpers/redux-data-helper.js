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
      endpoint: this.state,
      preferences: {
        preferences: {
          machinePreference: {
            visibleColumns: [
              'id',
              'machineIdentity.agentVersion',
              'machine.scanStartTime',
              'machineIdentity.machineOsType'
            ]
          }
        }
      },
      endpointServer: this.state.endpointServer,
      endpointQuery: this.state.endpointQuery,
      investigate: this.state.investigate
    });
    this.setState(state);
    return state.asMutable();
  }

  schema(schema) {
    _set(this.state, 'schema.schema', schema);
    return this;
  }

  columns(schema) {
    this.schema(schema);
    return this;
  }

  hostList(hostList) {
    _set(this.state, 'machines.hostList', hostList);
    return this;
  }
  selectedHostList(selected) {
    _set(this.state, 'machines.selectedHostList', selected);
    return this;
  }
  hostSortField(field) {
    _set(this.state, 'machines.hostColumnSort', field);
    return this;
  }
  visibleColumns(columns) {
    _set(this.state, 'schema.visibleColumns', columns);
    return this;
  }

  updateFilterExpressionList(expressionList) {
    _set(this.state, 'filter.expressionList', expressionList);
    return this;
  }

  updateFilterSchems(schema) {
    _set(this.state, 'filter.schemas', schema);
    return this;
  }
  lastFilterAdded() {
    _set(this.state, 'filter.lastFilterAdded', null);
    return this;
  }

  hasMachineId(id) {
    _set(this.state, 'detailsInput.agentId', id);
    return this;
  }

  columnsConfig(osType) {
    const hostDetails = { machine: { machineOsType: osType } };
    _set(this.state, 'overview', { hostDetails });
    return this;
  }

  // host Files
  filesLoadMoreStatus(status) {
    _set(this.state, 'hostFiles.filesLoadMoreStatus', status);
    return this;
  }
  files(items) {
    _set(this.state, 'hostFiles.files', items);
    return this;
  }
  totalItems(fileCount) {
    _set(this.state, 'hostFiles.totalItems', fileCount);
    return this;
  }
  selectedFileList(data) {
    _set(this.state, 'hostFiles.selectedFileList', data);
    return this;
  }
  fileStatusData(data) {
    _set(this.state, 'hostFiles.fileStatusData', data);
    return this;
  }
  // Host drivers
  drivers(drivers) {
    _set(this.state, 'drivers', drivers);
    return this;
  }
  selectedDriverList(selectedDrivers) {
    _set(this.state, 'drivers.selectedDriverList', selectedDrivers);
    return this;
  }
  driverStatusData(data) {
    _set(this.state, 'hostFiles.driverStatusData', data);
    return this;
  }
  fileContextSelections(fileContextSelections) {
    _set(this.state, 'drivers.fileContextSelections', fileContextSelections);
    return this;
  }

  // Host Details
  selectedTabComponent(tabName) {
    _set(this.state, 'visuals.activeHostDetailTab', tabName);
    return this;
  }
  fileAnalysis(fileAnalysis) {
    _set(this.state, 'fileAnalysis', fileAnalysis);
    return this;
  }
  isSnapshotsAvailable(flag) {
    _set(this.state, 'detailsInput.snapShots', flag ? [0, 1] : []);
    return this;
  }
  isSnapshotsLoading(flag) {
    _set(this.state, 'detailsInput.isSnapshotsLoading', flag);
    return this;
  }
  dllList(dllData) {
    _set(this.state, 'process.dllList', dllData);
    return this;
  }
  selectedProcessId(processId) {
    _set(this.state, 'process.selectedProcessId', processId);
    return this;
  }
  selectedProcessList(process) {
    _set(this.state, 'process.selectedProcessList', process);
    return this;
  }
  host(host) {
    _set(this.state, 'overview.hostDetails', host);
    return this;
  }
  hostName(value) {
    const machineIdentity = { machineName: value };
    _set(this.state, 'overview.hostOverview', { machineIdentity });
    return this;
  }
  isProcessTreeLoading(value) {
    _set(this.state, 'process.isProcessTreeLoading', value);
    return this;
  }
  processList(value) {
    _set(this.state, 'process.processList', value);
    return this;
  }
  machineOSType(value) {
    _set(this.state, 'overview.hostOverview.machineIdentity.machineOsType', value);
    return this;
  }
  registryDiscrepancies(value) {
    _set(this.state, 'overview.hostDetails.machine', value);
    return this;
  }
  machineIdentity(value) {
    _set(this.state, 'overview.hostDetails.machineIdentity', value);
    return this;
  }
  processTree(value) {
    _set(this.state, 'process.processTree', value);
    return this;
  }
  selectedTab(tabName) {
    _set(this.state, 'explore.selectedTab', tabName);
    return this;
  }
  sortField(field) {
    _set(this.state, 'process.sortField', field);
    return this;
  }
  isDescOrder(sortOrder) {
    _set(this.state, 'process.isDescOrder', sortOrder);
    return this;
  }
  snapShot(snapShot) {
    _set(this.state, 'detailsInput.snapShots', snapShot);
    return this;
  }
  scanTime(time) {
    _set(this.state, 'detailsInput.scanTime', time);
    return this;
  }
  agentId(id) {
    _set(this.state, 'detailsInput.agentId', id);
    return this;
  }
  fileSearchResults(data) {
    _set(this.state, 'explore.fileSearchResults', data.fileSearchResults);
    return this;
  }
  searchStatus(statusValue) {
    _set(this.state, 'explore.searchStatus', statusValue);
    return this;
  }
  searchValue(value) {
    _set(this.state, 'explore.searchValue', value);
    return this;
  }
  componentName(componentName) {
    _set(this.state, 'explore.componentName', componentName);
    return this;
  }
  showSearchResults(showSearchResults) {
    _set(this.state, 'explore.showSearchResults', showSearchResults);
    return this;
  }
  exploreData(explore) {
    _set(this.state, 'explore', explore);
    return this;
  }
  agentVersion(version) {
    this.host({
      machine: {
        agentVersion: `${version}`
      }
    });
    return this;
  }

  isJsonExportCompleted(status) {
    _set(this.state, 'overview.exportJSONStatus', status ? 'completed' : 'streaming');
    return this;
  }

  scanCount(count) {
    _set(this.state, 'machines.selectedHostList', new Array(count));
    return this;
  }

  totalHostItems(fileCount) {
    _set(this.state, 'machines.totalItems', fileCount);
    return this;
  }

  processDetails(data) {
    _set(this.state, 'process.processDetails', data);
    return this;
  }
  searchResultProcessList(data) {
    _set(this.state, 'process.searchResultProcessList', data);
    return this;
  }

  processDetailsLoading(flag) {
    _set(this.state, 'process.processDetailsLoading', flag);
    return this;
  }

  isTreeView(flag) {
    _set(this.state, 'visuals.isTreeView', flag);
    return this;
  }

  hostExportStatus(exportStatus) {
    _set(this.state, 'machines.hostExportStatus', exportStatus);
    return this;
  }

  lookupData(data) {
    _set(this.state, 'visuals.lookupData', data);
    return this;
  }

  activeHostPropertyTab(hostPropertyTab) {
    _set(this.state, 'visuals.activeHostPropertyTab', hostPropertyTab);
    return this;
  }

  setContextError(value) {
    _set(this.state, 'visuals.contextError', value);
    return this;
  }

  policy(data) {
    _set(this.state, 'overview.policyDetails', data);
    return this;
  }

  anomalies(data) {
    _set(this.state, 'anomalies', data);
    return this;
  }

  isEndpointServerOffline(status) {
    _set(this.state, 'endpointServer.isSummaryRetrieveError', status);
    return this;
  }
  endpointServer(status) {
    _set(this.state, 'endpointServer', status);
    return this;
  }
  endpointQuery(status) {
    _set(this.state, 'endpointQuery', status);
    return this;
  }
  serviceId(id) {
    _set(this.state, 'investigate.serviceId', id);
    return this;
  }

  timeRange(range) {
    _set(this.state, 'investigate.timeRange', range);
    return this;
  }
  services(data) {
    _set(this.state, 'endpointServer', data);
    return this;
  }
  isDetailRightPanelVisible(value) {
    _set(this.state, 'detailsInput.isDetailRightPanelVisible', value);
    return this;
  }
  isProcessDetailsView(value) {
    _set(this.state, 'visuals.isProcessDetailsView', value);
    return this;
  }
  isDataTruncated(flag) {
    _set(this.state, 'explore.isDataTruncated', flag);
    return this;
  }
  setActiveHostDetailPropertyTab(value) {
    _set(this.state, 'detailsInput.activeHostDetailPropertyTab', value);
    return this;
  }
  setFocusedHost(value) {
    _set(this.state, 'machines.focusedHost', value);
    return this;
  }
}
