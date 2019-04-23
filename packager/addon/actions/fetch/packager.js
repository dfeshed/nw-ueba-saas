
import { lookup } from 'ember-dependency-lookup';

const getPackagerConfig = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'get',
    modelName: 'packager',
    query: {}
  });
};
const setPackagerConfig = (model) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'set',
    modelName: 'packager',
    query: {
      data: model
    }
  });
};

const getConfiguration = (model) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'loadConfig',
    modelName: 'packager',
    query: {
      data: model
    }
  });
};

export const fetchEndpointServers = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'endpoint-server',
    query: {}
  });
};

export {
  getPackagerConfig,
  setPackagerConfig,
  getConfiguration
};


