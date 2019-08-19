import {
  findAllPromiseRequest
} from 'investigate-shared/actions/api/events/utils';
import { lookup } from 'ember-dependency-lookup';

const MODEL_NAME = 'investigate-columns';

/**
 * Fetch all of column groups.
 * @return {object} RSVP Promise
 * @public
 */
export const fetchColumnGroups = () => findAllPromiseRequest(MODEL_NAME);

/**
 *
 * @param {string} id id of col group to delete
 */
export const apiDeleteColumnGroup = (id) => {
  const request = lookup('service:request');
  const query = {
    id
  };

  return request.promiseRequest({
    method: 'delete',
    modelName: MODEL_NAME,
    query
  });
};

/**
 *
 * @param {string} name
 * @param {object[]} fields
 */
export const apiCreateColumnGroup = (name, fields) => {
  return _createOrUpdateColumnGroup(name, fields);
};

/**
 *
 * @param {string} id id of col group to update
 * @param {string} name
 * @param {object[]} fields
 */
export const apiUpdateColumnGroup = (id, name, fields) => {
  return _createOrUpdateColumnGroup(name, fields, id);
};

/**
 *
 * @param {null|*} id id of col group to update, or null to create a new col group
 * @param {*} name
 * @param {*} fields
 */
const _createOrUpdateColumnGroup = (name, fields, id = null) => {
  const request = lookup('service:request');
  const query = {
    'columnGroup': {
      id,
      name,
      'ootb': false,
      fields
    }
  };

  return request.promiseRequest({
    method: 'post',
    modelName: MODEL_NAME,
    query
  });
};
