import reselect from 'reselect';
const { createSelector } = reselect;
import _ from 'lodash';

const _fileContext = (state, name) => state.endpoint[name].fileContext;
const _sortConfig = (state, name) => state.endpoint[name].sortConfig;
const _contextLoadingStatus = (state, name) => state.endpoint[name].contextLoadingStatus;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;

const _fileStatus = (state, name) => state.endpoint[name].fileStatus;
const _selectedRowId = (state, name) => state.endpoint[name].selectedRowId;
const _fileContextSelections = (state, name) => {
  if (name === 'process') {
    return state.endpoint.process.selectedProcessList || [];
  }
  return state.endpoint[name].fileContextSelections || [];
};
const _totalItems = (state, name) => state.endpoint[name].totalItems;
const _contextLoadMoreStatus = (state, name) => state.endpoint[name].contextLoadMoreStatus;
const _isRemediationAllowed = (state, name) => state.endpoint[name].isRemediationAllowed;
const _hostDetails = (state) => state.endpoint.overview.hostDetails;

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

export const selectedFileList = createSelector(
  fileContextSelections,
  (selectedFiles) => selectedFiles.map(({ checksumSha256, fileName }) => ({
    checksumSha256,
    fileName
  }))
);

export const focusedRowChecksum = createSelector(
  [selectedRowId, _fileContext],
  (rowId, files) => {
    if (rowId) {
      return files[rowId].checksumSha256;
    }
  }
);


export const listOfFiles = createSelector(
  [ _fileContext, _sortConfig, _selectedTab],
  (fileContext, sortConfig, selectedTab) => {
    let data = _.values(fileContext);
    if (selectedTab) {
      data = data.filter((val) => (selectedTab.checksum === val.checksumSha256));
    }

    if (sortConfig) {
      data = data.sortBy(sortConfig.field);
      if (sortConfig.isDescending) {
        data.reverse();
      }
      return data;
    } else {
      // default sorting by risk score, higher risk first
      data = data.sortBy('fileProperties.score');
      data.reverse();
      return data;
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
  _contextLoadingStatus,
  (contextLoadingStatus) => {
    return ['wait', 'streaming'].includes(contextLoadingStatus);
  }
);

const _isNotAdvanced = createSelector(
  _hostDetails,
  (hostDetails) => {
    if (hostDetails) {
      return hostDetails.machineIdentity.agentMode.toLowerCase() !== 'advanced';
    }
    return true;
  }
);

const _isFloatingOrMemoryDll = createSelector(
  fileContextSelections,
  (fileContextSelections) => {
    if (fileContextSelections && fileContextSelections.length) {
      const filteredList = fileContextSelections.filter((item) => (item.format === 'floating') || (item.features && item.features.includes('file.memoryHash')));
      return filteredList.length === fileContextSelections.length;
    }
    return true;
  }
);
export const isAnyFileFloatingOrMemoryDll = createSelector(
  fileContextSelections,
  (fileContextSelections) => {
    if (fileContextSelections && fileContextSelections.length) {
      const filteredList = fileContextSelections.some((item) => {
        return (item.format && item.format === 'floating') || (item.features && item.features.includes('file.memoryHash'));
      });
      return filteredList;
    }
    return false;
  }
);
const _areAllFilesNotDownloadedToServer = createSelector(
  fileContextSelections,
  (fileContextSelections) => {
    if (fileContextSelections && fileContextSelections.length) {
      return fileContextSelections.some((item) => {
        if (item.downloadInfo) {
          return item.downloadInfo.status !== 'Downloaded';
        }
        return true;
      });
    }
    return true;
  }
);

export const fileDownloadButtonStatus = createSelector(
  [_isNotAdvanced, _isFloatingOrMemoryDll, _areAllFilesNotDownloadedToServer, fileContextSelections],
  (isNotAdvanced, areAllSelectedFloatingOrMemoryDll, areAllFilesNotDownloadedToServer, fileContextSelections) => {
    const selectedFilesLength = fileContextSelections.length;
    // if agent is not advanced and file's downloaded status is true
    const isDownloadToServerDisabled = isNotAdvanced || areAllSelectedFloatingOrMemoryDll || (selectedFilesLength < 0) || (selectedFilesLength > 100) || (!areAllFilesNotDownloadedToServer);
    // if agent is not advanced and selectedFilesLength is 1 and file's downloaded status is true
    const isSaveLocalAndFileAnalysisDisabled = isNotAdvanced || areAllSelectedFloatingOrMemoryDll || ((selectedFilesLength !== 1) || areAllFilesNotDownloadedToServer);
    return {
      isDownloadToServerDisabled,
      isSaveLocalAndFileAnalysisDisabled
    };
  }
);