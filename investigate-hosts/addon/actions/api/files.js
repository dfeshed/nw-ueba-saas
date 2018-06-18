import { lookup } from 'ember-dependency-lookup';

const getHostFiles = (pageNumber, agentId, scanTime, checksumSha256, key, descending) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getHostFilesPages',
    modelName: 'endpoint',
    query: {
      data: {
        pageNumber,
        criteria: { agentId, scanTime, checksumSha256 },
        sort: [{ key, descending }]
      }
    }
  });
};
export {
  getHostFiles
};
