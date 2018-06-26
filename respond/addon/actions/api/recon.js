import RSVP, { Promise } from 'rsvp';
import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from '../types';

const requestServices = function() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'core-service',
    query: {}
  });
};

const serviceIdFilter = (value) => ({ field: 'endpointId', value });
const fetchLanguage = function(endpointId) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'query',
    modelName: 'core-meta-key',
    query: {
      filter: [
        serviceIdFilter(endpointId)
      ]
    }
  });
};

const fetchAliases = function(endpointId) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'query',
    modelName: 'core-meta-alias',
    query: {
      filter: [
        serviceIdFilter(endpointId)
      ]
    },
    streamOptions: {
      cancelPreviouslyExecuting: true
    }
  });
};

export default {

  getServices() {
    return (dispatch) => {
      return new Promise((resolve, reject) => {
        dispatch({
          type: ACTION_TYPES.SERVICES_RETRIEVE,
          promise: requestServices(),
          meta: {
            onSuccess() {
              resolve();
            },
            onFailure() {
              reject();
            }
          }
        });
      });
    };
  },

  getLanguagesAndAliases(endpointId) {
    const languagePromise = fetchLanguage(endpointId);
    const aliasesPromise = fetchAliases(endpointId);
    return RSVP.all([languagePromise, aliasesPromise]);
  }

};
