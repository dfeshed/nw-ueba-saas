import * as ACTION_TYPES from './types';
import fetchNotifications from './fetch/notifications';
import fetchExtractJobId from './fetch/file-extract';
import { createFilename } from 'investigate-shared/actions/api/events/utils';
import { getActiveQueryNode } from 'investigate-events/reducers/investigate/query-node/selectors';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { EVENT_DOWNLOAD_TYPES } from 'component-lib/constants/event-download-types';

import { lookup } from 'ember-dependency-lookup';
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
        if (extractStatus === 'wait') {

          if (data.success && data.link) {

            const { fileExtractJobId } = getState().investigate.files;
            // fetch the second-to-last item as responseJobId in data.link
            const [, responseJobId] = data.link.split('/').reverse();
            if (fileExtractJobId === responseJobId) {

              dispatch({
                type: ACTION_TYPES.FILE_EXTRACT_JOB_SUCCESS,
                payload: { link: encodeURI(data.link) }
              });
            }
          } else {

            dispatch({ type: ACTION_TYPES.FILE_EXTRACT_FAILURE });
            _displayDownloadError(data.errorMessage);
          }
        }
      },
      // some job failed
      (response) => {
        handleInvestigateErrorCode(response, 'FETCH_NOTIFICATIONS');

        // if the failed job was download related...
        const extractStatus = getState().investigate.files?.fileExtractStatus;
        if (extractStatus === 'wait') {
          dispatch({ type: ACTION_TYPES.FILE_EXTRACT_FAILURE });
          _displayDownloadError(response.errorMessage);
        }
      }
    );
  };
};

export const extractFiles = (eventDownloadType, fileType, sessionIds = [], isSelectAll) => {
  return (dispatch, getState) => {

    const queryNode = getActiveQueryNode(getState());
    const { selectedColumnGroup } = getState().investigate.data;

    let columnList = [];
    // All meta available will be downloaded for 'SUMMARY' columnGroup.
    // For others, visible meta (selected columns) pertaining to the columGroup will be downloaded.
    if (selectedColumnGroup !== 'SUMMARY' && eventDownloadType === EVENT_DOWNLOAD_TYPES.META) {
      const { visibleColumns } = getState().investigate.eventResults;
      columnList = visibleColumns.filter((col) => col.field !== 'checkbox').map(({ field }) => field);
    }

    const { serviceId } = queryNode;
    const { investigate: { services: { serviceData } } } = getState();
    const selectedServiceData = serviceData.find((s) => s.id === serviceId);

    const filename = createFilename(eventDownloadType, selectedServiceData.displayName, sessionIds, isSelectAll);

    dispatch({
      type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE,
      promise: fetchExtractJobId(serviceId, sessionIds, fileType, filename, eventDownloadType, columnList),
      meta: {
        onStart() {
          _showDownloadInQueueInfo();
        },
        onFailure(response) {
          handleInvestigateErrorCode(response, `FETCH_EXTRACT_JOB_ID; ${serviceId} ${eventDownloadType}`);
          _displayDownloadError();
        }
      }
    });
  };
};

export const teardownNotifications = () => ({ type: ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS });

export const didDownloadFiles = () => ({ type: ACTION_TYPES.FILE_EXTRACT_JOB_DOWNLOADED });

const _showDownloadInQueueInfo = () => {
  const flashMessages = lookup('service:flashMessages');
  if (flashMessages && flashMessages.info) {
    const i18n = lookup('service:i18n');
    const url = `${window.location.origin}/profile#jobs`;
    flashMessages.info(i18n.t('fileExtract.info', { url, htmlSafe: true }).string);
  }
};

const _displayDownloadError = (errorMessage) => {
  const flashMessages = lookup('service:flashMessages');
  if (flashMessages && flashMessages.error) {
    const i18n = lookup('service:i18n');
    errorMessage = errorMessage || i18n.t('fileExtract.error.generic');
    flashMessages.error(errorMessage);
  }
};

// *******
// END - Copy/pasted download code from Recon
// *******
