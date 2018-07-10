import { lookup } from 'ember-dependency-lookup';

/**
 * Saving the file status to mongo
 * @param data
 * @returns {*}
 * @public
 */
const setFileStatus = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'setFileStatus',
    modelName: 'files',
    query: {
      data
    }
  });
};

/**
 * Getting the status history for the selected file
 * @param checksum
 * @param requestLatestHistory
 * @returns {*}
 * @public
 */
const getFileStatusHistory = (checksum, requestLatestHistory) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getFileStatusHistory',
    modelName: 'files',
    query: {
      data: {
        checksum,
        requestLatestHistory
      }
    }
  });
};

export {
  setFileStatus,
  getFileStatusHistory
};
