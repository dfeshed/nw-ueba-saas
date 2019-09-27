import { get } from '@ember/object';
import { isArray } from '@ember/array';

import * as ACTION_TYPES from './types';
import { fetchExtractJobId } from './fetch';
import { showDownloadInQueueInfo, displayDownloadError } from './data-creators';
import { getHeaderItem } from 'recon/utils/recon-event-header';
import { selectedFiles } from 'recon/reducers/files/selectors';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import {
  isEndpointEvent,
  isLogEvent
} from 'recon/reducers/meta/selectors';

const fileSelected = (fileId) => {
  return {
    type: ACTION_TYPES.FILES_FILE_TOGGLED,
    payload: fileId
  };
};

const fileSelectionChanged = (fileIds, isSelected) => {
  return {
    type: isSelected ? ACTION_TYPES.FILES_FILE_SELECTED : ACTION_TYPES.FILES_FILE_DESELECTED,
    payload: fileIds
  };
};

const deselectAllFiles = () => ({ type: ACTION_TYPES.FILES_SELECT_ALL });

const selectAllFiles = () => ({ type: ACTION_TYPES.FILES_DESELECT_ALL });

const createFilename = (eventType, headerItems, files = []) => {
  /*
   If the file name is empty, the service will return a UUID filename.  And
   if we do not have the required paramters to make a file name, it is best
   to allow the service assign an UUID instead of the UI giving an undefined.
   */
  let fileName = '';

  const [nwService, sessionId] =
    ['nwService', 'sessionId'].map((k) => {
      return get(getHeaderItem(headerItems, k), 'value');
    });
  if (nwService && sessionId) {
    fileName = `${nwService.replace(/\s/g, '')}_SID${sessionId}`;
    if (isArray(files) && files.length) {
      fileName += `_${files.length}_FILES`;
    }
    fileName += `_${eventType}`;
  }

  return fileName;
};

const extractFiles = (type = 'FILES') => {
  return (dispatch, getState) => {
    const {
      recon,
      recon: {
        header: {
          headerItems
        },
        data: {
          endpointId,
          eventId
        }
      }
    } = getState();

    const selectedFileNames = selectedFiles(recon).map(({ fileName }) => fileName);

    let eventType = 'NETWORK';

    if (isEndpointEvent(recon)) {
      eventType = 'ENDPOINT';
    } else if (isLogEvent(recon)) {
      eventType = 'LOG';
    }

    const filename = createFilename(eventType, headerItems, selectedFileNames);

    dispatch({
      type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE,
      promise: fetchExtractJobId(endpointId, eventId, type, filename, selectedFileNames, eventType),
      meta: {
        onStart() {
          showDownloadInQueueInfo();
        },
        onFailure(response) {
          handleInvestigateErrorCode(response, `FETCH_EXTRACT_JOB_ID; ${endpointId} ${eventId}`);
          displayDownloadError();
        }
      }
    });
  };
};

const didDownloadFiles = () => ({ type: ACTION_TYPES.FILE_EXTRACT_JOB_DOWNLOADED });

const showPacketTooltip = (tootipData) => ({
  type: ACTION_TYPES.SHOW_PACKET_TOOLTIP,
  payload: tootipData
});

const hidePacketTooltip = () => ({ type: ACTION_TYPES.HIDE_PACKET_TOOLTIP });

const highlightMeta = (metaToHighlight) => ({
  type: ACTION_TYPES.TEXT_HIGHLIGHT_META,
  payload: metaToHighlight
});

export {
  deselectAllFiles,
  selectAllFiles,
  fileSelectionChanged,
  extractFiles,
  didDownloadFiles,
  fileSelected,
  highlightMeta,
  showPacketTooltip,
  hidePacketTooltip
};
