import reselect from 'reselect';
const { createSelector } = reselect;
import _ from 'lodash';

const _fileContext = (state, name) => state.endpoint[name].fileContext;
const _sortConfig = (state, name) => state.endpoint[name].sortConfig;
const _contextLoadingStatus = (state, name) => state.endpoint[name].contextLoadingStatus;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;
const _tabName = (state, name) => name;

const _fileStatus = (state, name) => state.endpoint[name].fileStatus;
const _selectedRowId = (state, name) => state.endpoint[name].selectedRowId;
const _fileContextSelections = (state, name) => state.endpoint[name].fileContextSelections || [];
const _totalItems = (state, name) => state.endpoint[name].totalItems;
const _contextLoadMoreStatus = (state, name) => state.endpoint[name].contextLoadMoreStatus;
const _isRemediationAllowed = (state, name) => state.endpoint[name].isRemediationAllowed;

export const fileStatus = createSelector(
  _fileStatus,
  (fileStatus) => ({ ...fileStatus })
);


export const selectedRowId = createSelector(
  _selectedRowId,
  (selectedRowId) => selectedRowId
);


export const fileContextSelections = createSelector(
  _fileContextSelections,
  (fileContextSelections) => fileContextSelections
);


export const totalItems = createSelector(
  _totalItems,
  (totalItems) => totalItems
);


export const contextLoadMoreStatus = createSelector(
  _contextLoadMoreStatus,
  (contextLoadMoreStatus) => contextLoadMoreStatus
);


export const isRemediationAllowed = createSelector(
  _isRemediationAllowed,
  (isRemediationAllowed) => isRemediationAllowed
);


const _getProperties = (rowId, list, data) => {
  const isDataAnArray = Array.isArray(data);
  if (data && rowId) {
    if (isDataAnArray) {
      const filteredRow = data.filter((item) => item.id === rowId);
      return filteredRow[0];
    }
    return data[rowId];
  } else if (list) {
    if (isDataAnArray) {
      return data[0];
    }
    return list[0];
  }
};


export const selectedFileChecksums = createSelector(
  fileContextSelections,
  (selectedFileList) => selectedFileList.map((file) => file.checksumSha256)
);


export const listOfFiles = createSelector(
  [ _fileContext, _sortConfig, _selectedTab, _tabName],
  (fileContext, sortConfig, selectedTab, tabName) => {
    let data = _.values(fileContext);
    if (selectedTab && selectedTab.tabName === tabName.toUpperCase()) {
      data = data.filter((val) => (selectedTab.checksum === val.checksumSha256));
    }

    if (sortConfig) {
      data = data.sortBy(sortConfig.field);
      if (sortConfig.isDescending) {
        data.reverse();
      }
      return data;
    } else {
      return data.sortBy('fileName');
    }
  }
);

export const isAllSelected = createSelector(
  [listOfFiles, fileContextSelections],
  (fileContext, fileContextSelections) => {
    if (fileContextSelections && fileContextSelections.length) {
      return fileContext.length === fileContextSelections.length;
    }
    return false;
  }
);

export const fileContextFileProperty = createSelector([selectedRowId, listOfFiles, _fileContext], _getProperties);

export const isDataLoading = createSelector(
  [_contextLoadingStatus], (contextLoadingStatus) => {
    return ['wait', 'streaming'].includes(contextLoadingStatus);
  }
);
