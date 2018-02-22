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
              'machine.agentVersion',
              'machine.scanStartTime',
              'machine.machineOsType'
            ]
          }
        }
      }
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

  // Host Details
  selectedTabComponent(tabName) {
    _set(this.state, 'visuals.activeHostDetailTab', tabName);
    return this;
  }
  isSnapshotsAvailable(flag) {
    _set(this.state, 'detailsInput.snapShots', flag ? [0, 1] : []);
    return this;
  }
  hostDetailsLoading(flag) {
    _set(this.state, 'visuals.hostDetailsLoading', flag);
    return this;
  }
  dllList(dllData) {
    _set(this.state, 'process.dllList', dllData);
    return this;
  }
  host(host) {
    _set(this.state, 'overview.hostDetails', host);
    return this;
  }
  hostName(value) {
    const machine = { machineName: value };
    _set(this.state, 'overview.hostDetails', { machine });
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

  showNonEmptyProperty(flag) {
    _set(this.state, 'detailsInput.showNonEmptyProperty', flag);
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
}
