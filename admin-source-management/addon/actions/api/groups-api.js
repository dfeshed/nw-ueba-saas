import { lookup } from 'ember-dependency-lookup';

function fetchGroups() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'groups',
    query: {}
  });
}

export default {
  fetchGroups
};
