import {
  findAllPromiseRequest
} from 'investigate-shared/actions/api/events/utils';
import { lookup } from 'ember-dependency-lookup';

/**
 * Fetch all of column groups.
 * @return {object} RSVP Promise
 * @public
 */
export const fetchColumnGroups = () => findAllPromiseRequest('investigate-columns');

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
    modelName: 'investigate-columns-create-or-update',
    query
  });
};
