import { promiseRequest } from 'streaming-data/services/data-access/requests';

const getHostFiles = (pageNumber, agentId, scanTime, checksumSha256, key, descending) => {
  return promiseRequest({
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
