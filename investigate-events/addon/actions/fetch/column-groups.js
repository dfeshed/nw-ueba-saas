import {
  findAllPromiseRequest
} from 'investigate-shared/actions/api/events/utils';

/**
 * Fetch all of column groups.
 * @return {object} RSVP Promise
 * @public
 */
export const fetchColumnGroups = () => findAllPromiseRequest('investigate-columns');
