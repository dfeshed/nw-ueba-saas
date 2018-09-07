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
        'field': 'id',
        'values': selections.mapBy('id')
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

export {
  setFileStatus,
  getFileStatus
};
