import rsvp from 'rsvp';
import { lookup } from 'ember-dependency-lookup';

const request = lookup('service:request');

const fetchPreferences = (preferenceFor, data) => {
  const requestPayload = {
    modelName: preferenceFor,
    method: 'getPreferences',
    query: {
      data
    }
  };
  return new rsvp.Promise(function(resolve) {
    request.promiseRequest(requestPayload).then(({ data }) => {
      resolve(data);
    }).catch(() => {
      resolve(null);
    });
  });
};

const savePreferences = (preferenceFor, preferences) => {
  const requestPayload = {
    modelName: preferenceFor,
    method: 'setPreferences',
    query: {
      data: preferences
    }
  };
  return new rsvp.Promise(function(resolve, reject) {
    request.promiseRequest(requestPayload).then(() => {
      resolve(preferences);
    }).catch(() => {
      reject(preferences);
    });
  });
};

export { fetchPreferences, savePreferences };
