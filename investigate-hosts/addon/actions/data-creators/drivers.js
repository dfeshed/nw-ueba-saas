import { HostDetails } from '../api';
import * as ACTION_TYPES from '../types';
import { handleError } from '../creator-utils';
import { setFileStatus, getFileStatus } from 'investigate-shared/actions/api/file/file-status';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

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
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_DRIVERS, response)
      }
    });
  };
};

const setSelectedRow = ({ id }) => ({ type: ACTION_TYPES.SET_DRIVERS_SELECTED_ROW, payload: { id } });
const toggleDriverSelection = (selectedDriver) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_DRIVER, payload: selectedDriver });
const toggleAllDriverSelection = () => ({ type: ACTION_TYPES.TOGGLE_ALL_DRIVER_SELECTION });

const saveDriverStatus = (checksums, data, callbacks = callbacksDefault) => ({
  type: ACTION_TYPES.SAVE_DRIVER_STATUS,
  promise: setFileStatus({ ...data, checksums }),
  meta: {
    onSuccess: (response) => {
      callbacks.onSuccess(response);
    },
    onFailure: (response) => {
      callbacks.onFailure(response);
    }
  }
});

const getSavedDriverStatus = (selections) => ({
  type: ACTION_TYPES.GET_DRIVER_STATUS,
  promise: getFileStatus(selections)
});

export {
  getFileContextDrivers,
  setSelectedRow,
  toggleDriverSelection,
  toggleAllDriverSelection,
  saveDriverStatus,
  getSavedDriverStatus
};