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
}
