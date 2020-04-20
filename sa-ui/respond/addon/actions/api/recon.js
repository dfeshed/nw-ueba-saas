import { Promise } from 'rsvp';
import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from '../types';
import { getServices } from 'respond/reducers/respond/recon/selectors';

const requestServices = function() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'core-service',
    query: {}
  });
};

const fetchPackAction = function(resolve, reject) {
  return () => {
    return {
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
    };
  };
};

const serviceIdFilter = (value) => ({ field: 'endpointId', value });

const fetchLanguageAndAliases = function(endpointId) {
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
    return (dispatch, getState) => {
      return new Promise((resolve, reject) => {
        const coreServices = getServices(getState());
        const callback = fetchPackAction(resolve, reject);
        if (coreServices === undefined) {
          dispatch(callback());
        }
      });
    };
  },

  getLanguagesAndAliases(endpointId) {
    return fetchLanguageAndAliases(endpointId);
  }
};
