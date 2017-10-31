
import { promiseRequest } from 'streaming-data/services/data-access/requests';

const getPackagerConfig = () => {
  return promiseRequest({
    method: 'get',
    modelName: 'packager',
    query: {}
  });
};
const setPackagerConfig = (model) => {
  return promiseRequest({
    method: 'set',
    modelName: 'packager',
    query: {
      data: model
    }
  });
};

const createPackagerLogConfig = (model) => {
  return promiseRequest({
    method: 'create',
    modelName: 'packager',
    query: {
      data: model
    }
  });
};

export {
  getPackagerConfig,
  setPackagerConfig,
  createPackagerLogConfig
};
