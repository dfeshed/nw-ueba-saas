import reselect from 'reselect';

const { createSelector } = reselect;
const _filesData = (state) => state.endpoint.hostFiles.files || [];
const _selectedFileId = (state) => state.endpoint.hostFiles.selectedFileId;
const _filesLoadingStatus = (state) => state.endpoint.hostFiles.filesLoadingStatus;

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