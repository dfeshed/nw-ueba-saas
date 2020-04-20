import { findAllPromiseRequest } from 'investigate-shared/actions/api/events/utils';
import { META_GROUPS_MODEL_NAME as MODEL_NAME } from 'investigate-events/constants/metaGroups';

/**
 * Fetch all of meta groups
 * @return {object} RSVP Promise
 * @public
 */
export const fetchMetaGroups = () => findAllPromiseRequest(MODEL_NAME);
