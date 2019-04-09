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
    _set(this.state, 'wsConnecting', false);
    _set(this.state, 'wsConnected', false);
    _set(this.state, 'wsErr', null);
    _set(this.state, 'activeTab', null);
    return this;
  }

  connecting() {
    _set(this.state, 'wsConnecting', true);
    _set(this.state, 'wsConnected', false);
    _set(this.state, 'wsErr', null);
    _set(this.state, 'activeTab', null);
    return this;
  }

  connected() {
    _set(this.state, 'wsConnecting', false);
    _set(this.state, 'wsConnected', true);
    _set(this.state, 'wsErr', null);
    _set(this.state, 'treePath', '/');
    _set(this.state, 'treeSelectedOperationIndex', -1);
    _set(this.state, 'treeOperationParams', {});
    _set(this.state, 'deviceInfo', {});
    _set(this.state, 'username', null);
    _set(this.state, 'availablePermissions', null);
    _set(this.state, 'operationResponse', null);
    _set(this.state, 'responseExpanded', false);
    _set(this.state, 'activeTab', null);
    _set(this.state, 'selectedNode', null);
    _set(this.state, 'logs', null);
    _set(this.state, 'logsFilterChangePending', false);
    _set(this.state, 'logsLoading', false);
    _set(this.state, 'logsLastLoaded', '0');
    _set(this.state, 'logsIntervalHandle', null);
    return this;
  }

  wsErr(err = 'WebSocket error') {
    _set(this.state, 'wsErr', err);
    return this;
  }

  treePath(path) {
    _set(this.state, 'treePath', path);
    return this;
  }

  treePathContentsEmpty() {
    _set(this.state, 'treePathContents', {});
    return this;
  }

  treePathContentsStandard() {
    _set(this.state, 'treePathContents', TREE_CONTENTS.STANDARD);
    return this;
  }

  treeSelectedOperationIndex(index) {
    _set(this.state, 'treeSelectedOperationIndex', index);
    return this;
  }

  availablePermissions(permissions) {
    _set(this.state, 'availablePermissions', permissions);
    return this;
  }

  operationResponse(response) {
    _set(this.state, 'operationResponse', response);
    return this;
  }

  responseExpanded(bool) {
    _set(this.state, 'responseExpanded', bool);
    return this;
  }

  responseAsJson(bool) {
    _set(this.state, 'responseAsJson', bool);
    return this;
  }

  selectedNode(value) {
    _set(this.state, 'selectedNode', value);
    return this;
  }

  selectedNodeConfigSetResult(result) {
    _set(this.state, 'selectedNode.configSetResult', result);
    return this;
  }

  release(string) {
    _set(this.state, 'deviceInfo.release', string);
    return this;
  }

  module(string) {
    _set(this.state, 'deviceInfo.module', string);
    return this;
  }

  logs(arr) {
    _set(this.state, 'logs', arr);
    return this;
  }

  logsFilterChangePending(bool) {
    _set(this.state, 'logsFilterChangePending', bool);
    return this;
  }

  logsLoading(bool) {
    _set(this.state, 'logsLoading', bool);
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
