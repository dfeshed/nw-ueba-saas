import { HostDetails } from '../api';
import * as ACTION_TYPES from '../types';
import { handleError } from '../creator-utils';
import { isDetailsLoading } from 'investigate-hosts/actions/ui-state-creators';

/**
 * Action creator for fetching all the drivers for given host and scan time
 * @method getFileContextDrivers
 * @public
 * @returns {Object}
 */
const getFileContextDrivers = () => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories: ['DRIVERS']
    };
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_DRIVERS,
      promise: HostDetails.getFileContextData(data),
      meta: {
        onSuccess: () => {
          dispatch(isDetailsLoading(false));
        },
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_DRIVERS, response)
      }
    });
  };
};

const setSelectedRow = ({ id }) => ({ type: ACTION_TYPES.SET_DRIVERS_SELECTED_ROW, payload: { id } });

export {
  getFileContextDrivers,
  setSelectedRow
};
