import { findAllPromiseRequest } from 'investigate-shared/actions/api/events/utils';
import { PROFILES_MODEL_NAME as MODEL_NAME } from 'investigate-events/constants/profiles';

/**
 * Fetch all of profiles.
 * @return {object} RSVP Promise
 * @public
 */
export const fetchProfiles = () => findAllPromiseRequest(MODEL_NAME);
