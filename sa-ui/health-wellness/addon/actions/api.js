import { lookup } from 'ember-dependency-lookup';

const query = {
  keys: ['monitor'], descending: false
};

const getMonitors = () => {

  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getMonitors',
    modelName: 'health-wellness',
    query: { data: query }
  });
};

export default {
  getMonitors
};