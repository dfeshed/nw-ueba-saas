import fetch from 'fetch';
import { Promise } from 'rsvp';
import config from 'ember-get-config';

const endpoint = function(path) {
  const { useMockServer, mockServerUrl } = config;
  return useMockServer ? `${mockServerUrl}${path}` : path;
};

const _private = {
  _fetch(path) {
    const url = endpoint(path);
    return fetch(url);
  }
};

export default function(path) {
  return new Promise((resolve, reject) => {
    return _private._fetch(path).then((response) => {
      if (response.ok) {
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
