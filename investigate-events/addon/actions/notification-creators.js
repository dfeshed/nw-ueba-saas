import * as ACTION_TYPES from './types';
import fetchNotifications from './fetch/notifications';
import fetchExtractJobId from './fetch/file-extract';
import { createFilename } from 'investigate-shared/actions/api/events/utils';
import { getActiveQueryNode } from 'investigate-events/reducers/investigate/query-node/selectors';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';

// *******
// BEGIN - Copy/pasted download code from Recon
// *******

/**
 * Subscribe to notifications. Notifications tell us when any file
 * downloads are finished/failed.
 * @public
 */
export const initializeNotifications = () => {
  return (dispatch, getState) => {
    fetchNotifications(
      // on successful init, will received a function for
      // stopping all notification callbacks
      (response) => {
        dispatch({
          type: ACTION_TYPES.NOTIFICATION_INIT_SUCCESS,
          payload: { cancelFn: response }
        });
      },
      // some job has finished and is ready for download...
      ({ data }) => {

        // ...but is it the right job?
        //
        // Verify that a file extraction is actually taking place.
        // If multiple browsers are open to the file tab of recon,
        // then all those open sockets will get the notification
        // that the download is ready, but we do not want to download
        // from every browser, just the browser where the download originated.
        const extractStatus = getState().investigate.files.fileExtractStatus;
        const extractedJobId = getState().investigate.files.fileExtractJobId;
        const [,,,, jobId] = data.link.split('/');
        if (extractStatus === 'wait' && extractedJobId === jobId) {

          dispatch({
            type: ACTION_TYPES.FILE_EXTRACT_JOB_SUCCESS,
            payload: data
          });
        }
      },
      // some job failed
      (response) => {
        handleInvestigateErrorCode(response, 'FETCH_NOTIFICATIONS');
      }
    );
  };
};

export const extractFiles = (eventType, fileType, sessionIds = [], isSelectAll) => {
  return (dispatch, getState) => {

    const queryNode = getActiveQueryNode(getState());
    const { serviceId } = queryNode;
    const { investigate: { services: { serviceData } } } = getState();
    const selectedServiceData = serviceData.find((s) => s.id === serviceId);
    const filename = createFilename(eventType, selectedServiceData.displayName, sessionIds, isSelectAll);

    dispatch({
      type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE,
      promise: fetchExtractJobId(queryNode, serviceId, sessionIds, fileType, filename, eventType, isSelectAll),
      meta: {
        onFailure(response) {
          handleInvestigateErrorCode(response, `FETCH_EXTRACT_JOB_ID; ${serviceId} ${eventType}`);
        }
      }
    });
  };
};

export const teardownNotifications = () => ({ type: ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS });

export const didDownloadFiles = () => ({ type: ACTION_TYPES.FILE_EXTRACT_JOB_DOWNLOADED });

// *******
// END - Copy/pasted download code from Recon
// *******
