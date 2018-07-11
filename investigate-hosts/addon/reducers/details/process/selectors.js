import reselect from 'reselect';
import { convertTreeToList } from './util';
import _ from 'lodash';

const { createSelector } = reselect;
const _processData = (state) => state.endpoint.process.processDetails;
const _treeData = (state) => state.endpoint.process.processTree;
const _listData = (state) => state.endpoint.process.processList;
const _dllData = (state) => state.endpoint.process.dllList;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;
const _processDetailsLoading = (state) => state.endpoint.process.processDetailsLoading;
const _isProcessTreeLoading = (state) => state.endpoint.process.isProcessTreeLoading;
const _hostDetails = (state) => state.endpoint.overview.hostDetails || {};
const _selectedProcessId = (state) => state.endpoint.process.selectedProcessId;

const _getTree = (selectedTab, tabName, data) => {
  if (selectedTab && selectedTab.tabName === tabName) {
    return data.map((d) => {
      if (d.checksumSha256 === selectedTab.checksum) {
        return d;
      }
      if (d.childProcesses) {
        const children = _getChildProcesses(d, selectedTab.checksum);
        if (children.length) {
          return {
            ...d,
            childProcesses: children
          };
        }
      }
    }).filter((d) => d);
  } else {
    return data;
  }
};

/**
 * get the matching child processess for a parent process.
 * @private
 * @param {Object} d
 * @param {String} checksum
 */
const _getChildProcesses = (d, checksum) => {
  return d.childProcesses.map((child) => {
    if (child.checksumSha256 === checksum) {
      return child;
    } else {
      if (child.childProcesses) {
        const children = _getChildProcesses(child, checksum);
        if (children.length) {
          return {
            ...child,
            childProcesses: children
          };
        }
      }
    }
  }).filter((d) => d);
};

export const getProcessData = createSelector(
  [ _processData ],
  (processData) => {
    if (processData) {
      const osType = processData.machineOsType;
      if (osType) {
        const { processes: [ process ] } = processData[osType];
        const clonedProcess = { ...process }; // Cloning the process as we need to add new property
        const { fileProperties: { signature } } = processData;
        if (signature) {
          clonedProcess.signature = signature.signer ? `${signature.features},${signature.signer}` : signature.features;
        }
        return { ...processData, process: clonedProcess };
      }
    }
    return {};
  }
);

const _getProcessList = createSelector(
  [_listData, _treeData, _selectedTab],
  (listData, treeData, selectedTab) => {
    if (!listData || !treeData) {
      return null;
    }
    const tabName = 'PROCESS';
    const tree = _getTree(selectedTab, tabName, treeData.asMutable());
    return {
      list: listData,
      tree
    };
  }
);

/* Processes data for loaded libraries also removes floating code from the list */
export const enrichedDllData = createSelector(
  [_dllData],
  (dllData) => {
    if (dllData && dllData.length > 0) {
      const newDallData = dllData.map((dll) => {
        if (dll.fileProperties) {
          const { signature } = dll.fileProperties;
          if (signature) {
            const signatureText = signature.signer ? `${signature.features},${signature.signer}` : signature.features;
            return { ...dll, signature: signatureText };
          }
          return dll;
        }
      }).filter((item) => item);
      return newDallData;
    }
    return [];
  }
);


/**
 * Convert tree json into flat objects with level for each row, `level` is used the position the column name, which looks
 * like tree structure. Also setting `hasChild` property to indicate the row has the child or not
 * @param items
 * @public
 */
export const processTree = createSelector(
  [_getProcessList],
  (processList) => {
    if (processList && processList.tree.length) {
      const { tree } = processList;
      const rows = tree.map(convertTreeToList);
      return _.flatten(rows);
    } else {
      return [];
    }
  }
);

export const processList = createSelector(
  [_getProcessList],
  (processList) => processList && processList.list.length ? processList.list : []
);

export const isNavigatedFromExplore = createSelector(
  [_selectedTab],
  (selectedTab) => selectedTab && selectedTab.tabName === 'PROCESS'
);

export const isProcessLoading = createSelector(
  [ _processDetailsLoading, _isProcessTreeLoading ],
  (processDetailsLoading, isProcessTreeLoading) => isProcessTreeLoading || processDetailsLoading
);

export const noProcessData = createSelector(processTree, (tree) => !tree.length);

export const isJazzAgent = createSelector(
  _hostDetails,
  (hostDetails) => {
    if (hostDetails && hostDetails.machineIdentity && hostDetails.machine) {
      const { machine: { agentVersion }, machineIdentity: { agentMode } } = hostDetails;
      return (agentVersion && agentVersion.startsWith('11.2')) && (agentMode === 'userModeOnly');
    }
  });

/*
  Fetches all the dllList items that have hooks.
  Then filters out the relevent hooks based on PID and then
  process each hook into the required format.
  input : dllList, selected process Id
  output : [{
              dllFileName,
              type,
              hookFileName,
              symbol
  },..]
*/
export const imageHooksData = createSelector(
    [_dllData, _selectedProcessId],
    (dllData, selectedProcessId) => {
      if (dllData && dllData.length > 0) {
        const [{ machineOsType }] = dllData;
        const dllsThatHaveHooks = dllData.filter((dll) => {
          const { hooks } = dll[machineOsType];
          return hooks && hooks.length;
        });
        const imageHooks = dllsThatHaveHooks.map((item) => {
          const { fileName: dllFileName } = item;
          const filteredHooks = item[machineOsType].hooks.filter((hookObj) => {
            return hookObj.process.pid === selectedProcessId;
          });

          return filteredHooks.map((hookObj) => {
            const { type, hookLocation: { fileName: hookFileName, symbol } } = hookObj;
            return {
              dllFileName,
              type,
              hookFileName,
              symbol
            };
          });
        });

        let consolidatedHooks = [];
        imageHooks.forEach((item) => {
          consolidatedHooks = [...consolidatedHooks, ...item];
        });
        return consolidatedHooks;
      }
      return [];
    }
  );
