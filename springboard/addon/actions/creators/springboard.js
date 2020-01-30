import * as ACTION_TYPES from 'springboard/actions/types';
import { getAllSpringboards } from 'springboard/actions/api/springboard';

/**
 * Fetching all the springboard for logged in user
 */
export const fetchAllSpringboards = () => ({
  type: ACTION_TYPES.FETCH_ALL_SPRINGBOARD,
  promise: getAllSpringboards()
});

/**
 * User can select the springboard from the dropdown on selection, change the active springboard id
 * @param id
 */
export const setActiveSpringboardId = (id) => ({
  type: ACTION_TYPES.SET_ACTIVE_SPRINGBOARD_ID,
  payload: id
});

/**
 * Initialize the springboard, fetch all required data for springboard
 * @returns {function(...[*]=)}
 */
export const initializeSpringboard = () => {
  return async(dispatch) => {
    try {
      await dispatch(fetchAllSpringboards());
    } catch (e) {
      // Exception
    }
  };
};