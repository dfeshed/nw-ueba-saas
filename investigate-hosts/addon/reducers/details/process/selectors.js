import reselect from 'reselect';
import { convertTreeToList } from './util';
import _ from 'lodash';
import TREE_CONFIG from './process-config';
import LIST_CONFIG from './process-list-config';

const { createSelector } = reselect;
const _processData = (state) => state.endpoint.process.processDetails;
const _treeData = (state) => state.endpoint.process.processTree;
const _listData = (state) => state.endpoint.process.processList;
const _dllData = (state) => state.endpoint.process.dllList;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;
const _processDetailsLoading = (state) => state.endpoint.process.processDetailsLoading;
const _isProcessTreeLoading = (state) => state.endpoint.process.isProcessTreeLoading;
const _selectedProcessId = (state) => state.endpoint.process.selectedProcessId;
const _processList = (state) => state.endpoint.process.processList;
const _selectedProcessList = (state) => state.endpoint.process.selectedProcessList;
const _searchResultProcessList = (state) => state.endpoint.process.searchResultProcessList;
const _sortField = (state) => state.endpoint.process.sortField;
const _isDescOrder = (state) => state.endpoint.process.isDescOrder;

const PROCESS_DEFAULT_COLUMN = [
  {
    'dataType': 'checkbox',
    'width': '2vw',
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox',
    field: 'checkbox',
    'preferredDisplayIndex': 1
  }
];

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

export const selectedProcessName = createSelector(
  [ _processData ],
  (processData) => {
    if (processData) {
      return processData.fileName;
    }
    return null;
  });

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
  [_getProcessList, _sortField, _isDescOrder],
  (processList, sortField, isDescOrder) => {
    if (processList && processList.list.length) {
      let data = processList.list.asMutable();
      data = data.map((item) => {
        return { ...item, id: item.pid };
      });
      data = data.sortBy(sortField);
      if (isDescOrder) {
        data.reverse();
      }
      return data && data.length ? data : [];
    }
    return [];
  }
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

const _consolidatedObjs = (listToConsolidate) => {
  let consolidatedList = [];
  listToConsolidate.forEach((item) => {
    consolidatedList = [...consolidatedList, ...item];
  });
  return consolidatedList;
};

/*
  Fetches all the dllList items that have imageHooks.
  Then filters out the relevent imageHooks based on PID and then
  process each hook into the required format.
  input : dllList, selected process Id
  output : [{
              signature,
              dllFileName,
              type,
              hookFileName,
              symbol
  },..]
*/
export const imageHooksData = createSelector(
  [_dllData, _selectedProcessId],
  (dllData, selectedProcessId) => {
    if (dllData && dllData.length) {
      const [{ machineOsType }] = dllData;
      const dllsThatHaveHooks = dllData.filter((dll) => {
        const { imageHooks } = dll[machineOsType];
        return imageHooks && imageHooks.length;
      });
      const filteredImageHooks = dllsThatHaveHooks.map((item) => {
        const { fileName: dllFileName, fileProperties } = item;
        const signature = (fileProperties && fileProperties.signature) ? fileProperties.signature.features : ['unsigned'];
        const filteredHooks = item[machineOsType].imageHooks.filter((hookObj) => {
          return hookObj.process.pid === selectedProcessId;
        });

        return filteredHooks.map((hookObj) => {
          const { type, hookLocation: { fileName: hookFileName, symbol } } = hookObj;
          return {
            ...item,
            signature,
            dllFileName,
            type,
            hookFileName,
            symbol,
            fileProperties
          };
        });
      });

      return _consolidatedObjs(filteredImageHooks);
    }
    return [];
  }
);

/*
  Fetches all the dllList items that have threads.
  Then filters out the relevent threads based on PID and then
  process each threads into the required format.
  input : dllList, selected process Id
  output : [{
              signature,
              dllFileName,
              startAddress,
              tid,
              teb
  },..]
*/
export const suspiciousThreadsData = createSelector(
  [_dllData, _selectedProcessId],
  (dllData, selectedProcessId) => {

    if (dllData && dllData.length) {
      const [{ machineOsType }] = dllData;
      const dllsThatHaveThreads = dllData.filter((dll) => {
        const { threads } = dll[machineOsType];
        return threads && threads.length;
      });
      const suspiciousThreads = dllsThatHaveThreads.map((item) => {
        const { fileName: dllFileName, fileProperties } = item;
        const signature = (fileProperties && fileProperties.signature) ? fileProperties.signature.features : ['unsigned'];
        const filteredThread = item[machineOsType].threads.filter((threadObj) => {
          return threadObj.pid === selectedProcessId;
        });

        return filteredThread.map((threadObj) => {
          const { startAddress, tid, teb } = threadObj;
          return {
            ...item,
            signature,
            dllFileName,
            startAddress,
            tid,
            teb,
            fileProperties
          };
        });
      });

      return _consolidatedObjs(suspiciousThreads);
    }
    return [];
  });

export const areAllSelected = createSelector(
  [ _processList, _selectedProcessList, _searchResultProcessList],
  (processList, selectedProcessList, searchResultProcessList) => {
    if (searchResultProcessList && searchResultProcessList.length > 0) {
      return searchResultProcessList.length === selectedProcessList.length;
    } else if (selectedProcessList && selectedProcessList.length) {
      return processList.length === selectedProcessList.length;
    }
    return false;
  }
);

export const selectedFileChecksums = createSelector(
  _selectedProcessList,
  (selectedProcessList) => {
    if (selectedProcessList && selectedProcessList.length) {
      return selectedProcessList.map((process) => process.checksumSha256);
    }
    return [];
  }
);
const _preferences = (state) => state.preferences.preferences;

export const savedProcessColumns = createSelector(
  _preferences,
  (preferences) => {
    let columnsInPreference = [];
    if (preferences.machinePreference) {
      columnsInPreference = preferences.machinePreference.columnConfig || [];
    }
    const savedColumns = columnsInPreference.filter((item) => {
      return item.tableId === 'hosts-process-tree';
    });

    if (savedColumns && savedColumns.length) {
      const [{ columns }] = savedColumns;
      return columns;
    } else {
      return LIST_CONFIG;
    }
  }
);
const _isTreeView = (state) => state.endpoint.visuals.isTreeView;

export const schema = createSelector(
  _isTreeView,
  (isTreeView) => {
    if (isTreeView) {
      return TREE_CONFIG;
    } else {
      return LIST_CONFIG;
    }
  });

export const updateProcessColumns = (savedProcessColumns, schema) => {
  let updatedSchema = schema;
  if (savedProcessColumns && savedProcessColumns.length) {
    if (savedProcessColumns.length) {
      updatedSchema = schema.map((item, index) => {
        const currentColumn = savedProcessColumns.filter((column) => {
          return column.field === item.field;
        });
        let visible = false;
        let displayIndex, width;
        if (currentColumn && currentColumn.length) {
          visible = true;
          const [{ displayIndex: index, width: columnWidth }] = currentColumn;
          displayIndex = parseInt(index, 10);
          width = columnWidth;
        } else {
          width = item.width;
          displayIndex = index;
        }
        return { ...item, visible, preferredDisplayIndex: displayIndex, width };
      });
    }
  }
  // Set the default columns, if not present in stored configuration
  PROCESS_DEFAULT_COLUMN.forEach((column) => {
    if (column.dataType === 'checkbox') {
      updatedSchema.unshift(column);
    }
  });

  const visibleList = updatedSchema.filter((column) => column.visible);
  if (visibleList) {
    return updatedSchema;
  }

  return [];
};
