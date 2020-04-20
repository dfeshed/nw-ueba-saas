import { lookup } from 'ember-dependency-lookup';

export default {
  fetchRiacValue() {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'queryRecord',
      modelName: 'riac-settings',
      query: {},
      streamOptions: {
        keepAliveOnRouteChange: true
      }
    });
  }
};