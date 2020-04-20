import rsvp from 'rsvp';
import { lookup } from 'ember-dependency-lookup';

const fetchPreferences = (preferenceFor, data, streamOptions) => {
  const requestPayload = {
    modelName: preferenceFor,
    method: 'getPreferences',
    query: {
      data
    }
  };

  if (streamOptions) {
    requestPayload.streamOptions = streamOptions;
  }
  return new rsvp.Promise(function(resolve) {
    const request = lookup('service:request');
    request.promiseRequest(requestPayload).then(({ data }) => {
      resolve(data);
    }).catch(() => {
      resolve(null);
    });
  });
};

const savePreferences = (preferenceFor, preferences, streamOptions) => {
  const requestPayload = {
    modelName: preferenceFor,
    method: 'setPreferences',
    query: {
      data: preferences
    }
  };

  if (streamOptions) {
    requestPayload.streamOptions = streamOptions;
  }

  return new rsvp.Promise(function(resolve, reject) {
    const request = lookup('service:request');
    request.promiseRequest(requestPayload).then(() => {
      resolve(preferences);
    }).catch(() => {
      reject(preferences);
    });
  });
};

export { fetchPreferences, savePreferences };
