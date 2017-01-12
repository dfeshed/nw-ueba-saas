import Ember from 'ember';
import { promiseRequest } from 'streaming-data/services/data-access/requests';
import config from 'sa/config/environment';

const { $ } = Ember;

/**
 * Temporary jQuery Ajax implementation of REST calls when NOMOCK is active, otherwise mock socket responses
 * @param method
 * @param modelName
 * @param url
 * @param ajaxMethod
 * @param payload
 * @public
 * @returns {Promise}
 */
const basicPromiseRequest = (method, modelName, url, ajaxMethod = 'GET', payload = {}) => {

  if (!config.useMockServer) {
    return $.ajax({
      url,
      method: ajaxMethod,
      contentType: 'application/json',
      data: JSON.stringify(payload)
    });
  } else {
    return promiseRequest({
      method,
      modelName,
      query: payload
    });
  }
};

export {
  basicPromiseRequest
};