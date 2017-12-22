import { createSelector } from 'reselect';
import { isValidExpression } from 'investigate-files/reducers/filter/selectors';

// Contains all the expression saved + newly added expression from the UI
const files = (state) => state.files.fileList.files;
const _fileExportLinkId = (state) => state.files.fileList.downloadId;
const _totalItems = (state) => state.files.fileList.totalItems;
const _hasNext = (state) => state.files.fileList.hasNext;

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

export const fileExportLink = createSelector(
  _fileExportLinkId,
  (fileExportLinkId) => {
    if (fileExportLinkId) {
      return `${location.origin}/rsa/endpoint/file/property/download?id=${fileExportLinkId}`;
    }
    return null;
  }
);

export const fileCountForDisplay = createSelector(
  [ _totalItems, isValidExpression],
  (totalItems, isValidExpression) => {
    // For performance reasons api returns 1000 as totalItems when filter is applied, even if result is more than 1000
    // Make sure we append '+' to indicate user more files are present
    if (isValidExpression && totalItems >= 1000) {
      return `${totalItems}+`;
    }
    return `${totalItems}`;
  }
);

export const loadMoreStatus = createSelector(
  [_hasNext, fileCountForDisplay],
  (hasNext, fileCountForDisplay) => {
    return fileCountForDisplay.includes('+') || hasNext ? 'stopped' : 'completed';
  }
);
