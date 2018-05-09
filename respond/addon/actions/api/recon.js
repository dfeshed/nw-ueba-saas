import { Promise } from 'rsvp';
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
  }

};
