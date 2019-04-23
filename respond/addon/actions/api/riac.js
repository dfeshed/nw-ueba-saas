import { lookup } from 'ember-dependency-lookup';

export default {
  // MAKE A WS CALL TO GET RIAC VALUE AND RETURN THE PROMISE
  fetchRiacValue() {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'queryRecord',
      modelName: 'riac-settings',
      query: {}
    });
  }
};