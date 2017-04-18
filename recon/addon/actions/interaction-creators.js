import Ember from 'ember';
import * as ACTION_TYPES from './types';
import { fetchExtractJobId } from './fetch';
import { getHeaderItem } from 'recon/utils/recon-event-header';

const {
  A,
  Logger,
  isArray,
  get
} = Ember;

const fileSelected = (fileId) => {
  return {
    type: ACTION_TYPES.FILES_FILE_TOGGLED,
    payload: fileId
  };
};

const deselectAllFiles = () => ({ type: ACTION_TYPES.FILES_SELECT_ALL });

const selectAllFiles = () => ({ type: ACTION_TYPES.FILES_DESELECT_ALL });

const createFilename = (headerItems, files = []) => {
  /*
    If the file name is empty, the service will return a UUID filename.  And
    if we do not have the required paramters to make a file name, it is best
    to allow the service assign an UUID instead of the UI giving an undefined.
  */
  let fileName = '';

  const [ type, service, id, session, device ] =
  ['type', 'service', 'id', 'session', 'device'].map((k) => {
    return get(getHeaderItem(headerItems, k), 'value');
  });

  if (type === 'Network' && device && session) {
    fileName = `${device}_SID${session}`;
    if (isArray(files) && files.length) {
      fileName += `_FC${files.length}`;
    }
  } else if (type === 'Log' && service && id) {
    fileName = `${service}_SID${id}`;
  }

  return fileName;
};

const extractFiles = (type = 'FILES') => {
  return (dispatch, getState) => {
    const {
      recon: {
        data: {
          endpointId,
          eventId,
          files,
          headerItems
        }
      }
    } = getState();

    const selectedFileNames = (files || A([]))
      .filterBy('selected', true)
      .map((file) => file.fileName);

    const filename = createFilename(headerItems, selectedFileNames);

    dispatch({
      type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE,
      promise: fetchExtractJobId(endpointId, eventId, type, filename, selectedFileNames),
      meta: {
        onFailure(response) {
          Logger.error('Error fetching job id for extraction', { endpointId, eventId }, response);
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
  extractFiles,
  didDownloadFiles,
  fileSelected,
  highlightMeta,
  showPacketTooltip,
  hidePacketTooltip
};
