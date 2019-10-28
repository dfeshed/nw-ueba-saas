import { lookup } from 'ember-dependency-lookup';

/**
 *
 * @param {string} id - Id of item to delete
 * @param {string} modelName
 */
export const apiDeleteItem = (id, modelName) => {
  const request = lookup('service:request');
  const query = {};
  query[modelName] = {
    id
  };

  return request.promiseRequest({
    method: 'delete',
    modelName,
    query
  });
};

/**
 *
 * @param {*} itemPayload, modelname for item to be created/updated
 */
export const apiCreateOrUpdateItem = (payload, modelName) => {
  const request = lookup('service:request');
  const query = payload;

  return request.promiseRequest({
    method: 'post',
    modelName,
    query
  });
};
