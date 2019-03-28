import { lookup } from 'ember-dependency-lookup';

/**
 * Websocket call for endpoint RAR
 * @public
 */

const getRARDownloadID = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'rarInstaller',
    modelName: 'endpoint-rar',
    query: { ...data }
  });
};

const getRARConfig = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'get',
    modelName: 'endpoint-rar',
    query: {}
  });
};

const saveRARConfig = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'set',
    modelName: 'endpoint-rar',
    query: { data: { ...data } }
  });
};

export default {
  getRARDownloadID,
  getRARConfig,
  saveRARConfig
};