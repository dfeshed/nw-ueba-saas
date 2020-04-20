import fetch from 'fetch';
import { Promise } from 'rsvp';
import config from 'ember-get-config';

const endpoint = function(path) {
  const { useMockServer, mockServerUrl } = config;
  return useMockServer ? `${mockServerUrl}${path}` : path;
};

const _private = {
  _fetch(path, options) {
    const url = endpoint(path);
    return fetch(url, options);
  }
};

export default function(path, options, passErrors = false) {
  return new Promise((resolve, reject) => {
    return _private._fetch(path, options).then((response) => {
      if (response.ok || passErrors) {
        resolve(response);
      } else {
        throw new Error('invalid http response');
      }
    }).catch((error) => {
      reject(error);
    });
  });
}

export { _private };