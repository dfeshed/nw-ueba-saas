import { HostDetails } from '../api';
import * as ACTION_TYPES from '../types';
import { handleError } from '../creator-utils';

/**
 * Action creator for fetching all hooks given host id and scan time
 * @method getFileContextHooks
 * @public
 * @returns {Object}
 */
const getFileContextHooks = () => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories: ['HOOKS']
    };
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_HOOKS,
      promise: HostDetails.getFileContextData(data),
      meta: {
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_HOOKS, response)
      }
    });
  };
};

const setSelectedRow = ({ id }) => ({ type: ACTION_TYPES.SET_ANOMALIES_SELECTED_ROW, payload: { id } });

export {
  getFileContextHooks,
  setSelectedRow
};
