import { lookup } from 'ember-dependency-lookup';

/**
 * Making api call to fetch all the springboard
 * @returns {RSVP.Promise}
 */
export const getAllSpringboards = () => {
  const request = lookup('service:request');
  const modelName = 'springboard';
  const method = 'all';

  return request.promiseRequest({
    method,
    modelName,
    query: {}
  });
};