import * as ACTION_TYPES from './types';
import fetchNotifications from './fetch/notifications';
import fetchExtractJobId from './fetch/file-extract';
import { createFilename } from 'investigate-shared/actions/api/events/utils';
import { getActiveQueryNode } from 'investigate-events/reducers/investigate/query-node/selectors';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { getColumns } from 'investigate-events/reducers/investigate/data-selectors';
import { EVENT_DOWNLOAD_TYPES } from 'component-lib/constants/event-download-types';

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
        // fetch the second-to-last item as jobId in data.link
        const [, jobId] = data.link.split('/').reverse();
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

export const extractFiles = (eventDownloadType, fileType, sessionIds = [], isSelectAll) => {
  return (dispatch, getState) => {

    const queryNode = getActiveQueryNode(getState());
    const { columnGroup } = getState().investigate.data;

    let columnList = [];
    // All meta available will be downloaded for 'SUMMARY' columnGroup.
    // For others, meta pertaining to the columGroup will be downloaded.
    if (columnGroup !== 'SUMMARY' && eventDownloadType === EVENT_DOWNLOAD_TYPES.META) {
      // download TODO filter by visible ?
      columnList = getColumns(getState()).map(({ field }) => field);
    }

    const { serviceId } = queryNode;
    const { investigate: { services: { serviceData } } } = getState();
    const selectedServiceData = serviceData.find((s) => s.id === serviceId);
    const filename = createFilename(eventDownloadType, selectedServiceData.displayName, sessionIds, isSelectAll);

    dispatch({
      type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE,
      promise: fetchExtractJobId(queryNode, serviceId, sessionIds, fileType, filename, eventDownloadType, isSelectAll, columnList),
      meta: { // download TODO download on success spinner
        onFailure(response) {
          handleInvestigateErrorCode(response, `FETCH_EXTRACT_JOB_ID; ${serviceId} ${eventDownloadType}`);
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
