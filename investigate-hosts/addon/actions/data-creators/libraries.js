import { HostDetails, Process } from '../api';
import { handleError } from '../creator-utils';
import * as ACTION_TYPES from '../types';
import { isDetailsLoading } from 'investigate-hosts/actions/ui-state-creators';

const getProcessAndLib = () => {
  return (dispatch, getState) => {
    const { sortField: key, isDescOrder: descending } = getState().endpoint.process;
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    dispatch({
      type: ACTION_TYPES.GET_LIBRARY_PROCESS_INFO,
      promise: Process.getProcessList({ agentId, scanTime }, { key, descending }),
      meta: {
        onSuccess: () => {
          dispatch(getFileContextDLLS());
        }
      }
    });
  };
};

/**
 * Action creator for fetching all the drivers for given host and scan time
 * @method getFileContextDrivers
 * @public
 * @returns {Object}
 */
const getFileContextDLLS = () => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories: ['LOADED_LIBRARIES']
    };
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_DLLS,
      promise: HostDetails.getFileContextData(data),
      meta: {
        onSuccess: () => {
          dispatch(isDetailsLoading(false));
        },
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_DLLS, response)
      }
    });
  };
};

const setSelectedRow = ({ id }) => ({ type: ACTION_TYPES.SET_DLLS_SELECTED_ROW, payload: { id } });

export {
  getProcessAndLib,
  getFileContextDLLS,
  setSelectedRow
};
