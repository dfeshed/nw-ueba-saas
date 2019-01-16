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

const saveLocalFileCopy = (hash) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'saveLocalCopy',
    modelName: 'endpoint',
    query: {
      data: hash
    }
  });
};

export default {
  getFileAnalysisData,
  getFileAnalysisStringFormatData,
  getFileAnalysisTextFormatData,
  saveLocalFileCopy
};
