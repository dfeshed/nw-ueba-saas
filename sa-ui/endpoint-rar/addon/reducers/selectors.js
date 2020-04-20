import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _rarDownloadId = (state) => state.rar.downloadId || '';
const _serverId = (state) => state.rar.serverId || '';

// SELECTOR FUNCTIONS

export const rarInstallerURL = createSelector(
  [_rarDownloadId, _serverId],
  (downloadID, serverId) => {
    let url = null;
    const time = Number(new Date());
    if (downloadID) {
      if (serverId) {
        url = `/rsa/endpoint/${serverId}/rar/installer/download?id=${downloadID}&${time}`;
      } else {
        url = `/rsa/endpoint/rar/installer/download?id=${downloadID}&${time}`;
      }
    }
    return url;
  }
);