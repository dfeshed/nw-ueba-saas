import * as ACTION_TYPES from './types';
import { setQueryTimeFormat } from 'investigate-events/actions/interaction-creators';
import { reconPreferencesUpdated } from 'recon/actions/data-creators';

/**
 * This action is triggered when the preferences are updated for this module.
 * This dispatches InvestigateEvents actions to update preference state.
 * It also determines if the query range needs to be recalculated due to a
 * change in the `queryTimeFormat` preference.
 * @param {object} preferences - The preferences data
 * @return {function} A Redux thunk
 * @public
 */
export const preferencesUpdated = (preferences) => {
  return (dispatch, getState) => {
    const currentTimeFormat = getState().investigate.queryNode.queryTimeFormat;
    dispatch({
      type: ACTION_TYPES.SET_PREFERENCES,
      payload: preferences
    });
    if (preferences.queryTimeFormat && preferences.queryTimeFormat !== currentTimeFormat) {
      dispatch(setQueryTimeFormat());
    }

    /* Dispatch a Recon action to update preference state in recon.
     * TODO: This code would move to a recon action-creator eventually, when the preferences trigger moves to recon
     */
    dispatch(reconPreferencesUpdated(preferences));
  };
};
