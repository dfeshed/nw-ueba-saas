import * as ACTION_TYPES from 'springboard/actions/types';
import { getAllSpringboards } from 'springboard/actions/api/springboard';

/**
 * Fetching all the springboard for logged in user
 */
export const fetchAllSpringboards = () => ({
  type: ACTION_TYPES.FETCH_ALL_SPRINGBOARD,
  promise: getAllSpringboards()
});