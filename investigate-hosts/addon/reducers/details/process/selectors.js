import reselect from 'reselect';
import { convertTreeToList } from './util';
import { getValues } from 'investigate-hosts/reducers/details/selector-utils';
import _ from 'lodash';

const { createSelector } = reselect;
const _processData = (state) => state.endpoint.process.processDetails;
const _treeData = (state) => state.endpoint.process.processTree;
const _listData = (state) => state.endpoint.process.processList;
const _dllData = (state) => state.endpoint.process.dllList;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;
const _processDetailsLoading = (state) => state.endpoint.process.processDetailsLoading;
const _processTreeLoading = (state) => state.endpoint.process.processTreeLoading;

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
    const list = getValues(selectedTab, tabName, listData);
    const tree = _getTree(selectedTab, tabName, treeData.asMutable());
    return {
      list,
      tree
    };
  }
);

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
      });
      return newDallData;
    }
    return dllData;
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
  [ _processDetailsLoading, _processTreeLoading ],
  (processDetailsLoading, processTreeLoading) => processTreeLoading || processDetailsLoading
);
