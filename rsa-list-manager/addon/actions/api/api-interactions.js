import { lookup } from 'ember-dependency-lookup';

/**
 *
 * @param {string} id, modelName of item to delete
 */
export const apiDeleteItem = (id, modelName) => {
  const request = lookup('service:request');
  const query = {
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
