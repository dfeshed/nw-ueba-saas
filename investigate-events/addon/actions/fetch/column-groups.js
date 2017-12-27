import {
  findAllPromiseRequest
} from './utils';

/**
 * Fetch all of column groups.
 * @return {object} RSVP Promise
 * @public
 */
export const fetchColumnGroups = () => findAllPromiseRequest('investigate-columns');
