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
    query: { data: { ...data } }
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

const getEnableStatus = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getEnableStatus',
    modelName: 'endpoint-rar',
    query: {}
  });
};

const saveEnableStatus = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'setEnableStatus',
    modelName: 'endpoint-rar',
    query: { data: { ...data } }
  });
};

const testRARConfig = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'testConfig',
    modelName: 'endpoint-rar',
    query: { data: { ...data } }
  });
};

export default {
  getRARDownloadID,
  getRARConfig,
  saveRARConfig,
  testRARConfig,
  getEnableStatus,
  saveEnableStatus
};