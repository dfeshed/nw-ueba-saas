import Ember from 'ember';
import * as ACTION_TYPES from './types';
import fetchFileExtractJobId from './fetch/file-extract';

const {
  Logger
} = Ember;

const fileSelected = (fileId) => {
  return {
    type: ACTION_TYPES.FILES_FILE_TOGGLED,
    payload: fileId
  };
};

const deselectAllFiles = () => ({ type: ACTION_TYPES.FILES_SELECT_ALL });

const selectAllFiles = () => ({ type: ACTION_TYPES.FILES_DESELECT_ALL });

const downloadFiles = () => {
  return (dispatch, getState) => {
    const {
      recon: {
        data: {
          endpointId,
          eventId,
          files
        }
      }
    } = getState();

    const selectedFileNames = (files || [])
      .filterBy('selected', true)
      .map((file) => file.fileName);

    // Dispatch the start of the job id retrieval.
    dispatch({
      type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE_STARTED
    });


    // Ask the server for a job id.
    fetchFileExtractJobId(endpointId, eventId, selectedFileNames)
      .catch((e) => {

        // Unable to get a job id.
        Logger.error('Error fetching job id for file extraction', { endpointId, eventId }, e);
        dispatch({
          type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE_FAILURE,
          payload: e,
          error: true
        });
        return e;
      })
      .then((response) => {

        // Got a job id successfully.
        dispatch({
          type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE_SUCCESS,
          payload: {
            jobId: response.data.jobId
          }
        });
        return response;
      });
  };
};

const didDownloadFiles = () => ({ type: ACTION_TYPES.FILE_EXTRACT_JOB_DOWNLOADED });

const showPacketTooltip = (tootipData) => ({
  type: ACTION_TYPES.SHOW_PACKET_TOOLTIP,
  payload: tootipData
});

const hidePacketTooltip = () => ({ type: ACTION_TYPES.HIDE_PACKET_TOOLTIP });

export {
  deselectAllFiles,
  selectAllFiles,
  downloadFiles,
  didDownloadFiles,
  fileSelected,
  showPacketTooltip,
  hidePacketTooltip
};
