import { lookup } from 'ember-dependency-lookup';

/**
 * Websocket call for File Analysis
 * @public
 */

const getFileAnalysisData = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'fileAnalysis',
    modelName: 'endpoint',
    query: { data }
  });
};

const getFileAnalysisStringFormatData = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'fileAnalysisStringFormat',
    modelName: 'endpoint',
    query: { data }
  });
};

const getFileAnalysisTextFormatData = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'fileAnalysisTextFormat',
    modelName: 'endpoint',
    query: { data }
  });
};

const saveLocalFileCopy = (hash, serverId) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'saveLocalCopy',
    modelName: 'endpoint',
    query: {
      data: hash
    },
    streamOptions: {
      socketUrlPostfix: serverId,
      requiredSocketUrl: 'endpoint/socket'
    }
  });
};

export default {
  getFileAnalysisData,
  getFileAnalysisStringFormatData,
  getFileAnalysisTextFormatData,
  saveLocalFileCopy
};
