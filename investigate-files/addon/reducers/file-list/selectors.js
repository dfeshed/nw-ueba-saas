import { createSelector } from 'reselect';

// Contains all the expression saved + newly added expression from the UI
const files = (state) => state.files.fileList.files;
const _fileExportLinkId = (state) => state.files.fileList.downloadId;

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
      return `${location.origin}/endpoint/file/download/${fileExportLinkId}`;
    }
    return null;
  }
);