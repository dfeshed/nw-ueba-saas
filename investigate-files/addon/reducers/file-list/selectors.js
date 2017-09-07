import { createSelector } from 'reselect';

// Contains all the expression saved + newly added expression from the UI
const files = (state) => state.fileList.files;

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