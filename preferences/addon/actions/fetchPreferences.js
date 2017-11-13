import rsvp from 'rsvp';
import { lookup } from 'ember-dependency-lookup';
import preferencesConfig from 'preferences/config/index';
import _ from 'lodash';

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
    const { defaultPreferences } = preferencesConfig[preferenceFor];
    request.promiseRequest(requestPayload).then(({ data }) => {
      if (data === null) {
        resolve(defaultPreferences);
      } else {
        // Need to merge with default preferences. Server can send partial preferences.
        data = _.merge(defaultPreferences, data);
        resolve(data);
      }
    }).catch(() => {
      resolve(defaultPreferences);
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
