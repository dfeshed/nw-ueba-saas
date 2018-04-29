import { lookup } from 'ember-dependency-lookup';

/**
 * Executes a websocket fetch call for Process (machine) properties of given hash and returns a Promise.
 *
 * @method getProcessProperties
 * @param model
 * @public
 * @returns {Promise}
 */

const getProcessDetails = (model) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getProcessAnalysisDetails',
    modelName: 'endpoint',
    query: {
      data: model
    }
  });
};

export {
  getProcessDetails
};