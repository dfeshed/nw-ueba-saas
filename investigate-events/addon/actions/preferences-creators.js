import * as ACTION_TYPES from './types';
import { setQueryTimeRange } from 'investigate-events/actions/interaction-creators';
import { selectedTimeRange } from 'investigate-events/reducers/investigate/query-node/selectors';

import { SET_PREFERENCES, CHANGE_RECON_VIEW } from 'recon/actions/types';
import { fetchInvestigateData } from './data-creators';
import { isEndpointEvent, isLogEvent } from 'recon/reducers/meta/selectors';


/**
 * This dispatches a Recon action to update preference state.
 * TODO: This action creator would move to recon eventually when the preferences
 * are split
 * @see preferencesUpdated
 * @param {object} preferences - The preferences data
 * @return {object} An action object
 * @private
 */
const _reconPreferenceUpdated = (preferences) => ({
  type: SET_PREFERENCES,
  payload: preferences
});


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
    if (preferences.queryTimeFormat !== currentTimeFormat) {
      const range = selectedTimeRange(getState());
      dispatch(setQueryTimeRange(range));
      dispatch(fetchInvestigateData());
    }
    dispatch(_reconPreferenceUpdated(preferences));

    /* If its a packet event, we need to update the currentReconView
     * to be same as the one selected in Preferences Panel..
     * But for Log/Endpoint Event , it needs to remain
     *  'Text Analysis' always.
     */
    if (!(isLogEvent(getState().recon) || isEndpointEvent(getState().recon))) {
      dispatch({
        type: CHANGE_RECON_VIEW,
        payload: {
          newView: getState().recon.visuals.defaultReconView
        }
      });
    }
  };
};