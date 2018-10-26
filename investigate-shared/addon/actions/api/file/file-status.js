import { lookup } from 'ember-dependency-lookup';

/**
 * Saving the file status to mongo
 * @param data
 * @returns {*}
 * @public
 */
const setFileStatus = (data) => {
  data.automaticallyAssigned = false;
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'setFileStatus',
    modelName: 'context-service',
    query: {
      data
    }
  });
};

/**
 * Saving the file status to mongo
 * @param data
 * @returns {*}
 * @public
 */
const getFileStatus = (selections) => {
  const request = lookup('service:request');
  const data = {
    'filter': [
      { 'field': 'dataSourceType',
        'value': 'FileStatus'
      },
      {
        'field': '_id',
        'values': selections.mapBy('checksumSha256')
      }
    ]
  };
  return request.promiseRequest({
    method: 'getFileStatus',
    modelName: 'context-service',
    streamOptions: { requireRequestId: true },
    query: data
  });
};

const getRestrictedFileList = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'restrictedList',
    modelName: 'endpoint',
    streamOptions: { requireRequestId: true },
    query: {
      data: 'Whitelist'
    }
  });
};


export {
  setFileStatus,
  getFileStatus,
  getRestrictedFileList
};
