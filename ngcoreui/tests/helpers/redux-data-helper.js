import Immutable from 'seamless-immutable';
import * as TREE_CONTENTS from './tree-contents';
import sysResponse from './sys-response';
import sysStatsResponse from './sys-stats-response';
import operationsResponse from './operations-response';
import paramHelpResponse from './param-help-response';
import {
  updateResponseChange,
  updateResponseAdd,
  updateResponseDelete
} from './update-responses';

export {
  sysResponse,
  sysStatsResponse,
  operationsResponse,
  updateResponseChange,
  updateResponseAdd,
  updateResponseDelete,
  paramHelpResponse
};

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
  constructor() {
    this.state = {};
  }

  build() {
    return Immutable.from(this.state);
  }

  disconnected() {
    _set(this.state, 'shared.wsConnecting', false);
    _set(this.state, 'shared.wsConnected', false);
    _set(this.state, 'shared.wsErr', null);
    _set(this.state, 'shared.activeTab', null);
    return this;
  }

  connecting() {
    _set(this.state, 'shared.wsConnecting', true);
    _set(this.state, 'shared.wsConnected', false);
    _set(this.state, 'shared.wsErr', null);
    _set(this.state, 'shared.activeTab', null);
    return this;
  }

  connected() {
    _set(this.state, 'shared.wsConnecting', false);
    _set(this.state, 'shared.wsConnected', true);
    _set(this.state, 'shared.wsErr', null);
    _set(this.state, 'shared.treePath', '/');
    _set(this.state, 'shared.treeSelectedOperationIndex', -1);
    _set(this.state, 'shared.treeOperationParams', {});
    _set(this.state, 'shared.deviceInfo', {});
    _set(this.state, 'shared.username', null);
    _set(this.state, 'shared.availablePermissions', null);
    _set(this.state, 'shared.operationResponse', null);
    _set(this.state, 'shared.responseExpanded', false);
    _set(this.state, 'shared.operationManualVisible', true);
    _set(this.state, 'shared.activeTab', null);
    _set(this.state, 'shared.selectedNode', null);
    _set(this.state, 'shared.logs', null);
    _set(this.state, 'shared.logsFilterChangePending', false);
    _set(this.state, 'shared.logsLoading', false);
    _set(this.state, 'shared.logsLastLoaded', '0');
    _set(this.state, 'shared.logsIntervalHandle', null);
    return this;
  }

  wsErr(err = 'WebSocket error') {
    _set(this.state, 'shared.wsErr', err);
    return this;
  }

  treePath(path) {
    _set(this.state, 'shared.treePath', path);
    return this;
  }

  treePathContentsEmpty() {
    _set(this.state, 'shared.treePathContents', {});
    return this;
  }

  treePathContentsStandard() {
    _set(this.state, 'shared.treePathContents', TREE_CONTENTS.STANDARD);
    return this;
  }

  treeSelectedOperationIndex(index) {
    _set(this.state, 'shared.treeSelectedOperationIndex', index);
    return this;
  }

  availablePermissions(permissions) {
    _set(this.state, 'shared.availablePermissions', permissions);
    return this;
  }

  operationResponse(response) {
    _set(this.state, 'shared.operationResponse', response);
    return this;
  }

  responseExpanded(bool) {
    _set(this.state, 'shared.responseExpanded', bool);
    return this;
  }

  operationManualVisible(bool) {
    _set(this.state, 'shared.operationManualVisible', bool);
    return this;
  }

  responseAsJson(bool) {
    _set(this.state, 'shared.responseAsJson', bool);
    return this;
  }

  selectedNode(value) {
    _set(this.state, 'shared.selectedNode', value);
    return this;
  }

  selectedNodeConfigSetResult(result) {
    _set(this.state, 'shared.selectedNode.configSetResult', result);
    return this;
  }

  release(string) {
    _set(this.state, 'shared.deviceInfo.release', string);
    return this;
  }

  module(string) {
    _set(this.state, 'shared.deviceInfo.module', string);
    return this;
  }

  logs(arr) {
    _set(this.state, 'shared.logs', arr);
    return this;
  }

  logsFilterChangePending(bool) {
    _set(this.state, 'shared.logsFilterChangePending', bool);
    return this;
  }

  logsLoading(bool) {
    _set(this.state, 'shared.logsLoading', bool);
    return this;
  }

  // Selector functions

  _connectAndFillTree() {
    return this.connected().treePathContentsStandard();
  }

  currentDirectoryContents() {
    return this._connectAndFillTree();
  }

  isNotRoot() {
    return this._connectAndFillTree();
  }

  pathParent() {
    return this._connectAndFillTree();
  }

  pathToUrlSegment() {
    return this._connectAndFillTree();
  }

  pathParentToUrlSegment() {
    return this._connectAndFillTree();
  }

  operationNames() {
    return this._connectAndFillTree();
  }

  selectedOperation() {
    return this._connectAndFillTree();
  }

  filteredOperationNames() {
    return this._connectAndFillTree();
  }

  selectedOperationHelp() {
    return this._connectAndFillTree();
  }

  selectedOperationManual() {
    return this._connectAndFillTree();
  }

  selectedOperationRoles() {
    return this._connectAndFillTree();
  }

  selectedOperationHasPermission() {
    return this._connectAndFillTree();
  }

  responses() {
    return this._connectAndFillTree();
  }

  operationResponseDataType() {
    return this._connectAndFillTree();
  }

  description() {
    return this._connectAndFillTree();
  }

  liveSelectedNode() {
    return this._connectAndFillTree();
  }

  configSetResult() {
    return this._connectAndFillTree();
  }

  selectedIsConfigNode() {
    return this._connectAndFillTree();
  }

  selectedIsStatNode() {
    return this._connectAndFillTree();
  }

  selectedNodeRequiresRestart() {
    return this._connectAndFillTree();
  }

  isDevelopmentBuild() {
    return this._connectAndFillTree();
  }

  isDecoder() {
    return this._connectAndFillTree();
  }

  hasNoAggPermission() {
    return this._connectAndFillTree();
  }

  hasNoCapturePermission() {
    return this._connectAndFillTree();
  }

  hasNoShutdownPermission() {
    return this._connectAndFillTree();
  }
}
