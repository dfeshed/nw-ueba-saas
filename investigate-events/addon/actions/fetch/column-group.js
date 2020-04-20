import { findAllPromiseRequest } from 'investigate-shared/actions/api/events/utils';
import { COLUMN_GROUPS_MODEL_NAME as MODEL_NAME } from 'investigate-events/constants/columnGroups';

/**
 * Fetch all of column groups.
 * @return {object} RSVP Promise
 * @public
 */
export const fetchColumnGroups = () => findAllPromiseRequest(MODEL_NAME);
