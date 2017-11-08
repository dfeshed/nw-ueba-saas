import rsvp from 'rsvp';
import { lookup } from 'ember-dependency-lookup';
import preferencesConfig from 'preferences/config/index';

const request = lookup('service:request');

const fetchPreferences = (preferenceFor, data) => {
  const requestPayload = {
    modelName: preferencesConfig[preferenceFor].modelName,
    method: 'getPreferences',
    query: {
      data
    }
  };
  return new rsvp.Promise(function(resolve) {
    request.promiseRequest(requestPayload).then(({ data }) => {
      if (data === null) {
        resolve(preferencesConfig[preferenceFor].defaultPreferences);
      } else {
        resolve(data);
      }
    }).catch(() => {
      resolve(preferencesConfig[preferenceFor].defaultPreferences);
    });
  });
};

const savePreferences = (preferenceFor, preferences) => {
  const requestPayload = {
    modelName: preferencesConfig[preferenceFor].modelName,
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
