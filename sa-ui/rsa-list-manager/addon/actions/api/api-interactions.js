import { lookup } from 'ember-dependency-lookup';

/**
 *
 * @param { object } payload for id to delete
 * @param {string} modelName
 */
export const apiDeleteItem = (payload, modelName) => {
  const request = lookup('service:request');
  const query = payload;

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
