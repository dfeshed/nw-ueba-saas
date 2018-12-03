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

export default {
  getFileAnalysisData,
  getFileAnalysisStringFormatData
};
