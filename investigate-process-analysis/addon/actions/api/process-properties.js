import { lookup } from 'ember-dependency-lookup';

/**
 * Executes a websocket fetch call for Process (machine) properties of given hash and returns a Promise.
 *
 * @method getProcessProperties
 * @param model
 * @param serverId
 * @public
 * @returns {Promise}
 */

const getProcessDetails = (model, serverId) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getProcessAnalysisDetails',
    modelName: 'endpoint',
    query: {
      data: model
    },
    streamOptions: {
      socketUrlPostfix: serverId
    }
  });
};

const getHostCount = (checksum) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getHostCount',
    modelName: 'host-count',
    query: {
      data: {
        checksumSha256: checksum
      }
    }
  });
};

export {
  getProcessDetails,
  getHostCount
};