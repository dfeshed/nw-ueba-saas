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

const _getTree = (selectedTab, tabName, data) => {
  if (selectedTab && selectedTab.tabName === tabName) {
    return data.filter((d) => {
      if (d.checksumSha256 === selectedTab.checksum) {
        return true;
      } else {
        if (d.childProcesses) {
          const childList = _.flatten(d.childProcesses.map(convertTreeToList));
          return childList.some((child) => child.checksumSha256 === selectedTab.checksum);
        }
      }
    });
  } else {
    return data;
  }
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
    const tree = _getTree(selectedTab, tabName, treeData);
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
