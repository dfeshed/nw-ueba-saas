import { createSelector } from 'reselect';

// Contains all the expression saved + newly added expression from the UI
const _files = (state) => state.endpoint.hostDownloads.downloads.files;
const _selectedFileList = (state) => state.endpoint.hostDownloads.downloads.selectedFileList || [];
const _areFilesLoading = (state) => state.endpoint.hostDownloads.downloads.areFilesLoading;
const _fileTotal = (state) => state.endpoint.hostDownloads.downloads.totalItems || 0;
const _hasNext = (state) => state.endpoint.hostDownloads.downloads.hasNext;
const _expressionList = (state) => state.endpoint.hostDownloads.filter.expressionList || [];

export const files = createSelector(
  _files,
  (files = {}) => {
    return Object.values(files);
  }
);

export const areFilesLoading = createSelector(
  _areFilesLoading,
  (areFilesLoading) => {
    return areFilesLoading === 'wait';
  }
);

export const fileCount = createSelector(
  files,
  (files) => {
    return files.length;
  }
);

export const hasFiles = createSelector(
  files,
  (files) => {
    return !!files.length;
  }
);

export const isAllSelected = createSelector(
  [files, _selectedFileList],
  (files, selectedFileList) => {
    if (selectedFileList && selectedFileList.length) {
      return files.length === selectedFileList.length;
    }
    return false;
  }
);

export const fileTotalLabel = createSelector(
  [_fileTotal, _expressionList, _hasNext],
  (total, expressionList, hasNext) => {
    if (total >= 1000) {
      if (expressionList && expressionList.length && hasNext) {
        return '1000+';
      }
    }
    return `${total}`;
  }
);
export const nextLoadCount = createSelector(
  [files],
  (files) => {
    const ONE_PAGE_MAX_LENGTH = 100;
    return files.length >= ONE_PAGE_MAX_LENGTH ? ONE_PAGE_MAX_LENGTH : files.length;
  }
);
