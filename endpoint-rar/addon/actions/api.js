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

export default { getRARDownloadID };