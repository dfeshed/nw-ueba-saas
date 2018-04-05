import { Promise, all } from 'rsvp';
import { _private } from 'component-lib/services/fetch';

export function patchFetch(callback) {

  const origFunc = _private._fetch;

  _private._fetch = function() {

    const func = callback.apply(this, arguments);
    const reset = new Promise((resolve) => {
      resolve();
    }).then(() => {
      _private._fetch = origFunc;
    });

    return all([func, reset]).then((results) => {
      return results && results[0];
    });
  };

}
