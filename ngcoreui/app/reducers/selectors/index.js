import { createSelector } from 'reselect';
import pathParentHelper from './path-parent';
import pathToUrlSegmentHelper from './path-to-url-segment';
import { isFlag, FLAGS } from 'ngcoreui/services/transport/flag-helper';

const _treePath = (state) => state.treePath;
const _treePathContents = (state) => state.treePathContents;
const _treeSelectedOperationIndex = (state) => state.treeSelectedOperationIndex;
const _selectedNode = (state) => state.selectedNode;
const _deviceInfo = (state) => state.deviceInfo;
const _availablePermissions = (state) => state.availablePermissions;
const _operationResponse = (state) => state.operationResponse;

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

const pathToUrlSegment = createSelector(
  [ _treePath ],
  (treePath) => {
    return pathToUrlSegmentHelper(treePath);
  }
);

const pathParentToUrlSegment = createSelector(
  [ _treePath ],
  (treePath) => {
    return pathToUrlSegmentHelper(pathParentHelper(treePath));
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

const _selectedOperationDescription = createSelector(
  [ selectedOperation ],
  (selectedOperation) => {
    if (selectedOperation && selectedOperation.description) {
      let { description } = selectedOperation;
      // Leverage the HTML escaping functionality of the textarea element
      escape.textContent = description;
      description = escape.innerHTML;
      return description;
    }
    return '';
  }
);

const selectedOperationHelp = createSelector(
  [ _selectedOperationDescription ],
  (_selectedOperationDescription) => {
    return _selectedOperationDescription.split('\n')[0];
  }
);

const selectedOperationManual = createSelector(
  [ selectedOperation ],
  (selectedOperation) => {
    return selectedOperation ? selectedOperation.manual : null;
  }
);

const selectedOperationRoles = createSelector(
  [ _selectedOperationDescription ],
  (_selectedOperationDescription) => {
    if (_selectedOperationDescription.length > 0) {
      const [, roleString ] = _selectedOperationDescription.split('\n');
      if (!roleString) {
        return [];
      }
      const getRoles = new RegExp('security.roles: (.*)');
      let results = getRoles.exec(roleString);
      // There shouldn't be more than one security role but we handle it just in case
      results = results[1];
      if (results) {
        results = results.replace('&amp;', ',')
          .split(',')
          .map((result) => {
            return result.trim();
          });
      } else {
        results = [];
      }
      return results.filter((role) => {
        return role !== 'everyone';
      });
    } else {
      return [];
    }
  }
);

const selectedOperationHasPermission = createSelector(
  [ selectedOperationRoles, _availablePermissions ],
  (selectedOperationRoles, _availablePermissions) => {
    return selectedOperationRoles.every((role) => {
      return _availablePermissions.indexOf(role) >= 0;
    });
  }
);

const responses = createSelector(
  [ _operationResponse ],
  (operationResponse) => {
    operationResponse = operationResponse || {};
    return {
      ...operationResponse,
      progress: operationResponse.progress ? `${operationResponse.progress}% ` : null,
      status: operationResponse.status ? `${operationResponse.status}...` : null,
      hasError: !!operationResponse.error,
      hasPendingOperation: operationResponse.complete === false
    };
  }
);

const operationResponseDataType = createSelector(
  [ _operationResponse ],
  (operationResponse) => {
    if (operationResponse && operationResponse.dataType) {
      const { dataType } = operationResponse;
      return {
        string: dataType === FLAGS.STRING,
        params: dataType === FLAGS.PARAMS,
        paramList: dataType === FLAGS.PARAM_LIST,
        queryResults: dataType === FLAGS.QUERY_RESULTS,
        nodeInfo: dataType === FLAGS.NODE_INFO,
        nodeList: dataType === FLAGS.NODE_LIST
      };
    } else {
      return null;
    }
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
const isLogCollector = _isModule('logcollector');

export {
  currentDirectoryContents,
  isNotRoot,
  pathParent,
  pathToUrlSegment,
  pathParentToUrlSegment,
  operationNames,
  filteredOperationNames,
  selectedOperation,
  selectedOperationHelp,
  selectedOperationManual,
  selectedOperationRoles,
  selectedOperationHasPermission,
  responses,
  operationResponseDataType,
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
  isLogCollector,
  moduleName
};
