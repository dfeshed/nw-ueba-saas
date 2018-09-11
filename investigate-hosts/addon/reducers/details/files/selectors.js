import reselect from 'reselect';

const { createSelector } = reselect;
const _filesData = (state) => state.endpoint.hostFiles.files || [];
const _selectedFileId = (state) => state.endpoint.hostFiles.selectedFileId;
const _filesLoadingStatus = (state) => state.endpoint.hostFiles.filesLoadingStatus;
const _selectedFileList = (state) => state.endpoint.hostFiles.selectedFileList || [];

export const isDataLoading = createSelector(
  _filesLoadingStatus,
  (filesLoadingStatus) => filesLoadingStatus === 'wait'
);

export const filesWithEnrichedData = createSelector(
  [_filesData],
  (filesData) => {
    if (filesData && filesData.length) {
      const data = filesData.map((file) => {
        const { owner, derivedOwner } = file;
        if (owner && !derivedOwner) {
          const groupname = `${owner.groupname} (${owner.gid})`;
          const username = `${owner.username} (${owner.uid})`;
          return { ...file, derivedOwner: { groupname, username } };
        } else {
          return file;
        }
      });
      return data;
    }
    return [];
  }
);
/**
 * selector to get the file's properties for the given id
 * @public
 */

export const fileProperty = createSelector(
  [_filesData, _selectedFileId],
  (filesData, selectedFileId) => {
    if (filesData.length) {
      if (!selectedFileId) {
        return filesData[0];
      }
      return filesData.find((item) => item.id === selectedFileId);
    }
  }
);

/**
 * selector to know all rows selected
 * @public
 */
export const isAllSelected = createSelector(
  [_filesData, _selectedFileList],
  (filesData, selectedFileList) => {
    if (selectedFileList && selectedFileList.length) {
      return filesData.length === selectedFileList.length;
    }
    return false;
  }
);

/**
 * selector for get selected row count.
 * @public
 */
export const selectedFileCount = createSelector(
  [_selectedFileList],
  (selectedFileList) => {
    if (selectedFileList) {
      return selectedFileList.length;
    }
    return 0;
  }
);

/**
 * Selector for list or checksums of all selected files.
 * @public
 */
export const checksums = createSelector(
  [_selectedFileList],
  (selectedFileList) => selectedFileList.map((file) => file.checksumSha256)
);