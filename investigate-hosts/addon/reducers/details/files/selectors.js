import reselect from 'reselect';

const { createSelector } = reselect;
const _filesData = (state) => state.endpoint.hostFiles.files || [];
const _selectedFileHash = (state) => state.endpoint.hostFiles.selectedFileHash;
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

export const fileProperty = createSelector(
  [_filesData, _selectedFileHash],
  (filesData, selectedFileHash) => {
    if (filesData.length) {
      let hash = selectedFileHash;
      if (!selectedFileHash) {
        hash = filesData[0].checksumSha256;
      }
      return filesData.find((item) => item.checksumSha256 === hash);
    }
  }
);