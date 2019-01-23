import { createSelector } from 'reselect';
import pathParentHelper from './path-parent';
import { isFlag, FLAGS } from 'ngcoreui/services/transport/flag-helper';

const _treePath = (state) => state.treePath;
const _treePathContents = (state) => state.treePathContents;
const _treeSelectedOperationIndex = (state) => state.treeSelectedOperationIndex;
const _selectedNode = (state) => state.selectedNode;
const _deviceInfo = (state) => state.deviceInfo;

const filterOut = ['ls', 'mon', 'stopMon', 'count', 'help', 'info'];

const escape = document.createElement('textarea');

const currentDirectoryContents = createSelector(
  [ _treePathContents ],
  (treePathContents) => {
    if (treePathContents && treePathContents.nodes) {
      return treePathContents.nodes;
    } else {
      return [];
    }
  }
);

const isNotRoot = createSelector(
  [ _treePath ],
  (treePath) => treePath !== '/'
);

const pathParent = createSelector(
  [ _treePath ],
  (treePath) => {
    return pathParentHelper(treePath);
  }
);

const _operations = createSelector(
  [ _treePathContents ],
  (treePathContents) => {
    if (treePathContents) {
      return treePathContents.operations || [];
    } else {
      return [];
    }
  }
);

const operationNames = createSelector(
  [ _operations ],
  (operations) => {
    return operations.map((op) => {
      return op.name;
    });
  }
);

const filteredOperationNames = createSelector(
  [ operationNames ],
  (operationNames) => {
    return operationNames.filter((name) => {
      return filterOut.indexOf(name) < 0;
    });
  }
);

const selectedOperation = createSelector(
  [ _treeSelectedOperationIndex, _operations ],
  (index, operations) => {
    return index >= 0 ? operations[index] : null;
  }
);

const selectedOperationHelp = createSelector(
  [ selectedOperation ],
  (selectedOperation) => {
    if (selectedOperation && selectedOperation.description) {
      let { description } = selectedOperation;
      // Leverage the HTML escaping functionality of the textarea element
      escape.textContent = description;
      description = escape.innerHTML;
      // Only the first line is the actual description
      [ description ] = description.split('\n');
      return description;
    }
    return '';
  }
);

const description = createSelector(
  [ _treePathContents ],
  (treePathContents) => {
    return treePathContents ? treePathContents.description || null : null;
  }
);

const liveSelectedNode = createSelector(
  [ _selectedNode, currentDirectoryContents],
  (selectedNode, currentDirectoryContents) => {
    if (selectedNode) {
      return currentDirectoryContents.find((node) => {
        return selectedNode.path === node.path;
      });
    }
    return null;
  }
);

const configSetResult = createSelector(
  [ _selectedNode ],
  (selectedNode) => {
    if (selectedNode) {
      return 'configSetResult' in selectedNode ? selectedNode.configSetResult : null;
    }
    return null;
  }
);

const selectedIsConfigNode = createSelector(
  [ _selectedNode ],
  (selectedNode) => {
    if (selectedNode) {
      return isFlag(selectedNode.nodeType, FLAGS.CONFIG_NODE);
    }
    return false;
  }
);

const selectedIsStatNode = createSelector(
  [ _selectedNode ],
  (selectedNode) => {
    if (selectedNode) {
      return isFlag(selectedNode.nodeType, FLAGS.STAT_NODE);
    }
    return false;
  }
);

const selectedNodeRequiresRestart = createSelector(
  [ liveSelectedNode ],
  (liveSelectedNode) => {
    if (liveSelectedNode) {
      return isFlag(liveSelectedNode.nodeType, FLAGS.CONFIG_VALUE_RESTART_NEEDED);
    }
    return false;
  }
);

const moduleName = createSelector(
  [ _deviceInfo ],
  (deviceInfo) => {
    return deviceInfo ? deviceInfo.module : null;
  }
);

const _release = createSelector(
  [ _deviceInfo ],
  (deviceInfo) => {
    return deviceInfo ? deviceInfo.release : null;
  }
);

const isDevelopmentBuild = createSelector(
  [ _release ],
  (release) => {
    return release ? release === '0.0.0' : false;
  }
);

const _isModule = (_mod) => {
  return createSelector(
    [ moduleName ],
    (mod) => {
      return mod ? mod === _mod : false;
    }
  );
};

const isArchiver = _isModule('archiver');
const isBroker = _isModule('broker');
const isConcentrator = _isModule('concentrator');
const isDecoder = _isModule('decoder');
const isLogDecoder = _isModule('logdecoder');

export {
  currentDirectoryContents,
  isNotRoot,
  pathParent,
  operationNames,
  filteredOperationNames,
  selectedOperation,
  selectedOperationHelp,
  description,
  liveSelectedNode,
  configSetResult,
  selectedIsConfigNode,
  selectedIsStatNode,
  selectedNodeRequiresRestart,
  isDevelopmentBuild,
  isArchiver,
  isBroker,
  isConcentrator,
  isDecoder,
  isLogDecoder,
  moduleName
};
